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
}
