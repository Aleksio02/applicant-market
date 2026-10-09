package com.maboy.applicantmarket.employer.controller;

import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import com.maboy.applicantmarket.employer.model.Company;
import com.maboy.applicantmarket.employer.model.request.CreateCompanyRequest;
import com.maboy.applicantmarket.employer.model.request.UpdateCompanyRequest;
import com.maboy.applicantmarket.employer.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Employer / Company", description = "Профиль компании работодателя")
@RestController
@RequestMapping("/api/employer/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "Создать компанию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Компания создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации или у пользователя уже есть компания",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping
    public Company create(@Valid @RequestBody CreateCompanyRequest request,
                          @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return companyService.create(ownerId, request);
    }

    @Operation(summary = "Получить свою компанию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Компания найдена"),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/me")
    public Company getMine(@Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return companyService.getMine(ownerId);
    }

    @Operation(summary = "Обновить компанию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Компания обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Пользователь не является работодателем",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Компания не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PatchMapping("/me")
    public Company update(@Valid @RequestBody UpdateCompanyRequest request,
                          @Parameter(hidden = true) @CurrentUser UUID ownerId) {
        return companyService.update(ownerId, request);
    }
}