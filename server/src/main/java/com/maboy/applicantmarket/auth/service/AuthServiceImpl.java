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
import com.maboy.applicantmarket.commons.exception.*;
import com.maboy.applicantmarket.commons.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Primary
@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final ConsentDao consentDao;
    private final JavaMailSender mailSender;
    private final SessionUtils sessionUtils;
    private final EmailCodeService emailCodeService;

    @Value("${spring.mail.username}")
    private String sourceEmail;

    @Value("${app.consents.data-processing-version}")
    private int dataProcessingVersion;

    @Value("${app.consents.profile-publication-version}")
    private int profilePublicationVersion;

    @Value("${app.consents.contact-reveal-version}")
    private int contactRevealVersion;

    public AuthServiceImpl(UserDao userDao,
                           ConsentDao consentDao,
                           JavaMailSender mailSender,
                           SessionUtils sessionUtils,
                           EmailCodeService emailCodeService) {
        this.userDao = userDao;
        this.consentDao = consentDao;
        this.mailSender = mailSender;
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
    public RegisterResponse register(RegisterRequest request) {
        if (!request.validToRegistration()) {
            throw new IncorrectRequestDataException("You should fill all fields!");
        }
        userDao.findByUsernameOrEmail(request.getLogin(), request.getEmail()).ifPresent((userDto) -> {
            throw new AlreadyExistsException("User with this username or email exists");
        });

        request.setPassword(PasswordEncoder.encode(request.getPassword()));
        UserDto newUser = new UserDto();
        new AuthConverter().toDto(request, newUser);
        newUser = userDao.save(newUser);

        saveConsents(newUser.getId(), request.getAcceptedConsents());

        String code = emailCodeService.generateAndStore(newUser.getId());
        sendCodeToMail(newUser.getEmail(), code);

        return RegisterResponse.builder()
                .userId(newUser.getId())
                .message("Confirmation code sent to email")
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
        RuntimeException sessionHasBeenFinishedException = new UnauthorizedException("This session has been finished");
        try {
            SessionPayload sessionPayload = sessionUtils.getSession(sessionId);
            if (sessionPayload.getExpires().isBefore(Instant.now())) {
                sessionUtils.deleteSession(sessionId);
                throw sessionHasBeenFinishedException;
            }

            UserDto userDto = userDao.findById(sessionPayload.getUserId()).get();
            User user = new User();
            new UserConverter().fromDto(userDto, user);
            sessionPayload.setCurrentUser(user);
            sessionUtils.extendSession(sessionId);

            return sessionPayload;
        } catch (Exception e) {
            throw sessionHasBeenFinishedException;
        }
    }

    @Override
    public void requireEmployer(UUID userId) {
        UserDto user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (user.getRole() != Role.EMPLOYER) {
            throw new AccessForbiddenException("Only employer can perform this action");
        }
    }

    private void saveConsents(UUID userId, List<ConsentType> types) {
        if (types == null || types.isEmpty()) {
            throw new IncorrectRequestDataException("Data processing consent is required");
        }
        if (!types.contains(ConsentType.DATA_PROCESSING)) {
            throw new IncorrectRequestDataException("Data processing consent is required");
        }
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