package com.supportportal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tender {
    private Long id;
    private Source source;
    private String sourceRefNumber;
    private String link;
    private String title;
    private String description;
    private String field;
    private String client;
    private ZonedDateTime date;
    private LocalDate deadline;
    private List<Cpv> cpv;
}