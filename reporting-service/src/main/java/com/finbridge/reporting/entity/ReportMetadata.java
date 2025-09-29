package com.finbridge.reporting.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "report_metadata")
public class ReportMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private String path;

    public ReportMetadata() {}

    public ReportMetadata(LocalDate reportDate, String format, String path) {
        this.reportDate = reportDate;
        this.format = format;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
