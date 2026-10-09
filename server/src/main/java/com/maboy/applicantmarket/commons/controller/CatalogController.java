package com.maboy.applicantmarket.commons.controller;

import com.maboy.applicantmarket.commons.model.Grade;
import com.maboy.applicantmarket.commons.model.Skill;
import com.maboy.applicantmarket.commons.model.Specialization;
import com.maboy.applicantmarket.commons.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Catalog", description = "Публичные справочники: специализации, грейды, навыки")
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Operation(
            summary = "Список специализаций",
            description = """
                    Возвращает все специализации из справочника, отсортированные по имени.
                    
                    Используется фронтом для выпадающих списков в формах вакансий,
                    потребностей в найме и фильтрах поиска.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список специализаций",
                    content = @Content(schema = @Schema(implementation = Specialization.class)))
    })
    @GetMapping("/specializations")
    public List<Specialization> getSpecializations() {
        return catalogService.getSpecializations();
    }

    @Operation(
            summary = "Список грейдов",
            description = """
                    Возвращает все грейды из справочника, отсортированные по уровню
                    (от Junior к Lead).
                    
                    Используется фронтом для выпадающих списков и фильтров.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список грейдов",
                    content = @Content(schema = @Schema(implementation = Grade.class)))
    })
    @GetMapping("/grades")
    public List<Grade> getGrades() {
        return catalogService.getGrades();
    }

    @Operation(
            summary = "Список навыков",
            description = """
                    Возвращает активные навыки из справочника, отсортированные по порядку
                    отображения. Неактивные навыки не попадают в ответ.
                    
                    Используется фронтом для выбора навыков в требованиях к вакансиям
                    и в профиле соискателя.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список навыков",
                    content = @Content(schema = @Schema(implementation = Skill.class)))
    })
    @GetMapping("/skills")
    public List<Skill> getSkills() {
        return catalogService.getSkills();
    }
}