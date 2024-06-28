package com.supportportal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "cpv_db_id" }) })
public class UserCpvNotShowDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @OneToOne
    @JoinColumn(name = "cpv_db_id", referencedColumnName = "id")
    private CpvDb cpvDb;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCpvNotShowDb that = (UserCpvNotShowDb) o;
        return Objects.equals(username, that.username) && Objects.equals(cpvDb, that.cpvDb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, cpvDb);
    }
}