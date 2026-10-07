package com.maboy.applicantmarket.applicant.controller;

import com.maboy.applicantmarket.applicant.converter.FspAchievementConverter;
import com.maboy.applicantmarket.applicant.model.response.FspAchievementResponse;
import com.maboy.applicantmarket.applicant.service.FspService;
import com.maboy.applicantmarket.auth.config.annotation.CurrentUser;
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

@RestController
@RequestMapping("/api/applicant/fsp")
@RequiredArgsConstructor
public class ApplicantFspController {

    private final FspService service;
    private final FspAchievementConverter converter;

    @PostMapping("/link")
    public void link(@CurrentUser UUID userId, @RequestBody Map<String, String> body) {
        service.link(userId, body.get("fspId"));
    }

    @DeleteMapping("/link")
    public void unlink(@CurrentUser UUID userId) {
        service.unlink(userId);
    }

    @GetMapping("/achievements")
    public List<FspAchievementResponse> achievements(@CurrentUser UUID userId) {
        // Пустой список — валидный ответ, не 404.
        return service.listAchievements(userId).stream()
            .map(converter::toResponse).toList();
    }
}