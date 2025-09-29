package com.finbridge.reporting.controller;

import com.finbridge.reporting.entity.ReportMetadata;
import com.finbridge.reporting.repository.ReportMetadataRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Загрузка сгенерированных отчётов")
public class ReportController {

    private final ReportMetadataRepo repo;

    public ReportController(ReportMetadataRepo repo) {
        this.repo = repo;
    }

    @Operation(
            summary = "Скачать отчёт",
            description = "Возвращает отчёт за указанную дату в формате CSV или JSON"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Файл отчёта",
            content = @Content(mediaType = "application/octet-stream")
    )
    @GetMapping("/{date}")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable @Schema(description = "Дата отчёта", example = "2025-09-29") LocalDate date,
            @RequestParam(defaultValue = "csv") @Schema(description = "Формат отчёта", example = "csv") String format
    ) {
        ReportMetadata meta = repo.findByReportDateAndFormat(date, format)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        Resource file = new FileSystemResource(meta.getPath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"report-" + date + "." + format + "\"")
                .body(file);
    }
}
