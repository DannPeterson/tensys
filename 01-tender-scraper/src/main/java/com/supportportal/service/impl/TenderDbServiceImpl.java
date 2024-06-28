package com.supportportal.service.impl;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.TenderDb;
import com.supportportal.domain.UserCpvNotShowDb;
import com.supportportal.domain.UserCpvShowDb;
import com.supportportal.repository.CpvDbRepository;
import com.supportportal.repository.TenderDbRepository;
import com.supportportal.repository.UserCpvNotShowRepository;
import com.supportportal.repository.UserCpvShowRepository;
import com.supportportal.service.TenderDbService;
import com.supportportal.utility.TenderTranslate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TenderDbServiceImpl implements TenderDbService {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private TenderDbRepository tenderDbRepository;
    private UserCpvNotShowRepository userCpvNotShowRepository;
    private UserCpvShowRepository userCpvShowRepository;
    private CpvDbRepository cpvDbRepository;

    @Autowired
    public TenderDbServiceImpl(TenderDbRepository tenderDbRepository,
                               UserCpvNotShowRepository userCpvNotShowRepository,
                               UserCpvShowRepository userCpvShowRepository,
                               CpvDbRepository cpvDbRepository) {
        this.tenderDbRepository = tenderDbRepository;
        this.userCpvNotShowRepository = userCpvNotShowRepository;
        this.userCpvShowRepository = userCpvShowRepository;
        this.cpvDbRepository = cpvDbRepository;
    }

    @Override
    public List<TenderDb> getTendersDb() {
        return tenderDbRepository.findAll();
    }

    @Override
    public TenderDb addNewTenderDb(TenderDb tenderDb) {
        if (tenderDb.getCpvDb() != null) {
            List<CpvDb> dbCpvDbs = new ArrayList<>();
            for (CpvDb cpv : tenderDb.getCpvDb()) {
                System.out.println("*" + cpv.getCode());
                CpvDb cpvDb = cpvDbRepository.findCpvDbByCodeContains(cpv.getCode());
                dbCpvDbs.add(cpvDb);
            }
            tenderDb.setCpvDb(dbCpvDbs);
        }
        tenderDbRepository.save(tenderDb);
        return tenderDb;
    }

    @Override
    public List<TenderDb> getTendersDbWithNotShowCpvFilterApplied(String username) {
        List<TenderDb> tenderDbs = tenderDbRepository.findAll();
        return applyNotShowCpvFilters(username, tenderDbs);
    }

    @Override
    public List<TenderDb> getTendersDbWithShowCpvFilterApplied(String username) {
        List<TenderDb> tenderDbs = tenderDbRepository.findAll();
        return applyShowCpvFilters(username, tenderDbs);
    }

    @Override
    public List<TenderDb> findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(LocalDateTime start, LocalDateTime end) {
        return tenderDbRepository.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(start, end);
    }


    @Override
    public List<TenderDb> getListShowCpvFilteredForPeriod(String username, LocalDateTime start, LocalDateTime end) {
        List<TenderDb> tenderDbs = tenderDbRepository.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(start, end);
        return applyShowCpvFilters(username, tenderDbs);
    }

    @Override
    public List<TenderDb> getListNotShowCpvFilteredForPeriod(String username, LocalDateTime start, LocalDateTime end) {
        List<TenderDb> tenderDbs = findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(start, end);
        return applyNotShowCpvFilters(username, tenderDbs);
    }

    @Override
    public TenderDb getTenderDbById(Long id) {
        return tenderDbRepository.getTenderDbById(id);
    }

    @Override
    public List<TenderDb> getTendersDbBySourceRefNumber(String sourceRefNumber) {
        return tenderDbRepository.getTendersDbBySourceRefNumber(sourceRefNumber);
    }

    @Override
    public List<TenderDb> getTendersDbByLink(String link) {
        return tenderDbRepository.getTendersDbByLink(link);
    }

    @Override
    public void removeTenderDbById(Long id) {
        tenderDbRepository.removeTenderDbById(id);
    }

    private List<TenderDb> applyShowCpvFilters(String username, List<TenderDb> tenderDbs) {
        List<UserCpvShowDb> userCpvShowDbs = userCpvShowRepository.findAllByUsername(username);
        List<CpvDb> cpvDbShows = new ArrayList<>();
        for (UserCpvShowDb u : userCpvShowDbs) {
            cpvDbShows.add(u.getCpvDb());
        }

        List<TenderDb> result = new ArrayList<>();

        for (TenderDb tenderDb : tenderDbs) {
            List<CpvDb> cpvDbs = tenderDb.getCpvDb();
            boolean hasShowCpv = false;
            for (CpvDb tenderCpvDb : cpvDbs) {
                for (CpvDb filterCpvDb : cpvDbShows) {
                    if (tenderCpvDb.equals(filterCpvDb)) {
                        hasShowCpv = true;
                        continue;
                    }
                    if (filterCpvDb.getCode().contains("*")) {
                        String customCpvFilter = filterCpvDb.getCode().split("\\*")[0];
                        if (tenderCpvDb.getCode().startsWith(customCpvFilter)) {
                            hasShowCpv = true;
                            continue;
                        }
                    }
                }
            }
            if (hasShowCpv) result.add(tenderDb);
        }
        return result;
    }

    private List<TenderDb> applyNotShowCpvFilters(String username, List<TenderDb> tenderDbs) {
        List<UserCpvNotShowDb> userCpvNotShowDbs = userCpvNotShowRepository.findAllByUsername(username);
        List<CpvDb> cpvDbNotShows = new ArrayList<>();
        for (UserCpvNotShowDb u : userCpvNotShowDbs) {
            cpvDbNotShows.add(u.getCpvDb());
            System.out.println(u.getCpvDb().getCode());
        }

        List<TenderDb> result = new ArrayList<>();

        for (TenderDb tenderDb : tenderDbs) {
            List<CpvDb> cpvDbs = tenderDb.getCpvDb();
            int isFiltered = 0;

            for (CpvDb tenderCpvDb : cpvDbs) {
                for (CpvDb filterCpvDb : cpvDbNotShows) {
                    if (tenderCpvDb.equals(filterCpvDb)) {
                        isFiltered++;
                    }
                    if (filterCpvDb.getCode().contains("*")) {
                        String customCpvFilter = filterCpvDb.getCode().split("\\*")[0];
                        System.out.println("CUSTOM FILTER " + customCpvFilter);
                        if (tenderCpvDb.getCode().startsWith(customCpvFilter)) {
                            isFiltered++;
                        }
                    }
                }
            }
            if (isFiltered < cpvDbs.size()) {
                result.add(tenderDb);
            }
        }
        return result;
    }
}