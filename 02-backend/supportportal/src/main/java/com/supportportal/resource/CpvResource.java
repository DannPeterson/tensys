package com.supportportal.resource;

import com.supportportal.domain.Cpv;
import com.supportportal.domain.CpvDb;
import com.supportportal.service.CpvDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/cpv"})
public class CpvResource {
    private CpvDbService cpvDbService;

    @Autowired
    public CpvResource(CpvDbService cpvDbService) {
        this.cpvDbService = cpvDbService;
    }

    @GetMapping("/{code}/{lang}")
    public ResponseEntity<List<Cpv>> getByCodeStartsWith(@PathVariable("code") String code,
                                                         @PathVariable("lang") String language) {
        List<CpvDb> cpvDbs = cpvDbService.findAllByCodeStartsWith(code);
        List<Cpv> result = new ArrayList<>();

        for (CpvDb cpvDb : cpvDbs) {
            if(!cpvDb.getCode().contains("*")) {
                result.add(getCpvTranslated(cpvDb, language));
            }
        }
        return new ResponseEntity<>(result, OK);
    }

    private Cpv getCpvTranslated(CpvDb cpvDb, String language) {
        Cpv result = new Cpv();
        result.setId(cpvDb.getId());
        result.setCode(cpvDb.getCode());

        switch (language) {
            case "et":
                result.setDescription(cpvDb.getDescriptionEe());
                break;
            case "lv":
                result.setDescription(cpvDb.getDescriptionLv());
                break;
            case "lt":
                result.setDescription(cpvDb.getDescriptionLt());
                break;
            case "ru":
                result.setDescription(cpvDb.getDescriptionRu());
                break;
            default:
                result.setDescription(cpvDb.getDescriptionEn());
        }
        return result;
    }
}