package com.supportportal.repository;

import com.supportportal.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {
    Source findByName(String name);
    Source findSourceById(Long id);
}
