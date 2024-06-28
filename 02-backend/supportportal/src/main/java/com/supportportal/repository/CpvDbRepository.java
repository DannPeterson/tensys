package com.supportportal.repository;

import com.supportportal.domain.CpvDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CpvDbRepository extends JpaRepository<CpvDb, Long> {
    List<CpvDb> findAllByCodeContains(String code);
    CpvDb findCpvDbByCode(String code);
    CpvDb findCpvDbByCodeContains(String code);
    List<CpvDb> findAllByCodeStartsWith(String code);

    List<CpvDb> findAllByDescriptionContains(String text);
    List<CpvDb> findAllByDescriptionEeContains(String text);
    List<CpvDb> findAllByDescriptionEnContains(String text);
    List<CpvDb> findAllByDescriptionLvContains(String text);
    List<CpvDb> findAllByDescriptionLtContains(String text);
    List<CpvDb> findAllByDescriptionRuContains(String text);

    CpvDb getById(Long id);
    void deleteById(Long id);
}
