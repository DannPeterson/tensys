package com.supportportal.service;

import com.supportportal.domain.UserCpvShowDb;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericShowCpvAddedException;

import java.util.List;

public interface UserCpvShowService {

    List<UserCpvShowDb> findAllByUsername(String username);

    UserCpvShowDb save(UserCpvShowDb cpvShow, String lang) throws CpvAlreadyAddedException, MoreGenericShowCpvAddedException;

    List<UserCpvShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId);

    void deleteById(Long id);

    UserCpvShowDb getById(Long id);
}
