package com.finbridge.reporting.repository;

import com.finbridge.reporting.entity.ReportMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ReportMetadataRepo extends JpaRepository<ReportMetadata, Long> {
    Optional<ReportMetadata> findByReportDateAndFormat(LocalDate reportDate, String format);
}

