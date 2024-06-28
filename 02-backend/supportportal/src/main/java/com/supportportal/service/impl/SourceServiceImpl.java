package com.supportportal.service.impl;

import com.supportportal.domain.Source;
import com.supportportal.repository.SourceRepository;
import com.supportportal.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SourceServiceImpl implements SourceService {
    private SourceRepository sourceRepository;

    @Autowired
    public SourceServiceImpl(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Override
    public List<Source> getAll() {
        return sourceRepository.findAll();
    }

    @Override
    public Source findByName(String name) {
        return sourceRepository.findByName(name);
    }

    @Override
    public Source save(Source source) {
        return sourceRepository.save(source);
    }

    @Override
    public Source findSourceById(Long id) {
        return sourceRepository.findSourceById(id);
    }
}
