package com.supportportal.repository;

import com.supportportal.domain.TenderDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface TenderDbRepository extends JpaRepository<TenderDb, Long> {

    List<TenderDb> findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(LocalDateTime start, LocalDateTime end);

    TenderDb getTenderDbById(Long id);

    List<TenderDb> getTendersDbBySourceRefNumber(String sourceRefNumber);

    List<TenderDb> getTendersDbByLink(String link);

    void removeTenderDbById(Long id);

    List<TenderDb> removeTenderDbsByDeadlineBefore(LocalDate date);
}
