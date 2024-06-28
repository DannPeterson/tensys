package com.supportportal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CpvDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;

    private String description;
    private String descriptionEe;
    private String descriptionLv;
    private String descriptionLt;
    private String descriptionRu;
    private String descriptionEn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CpvDb cpvDb = (CpvDb) o;
        return Objects.equals(id, cpvDb.id) && Objects.equals(code, cpvDb.code) && Objects.equals(description, cpvDb.description) && Objects.equals(descriptionEe, cpvDb.descriptionEe) && Objects.equals(descriptionLv, cpvDb.descriptionLv) && Objects.equals(descriptionLt, cpvDb.descriptionLt) && Objects.equals(descriptionRu, cpvDb.descriptionRu) && Objects.equals(descriptionEn, cpvDb.descriptionEn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, description, descriptionEe, descriptionLv, descriptionLt, descriptionRu, descriptionEn);
    }
}