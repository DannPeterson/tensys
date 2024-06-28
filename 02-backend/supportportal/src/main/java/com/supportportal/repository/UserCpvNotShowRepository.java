package com.supportportal.repository;

import com.supportportal.domain.UserCpvNotShowDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCpvNotShowRepository extends JpaRepository<UserCpvNotShowDb, Long> {

    List<UserCpvNotShowDb> findAllByUsernameOrderByCpvDbIdAsc(String username);

    List<UserCpvNotShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId);

    void deleteById(Long id);

    UserCpvNotShowDb getById(Long id);

}