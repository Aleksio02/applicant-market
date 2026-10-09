package com.maboy.applicantmarket.auth.model.request;

import com.maboy.applicantmarket.commons.model.ConsentType;
import com.maboy.applicantmarket.commons.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Login must not be empty")
    private String login;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be empty")
    private String email;

    @Size(min = 8, max = 32, message = "Password should be between 8 and 32 characters")
    @NotBlank(message = "Password must not be empty")
    private String password;

    @Schema(description = "Роль пользователя: APPLICANT — соискатель, EMPLOYER — работодатель",
            example = "APPLICANT")
    @NotNull(message = "Role must not be null")
    private Role role;

    @Schema(description = "Список принятых согласий. DATA_PROCESSING обязателен. " +
            "PROFILE_PUBLICATION и CONTACT_REVEAL опциональны на этапе регистрации.",
            example = "[\"DATA_PROCESSING\"]")
    private List<ConsentType> acceptedConsents;

    public boolean validToRegistration() {
        return login != null && email != null && password != null && role != null;
    }
}