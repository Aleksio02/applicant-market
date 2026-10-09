package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.FspAchievementConverter;
import com.maboy.applicantmarket.applicant.model.response.FspAchievementResponse;
import com.maboy.applicantmarket.applicant.service.FspService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
import com.maboy.applicantmarket.commons.model.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Applicant", description = "Профиль соискателя")
@RestController
@RequestMapping("/api/applicant/fsp")
@RequiredArgsConstructor
public class ApplicantFspController {

    private final FspService service;
    private final FspAchievementConverter converter;

    @Operation(
            summary = "Привязать ФСП ID",
            description = """
                    Привязывает ID участника ФСП к профилю соискателя.
                    Один ФСП ID может быть привязан только к одному профилю.
                    Тело запроса: {"fspId": "..."}
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ФСП ID привязан"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "ФСП ID уже привязан к другому профилю",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @PostMapping("/link")
    public void link(
            @Parameter(hidden = true) @CurrentUser UUID userId,
            @RequestBody Map<String, String> body
    ) {
        service.link(userId, body.get("fspId"));
    }

    @Operation(
            summary = "Отвязать ФСП ID",
            description = "Убирает привязку ФСП ID от профиля."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ФСП ID отвязан"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @DeleteMapping("/link")
    public void unlink(@Parameter(hidden = true) @CurrentUser UUID userId) {
        service.unlink(userId);
    }

    @Operation(
            summary = "Достижения ФСП",
            description = """
                    Возвращает список достижений участника ФСП.
                    Пустой список — валидный случай: у кандидата может не быть истории ФСП.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "404", description = "Профиль не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "sessionCookie")
    @GetMapping("/achievements")
    public List<FspAchievementResponse> achievements(
            @Parameter(hidden = true) @CurrentUser UUID userId
    ) {
        return service.listAchievements(userId).stream()
                .map(converter::toResponse).toList();
    }
}