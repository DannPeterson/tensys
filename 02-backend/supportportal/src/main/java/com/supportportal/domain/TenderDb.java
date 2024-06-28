package com.supportportal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenderDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private Source source;
    private String sourceRefNumber;
    private String link;

    @Column(length = 500)
    private String title;
    @Column(length = 500)
    private String titleEe;
    @Column(length = 500)
    private String titleLv;
    @Column(length = 500)
    private String titleLt;
    @Column(length = 500)
    private String titleRu;
    @Column(length = 500)
    private String titleEn;

    @Column(columnDefinition="TEXT")
    private String description;
    @Column(columnDefinition="TEXT")
    private String descriptionEe;
    @Column(columnDefinition="TEXT")
    private String descriptionLv;
    @Column(columnDefinition="TEXT")
    private String descriptionLt;
    @Column(columnDefinition="TEXT")
    private String descriptionRu;
    @Column(columnDefinition="TEXT")
    private String descriptionEn;

    private String field;
    private String fieldEe;
    private String fieldLv;
    private String fieldLt;
    private String fieldRu;
    private String fieldEn;

    private String client;

    @Column(name = "date", columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
    private LocalDate deadline;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<CpvDb> cpvDb;
}