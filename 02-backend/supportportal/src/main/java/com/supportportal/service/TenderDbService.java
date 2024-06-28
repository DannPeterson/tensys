package com.supportportal.service;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.TenderDb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TenderDbService {

    List<TenderDb> getTendersDb();

    TenderDb addNewTenderDb(TenderDb tenderDb);

    List<TenderDb> getTendersDbWithNotShowCpvFilterApplied(String username);

    List<TenderDb> getTendersDbWithShowCpvFilterApplied(String username);

    List<TenderDb> findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(LocalDateTime start, LocalDateTime end);

    List<TenderDb> getListShowCpvFilteredForPeriod(String username, LocalDateTime start, LocalDateTime end);

    List<TenderDb> getListNotShowCpvFilteredForPeriod(String username, LocalDateTime start, LocalDateTime end);

    TenderDb getTenderDbById(Long id);

    List<TenderDb> getTendersDbBySourceRefNumber(String sourceRefNumber);

    List<TenderDb> getTendersDbByLink(String link);

    void removeTenderDbById(Long id);

    List<TenderDb> removeTenderDbsByDeadlineBefore(LocalDate date);

}
