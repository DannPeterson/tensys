package com.supportportal.service;

import com.supportportal.domain.Source;

import java.util.List;

public interface SourceService {
    List<Source> getAll();

    Source findByName(String name);

    Source save(Source source);

    Source findSourceById(Long id);
}