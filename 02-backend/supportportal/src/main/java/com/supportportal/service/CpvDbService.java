package com.supportportal.service;

import com.supportportal.domain.CpvDb;

import java.util.List;

public interface CpvDbService {

    CpvDb save(CpvDb cpvDb);

    CpvDb getById(Long id);

    void deleteById(Long id);

    CpvDb findCpvDbByCode(String code);

    List<CpvDb> findAllByCodeStartsWith(String code);

    List<CpvDb> findAllByCodeContains(String code);

    CpvDb findCpvDbByCodeContains(String code);

    List<CpvDb> findAllByDescriptionContains(String text);
    List<CpvDb> findAllByDescriptionEeContains(String text);
    List<CpvDb> findAllByDescriptionEnContains(String text);
    List<CpvDb> findAllByDescriptionLvContains(String text);
    List<CpvDb> findAllByDescriptionLtContains(String text);
    List<CpvDb> findAllByDescriptionRuContains(String text);
}
