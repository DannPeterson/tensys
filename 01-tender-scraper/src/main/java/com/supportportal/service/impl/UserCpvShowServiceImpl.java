package com.supportportal.service.impl;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.UserCpvNotShowDb;
import com.supportportal.domain.UserCpvShowDb;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericShowCpvAddedException;
import com.supportportal.repository.UserCpvShowRepository;
import com.supportportal.service.CpvDbService;
import com.supportportal.service.UserCpvShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.supportportal.constant.ExceptionConstant.*;

@Service
@Transactional
public class UserCpvShowServiceImpl implements UserCpvShowService {
    private UserCpvShowRepository userCpvShowRepository;
    private CpvDbService cpvDbService;

    @Autowired
    public UserCpvShowServiceImpl(UserCpvShowRepository userCpvShowRepository, CpvDbService cpvDbService) {
        this.userCpvShowRepository = userCpvShowRepository;
        this.cpvDbService = cpvDbService;
    }

    @Override
    public List<UserCpvShowDb> findAllByUsername(String username) {
        return userCpvShowRepository.findAllByUsername(username);
    }

    @Override
    public UserCpvShowDb save(UserCpvShowDb cpvShow, String lang) throws CpvAlreadyAddedException, MoreGenericShowCpvAddedException {
        List<UserCpvShowDb> list = userCpvShowRepository.findAllByUsername(cpvShow.getUsername());
        for(UserCpvShowDb userCpvShowDb : list) {
            if(userCpvShowDb.getCpvDb().getCode().contains("*") && cpvShow.getCpvDb().getCode().startsWith(userCpvShowDb.getCpvDb().getCode().split("\\*")[0])){
                throw new MoreGenericShowCpvAddedException(moreGenericCpvAlreadyAddedConst(lang)  + ": '" + userCpvShowDb.getCpvDb().getCode() + " - " + userCpvShowDb.getCpvDb().getDescription() + "'");
            }
        }
        if(findAllByUsernameAndCpvDbId(cpvShow.getUsername(), cpvShow.getCpvDb().getId()).size() > 0) {
            throw new CpvAlreadyAddedException(alreadyHaveCpvConst(lang));
        }
        cpvShow.getCpvDb().getCode().trim();
        cpvShow.getCpvDb().getDescription().trim();
        return userCpvShowRepository.save(cpvShow);
    }

    @Override
    public List<UserCpvShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId) {
        return userCpvShowRepository.findAllByUsernameAndCpvDbId(username, cpvId);
    }

    @Override
    public void deleteById(Long id) {
        UserCpvShowDb userCpvShowDb = userCpvShowRepository.getById(id);
        if(userCpvShowDb.getCpvDb().getCode().contains("*")) {
            CpvDb cpvDb = userCpvShowDb.getCpvDb();
            cpvDbService.deleteById(cpvDb.getId());
        }
        userCpvShowRepository.deleteById(id);
    }

    @Override
    public UserCpvShowDb getById(Long id) {
        return userCpvShowRepository.getById(id);
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
