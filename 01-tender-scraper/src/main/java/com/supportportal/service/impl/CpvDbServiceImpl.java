package com.supportportal.service.impl;

import com.supportportal.domain.CpvDb;
import com.supportportal.repository.CpvDbRepository;
import com.supportportal.service.CpvDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CpvDbServiceImpl implements CpvDbService {
    private CpvDbRepository cpvDbRepository;

    @Autowired
    public CpvDbServiceImpl(CpvDbRepository cpvDbRepository) {
        this.cpvDbRepository = cpvDbRepository;
    }

    @Override
    public CpvDb save(CpvDb cpvDb) {
        return cpvDbRepository.save(cpvDb);
    }

    @Override
    public CpvDb getById(Long id) {
        return cpvDbRepository.getById(id);
    }

    @Override
    public void deleteById(Long id) {
        cpvDbRepository.deleteById(id);
    }


    @Override
    public CpvDb findCpvDbByCode(String code) {
        return cpvDbRepository.findCpvDbByCode(code);
    }

    @Override
    public List<CpvDb> findAllByCodeStartsWith(String code) {
        return cpvDbRepository.findAllByCodeStartsWith(code);
    }

    @Override
    public List<CpvDb> findAllByCodeContains(String code) {
        return cpvDbRepository.findAllByCodeContains(code);
    }

    @Override
    public CpvDb findCpvDbByCodeContains(String code) {
        return cpvDbRepository.findCpvDbByCodeContains(code);
    }
}
