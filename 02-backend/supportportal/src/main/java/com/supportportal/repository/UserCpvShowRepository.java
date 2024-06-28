package com.supportportal.repository;

import com.supportportal.domain.UserCpvShowDb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCpvShowRepository extends JpaRepository<UserCpvShowDb, Long> {
    List<UserCpvShowDb> findAllByUsernameOrderByCpvDbIdAsc(String username);

    List<UserCpvShowDb> findAllByUsernameAndCpvDbId(String username, Long cpvId);

    void deleteById(Long id);

    UserCpvShowDb getById(Long id);
}
