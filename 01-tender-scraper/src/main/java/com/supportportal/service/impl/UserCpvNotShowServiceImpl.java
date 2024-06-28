package com.supportportal.service.impl;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.UserCpvNotShowDb;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericNotShowCpvAddedException;
import com.supportportal.repository.UserCpvNotShowRepository;
import com.supportportal.service.CpvDbService;
import com.supportportal.service.UserCpvNotShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.supportportal.constant.ExceptionConstant.*;

@Service
@Transactional
public class UserCpvNotShowServiceImpl implements UserCpvNotShowService {
    private UserCpvNotShowRepository userCpvNotShowRepository;
    private CpvDbService cpvDbService;
    public static final Long CPV_TOTAL = 9454L;

    @Autowired
    public UserCpvNotShowServiceImpl(UserCpvNotShowRepository userCpvNotShowRepository, CpvDbService cpvDbService){
        this.userCpvNotShowRepository = userCpvNotShowRepository;
        this.cpvDbService = cpvDbService;
    }

    @Override
    public List<UserCpvNotShowDb> findAllByUsername(String username) {
        return userCpvNotShowRepository.findAllByUsername(username);
    }

    @Override
    public UserCpvNotShowDb save(UserCpvNotShowDb cpvNotShow, String lang) throws CpvAlreadyAddedException, MoreGenericNotShowCpvAddedException {
        List<UserCpvNotShowDb> list = userCpvNotShowRepository.findAllByUsername(cpvNotShow.getUsername());
        for(UserCpvNotShowDb userCpvNotShowDb : list) {
            if(userCpvNotShowDb.getCpvDb().getCode().contains("*") && cpvNotShow.getCpvDb().getCode().startsWith(userCpvNotShowDb.getCpvDb().getCode().split("\\*")[0])){
                throw new MoreGenericNotShowCpvAddedException(moreGenericCpvAlreadyAddedConst(lang) + ": '" + userCpvNotShowDb.getCpvDb().getCode() + " - " + userCpvNotShowDb.getCpvDb().getDescription() + "'");
            }
        }
        if(userCpvNotShowRepository.findAllByUsernameAndCpvDbId(cpvNotShow.getUsername(), cpvNotShow.getCpvDb().getId()).size() > 0) {
            throw new CpvAlreadyAddedException(alreadyHaveCpvConst(lang));
        }
        cpvNotShow.getCpvDb().getCode().trim();
        cpvNotShow.getCpvDb().getDescription().trim();
        return userCpvNotShowRepository.save(cpvNotShow);
    }

    @Override
    public List<UserCpvNotShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId) {
        return userCpvNotShowRepository.findAllByUsernameAndCpvDbId(username, cpvId);
    }

    @Override
    public UserCpvNotShowDb getById(Long id) {
        return userCpvNotShowRepository.getById(id);
    }

    @Override
    public void deleteById(Long id) {
        UserCpvNotShowDb userCpvNotShowDb = userCpvNotShowRepository.getById(id);
        if(userCpvNotShowDb.getCpvDb().getId() > CPV_TOTAL) {
            CpvDb cpvDb = userCpvNotShowDb.getCpvDb();
            cpvDbService.deleteById(cpvDb.getId());
        }
        userCpvNotShowRepository.deleteById(id);
    }

    private String alreadyHaveCpvConst(String language) {
        switch (language) {
            case "et": return CPV_FILTER_ALREADY_ADDED_ET;
            case "lt": return CPV_FILTER_ALREADY_ADDED_LT;
            case "lv": return CPV_FILTER_ALREADY_ADDED_LV;
            case "ru": return CPV_FILTER_ALREADY_ADDED_RU;
            default: return CPV_FILTER_ALREADY_ADDED_EN;
        }
    }

    private String moreGenericCpvAlreadyAddedConst(String language){
        switch (language) {
            case "et": return MORE_GENERIC_CPV_ALREADY_ADDED_ET;
            case "lt": return MORE_GENERIC_CPV_ALREADY_ADDED_LT;
            case "lv": return MORE_GENERIC_CPV_ALREADY_ADDED_LV;
            case "ru": return MORE_GENERIC_CPV_ALREADY_ADDED_RU;
            default: return MORE_GENERIC_CPV_ALREADY_ADDED_EN;
        }
    }
}