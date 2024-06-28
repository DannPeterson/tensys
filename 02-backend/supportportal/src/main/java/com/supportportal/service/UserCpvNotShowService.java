package com.supportportal.service;

import com.supportportal.domain.UserCpvNotShowDb;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericNotShowCpvAddedException;

import java.util.List;

public interface UserCpvNotShowService {

    List<UserCpvNotShowDb> findAllByUsernameOrderByCpvDbIdAsc(String username);

    UserCpvNotShowDb save(UserCpvNotShowDb cpvNotShow, String lang) throws CpvAlreadyAddedException, MoreGenericNotShowCpvAddedException;

    List<UserCpvNotShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId);

    UserCpvNotShowDb getById(Long id);

    void deleteById(Long id);
}
