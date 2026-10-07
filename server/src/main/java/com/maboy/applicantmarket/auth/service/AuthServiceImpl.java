package com.maboy.applicantmarket.auth.service;

import com.maboy.applicantmarket.auth.converter.AuthConverter;
import com.maboy.applicantmarket.auth.hash.PasswordEncoder;
import com.maboy.applicantmarket.auth.model.request.AuthorizationRequest;
import com.maboy.applicantmarket.auth.model.request.ConfirmEmailRequest;
import com.maboy.applicantmarket.auth.model.request.RegisterRequest;
import com.maboy.applicantmarket.auth.model.response.AuthResponse;
import com.maboy.applicantmarket.auth.model.response.RegisterResponse;
import com.maboy.applicantmarket.auth.utils.SessionUtils;
import com.maboy.applicantmarket.commons.converter.UserConverter;
import com.maboy.applicantmarket.commons.dao.ConsentDao;
import com.maboy.applicantmarket.commons.dao.UserDao;
import com.maboy.applicantmarket.commons.dao.dto.ConsentDto;
import com.maboy.applicantmarket.commons.dao.dto.UserDto;
import com.maboy.applicantmarket.commons.exception.AccessForbiddenException;
import com.maboy.applicantmarket.commons.exception.AlreadyExistsException;
import com.maboy.applicantmarket.commons.exception.IncorrectRequestDataException;
import com.maboy.applicantmarket.commons.exception.NotFoundException;
import com.maboy.applicantmarket.commons.exception.UnauthorizedException;
import com.maboy.applicantmarket.commons.model.ConsentType;
import com.maboy.applicantmarket.commons.model.Role;
import com.maboy.applicantmarket.commons.model.SessionPayload;
import com.maboy.applicantmarket.commons.model.User;
import com.maboy.applicantmarket.commons.model.UserStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final ConsentDao consentDao;
    private final SessionUtils sessionUtils;
    private final EmailCodeService emailCodeService;

    @Value("${app.auth.email-confirmation.enabled}")
    private boolean emailConfirmationEnabled;

    @Value("${app.consents.data-processing-version}")
    private int dataProcessingVersion;

    @Value("${app.consents.profile-publication-version}")
    private int profilePublicationVersion;

    @Value("${app.consents.contact-reveal-version}")
    private int contactRevealVersion;

    public AuthServiceImpl(UserDao userDao,
                           ConsentDao consentDao,
                           SessionUtils sessionUtils,
                           EmailCodeService emailCodeService) {
        this.userDao = userDao;
        this.consentDao = consentDao;
        this.sessionUtils = sessionUtils;
        this.emailCodeService = emailCodeService;
    }

    @Override
    public AuthResponse login(AuthorizationRequest request) {
        String login = request.getLogin();
        String password = request.getPassword();

        UserDto userDto = userDao.findByUsernameOrEmail(login)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!PasswordEncoder.matches(password, userDto.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (userDto.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Email is not confirmed");
        }

        User user = new User();
        new UserConverter().fromDto(userDto, user);

        String token = sessionUtils.createSession(userDto.getId());
        return new AuthResponse(token, user);
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (!request.validToRegistration()) {
            throw new IncorrectRequestDataException("You should fill all fields!");
        }

        validateConsents(request.getAcceptedConsents());

        userDao.findByUsernameOrEmail(request.getLogin(), request.getEmail()).ifPresent((userDto) -> {
            throw new AlreadyExistsException("User with this username or email exists");
        });

        request.setPassword(PasswordEncoder.encode(request.getPassword()));
        UserDto newUser = new UserDto();
        new AuthConverter().toDto(request, newUser);

        if (!emailConfirmationEnabled) {
            newUser.setStatus(UserStatus.ACTIVE);
            newUser.setEmailVerifiedAt(Instant.now());
        }

        newUser = userDao.save(newUser);

        saveConsents(newUser.getId(), request.getAcceptedConsents());

        if (emailConfirmationEnabled) {
            String code = emailCodeService.generateAndStore(newUser.getId());
            sendCodeToMail(newUser.getEmail(), code);
        }

        return RegisterResponse.builder()
                .userId(newUser.getId())
                .message(emailConfirmationEnabled
                        ? "Confirmation code sent to email"
                        : "Registration successful")
                .build();
    }

    @Override
    public AuthResponse confirmEmail(ConfirmEmailRequest request) {
        UserDto userDto = userDao.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (userDto.getStatus() == UserStatus.ACTIVE) {
            throw new IncorrectRequestDataException("Email is already confirmed");
        }

        if (!emailCodeService.verify(request.getUserId(), request.getCode())) {
            throw new IncorrectRequestDataException("Invalid or expired confirmation code");
        }

        userDto.setStatus(UserStatus.ACTIVE);
        userDto.setEmailVerifiedAt(Instant.now());
        userDto = userDao.save(userDto);

        User user = new User();
        new UserConverter().fromDto(userDto, user);

        String sessionId = sessionUtils.createSession(userDto.getId());
        return new AuthResponse(sessionId, user);
    }

    @Override
    public SessionPayload validateSession(String sessionId) {
        SessionPayload sessionPayload;
        try {
            sessionPayload = sessionUtils.getSession(sessionId);
        } catch (Exception e) {
            throw new UnauthorizedException("This session has been finished");
        }

        if (sessionPayload == null || sessionPayload.getExpires().isBefore(Instant.now())) {
            sessionUtils.deleteSession(sessionId);
            throw new UnauthorizedException("This session has been finished");
        }

        UserDto userDto = userDao.findById(sessionPayload.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        User user = new User();
        new UserConverter().fromDto(userDto, user);
        sessionPayload.setCurrentUser(user);
        sessionUtils.extendSession(sessionId);

        return sessionPayload;
    }

    @Override
    public void requireEmployer(UUID userId) {
        UserDto user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new AccessForbiddenException("Only employer can perform this action");
        }
    }

    private void validateConsents(List<ConsentType> types) {
        if (types == null || types.isEmpty() || !types.contains(ConsentType.DATA_PROCESSING)) {
            throw new IncorrectRequestDataException("Data processing consent is required");
        }
    }

    private void saveConsents(UUID userId, List<ConsentType> types) {
        for (ConsentType type : types) {
            ConsentDto consent = new ConsentDto();
            consent.setUserId(userId);
            consent.setType(type);
            consent.setVersion(versionFor(type));
            consent.setAcceptedAt(Instant.now());
            consentDao.save(consent);
        }
    }

    private int versionFor(ConsentType type) {
        return switch (type) {
            case DATA_PROCESSING -> dataProcessingVersion;
            case PROFILE_PUBLICATION -> profilePublicationVersion;
            case CONTACT_REVEAL -> contactRevealVersion;
        };
    }

    @Async
    protected void sendCodeToMail(String receiver, String code) {
        System.out.println("Confirmation code for " + receiver + ": " + code);
    }
}