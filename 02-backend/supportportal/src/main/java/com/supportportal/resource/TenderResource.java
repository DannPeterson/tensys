package com.supportportal.resource;

import com.supportportal.domain.Cpv;
import com.supportportal.domain.CpvDb;
import com.supportportal.domain.Tender;
import com.supportportal.domain.TenderDb;
import com.supportportal.service.TenderDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/tender"})
public class TenderResource {
    private TenderDbService tenderDbService;
    private Clock clock;

    @Autowired
    public TenderResource(TenderDbService tenderDbService) {
        this.tenderDbService = tenderDbService;
        clock = Clock.system(ZoneId.of("Europe/Tallinn"));
    }

    @GetMapping("/original/{id}")
    public ResponseEntity<Tender> getByIdOriginal(@PathVariable("id") Long id) {
        TenderDb tenderDb = tenderDbService.getTenderDbById(id);
        Tender tender = getTenderOriginal(tenderDb);
        return new ResponseEntity<>(tender, OK);
    }

    @GetMapping("/listforperiod/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<List<Tender>> getTendersDbForPeriod(@PathVariable("username") String username,
                                                              @PathVariable("start")
                                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                              @PathVariable("end")
                                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                              @PathVariable("lang") String lang,
                                                              @PathVariable("sourceIds") List<Long> sourceIds) {
        List<TenderDb> list = tenderDbService.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        list = getTendersWithSources(list, sourceIds);
        List<Tender> result = new ArrayList<>();

        for (TenderDb tenderDb : list) {
            result.add(getTenderTranslated(tenderDb, lang));
        }
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/listforperiod-size/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<Integer> getTendersDbForPeriodSize(@PathVariable("username") String username,
                                                             @PathVariable("start")
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                             @PathVariable("end")
                                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                             @PathVariable("lang") String lang,
                                                             @PathVariable("sourceIds") List<Long> sourceIds){
        List<TenderDb> list = tenderDbService.findAllByDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        Integer result = getTendersWithSources(list, sourceIds).size();
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/listShowCpvFilteredForPeriod/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<List<Tender>> getListShowCpvFilteredForPeriod(@PathVariable("username") String username,
                                                                        @PathVariable("start")
                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                                        @PathVariable("end")
                                                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                                        @PathVariable("lang") String lang,
                                                                        @PathVariable("sourceIds") List<Long> sourceIds) {
        List<TenderDb> list = tenderDbService.getListShowCpvFilteredForPeriod(username, start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        list = getTendersWithSources(list, sourceIds);
        List<Tender> result = new ArrayList<>();

        for (TenderDb tenderDb : list) {
            result.add(getTenderTranslated(tenderDb, lang));
        }

        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/listShowCpvFilteredForPeriod-size/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<Integer> getListShowCpvFilteredForPeriodSize(@PathVariable("username") String username,
                                                                            @PathVariable("start")
                                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                                            @PathVariable("end")
                                                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                                            @PathVariable("lang") String lang,
                                                                            @PathVariable("sourceIds") List<Long> sourceIds) {
        List<TenderDb> list = tenderDbService.getListShowCpvFilteredForPeriod(username, start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        Integer result = getTendersWithSources(list, sourceIds).size();
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/listNotShowCpvFilteredForPeriod/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<List<Tender>> getListNotShowCpvFilteredForPeriod(@PathVariable("username") String username,
                                                                           @PathVariable("start")
                                                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                                           @PathVariable("end")
                                                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                                           @PathVariable("lang") String lang,
                                                                           @PathVariable("sourceIds") List<Long> sourceIds) {

        List<TenderDb> list = tenderDbService.getListNotShowCpvFilteredForPeriod(username, start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        list = getTendersWithSources(list, sourceIds);
        List<Tender> result = new ArrayList<>();

        for (TenderDb tenderDb : list) {
            result.add(getTenderTranslated(tenderDb, lang));
        }
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/listNotShowCpvFilteredForPeriod-size/{username}/{start}/{end}/{lang}/{sourceIds}")
    public ResponseEntity<Integer> getListNotShowCpvFilteredForPeriodSize(@PathVariable("username") String username,
                                                                          @PathVariable("start")
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime start,
                                                                          @PathVariable("end")
                                                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime end,
                                                                          @PathVariable("lang") String lang,
                                                                          @PathVariable("sourceIds") List<Long> sourceIds) {

        List<TenderDb> list = tenderDbService.getListNotShowCpvFilteredForPeriod(username, start.toLocalDate().atStartOfDay(), end.toLocalDate().atTime(23, 59));
        Integer result = getTendersWithSources(list, sourceIds).size();
        return new ResponseEntity<>(result, OK);
    }

    private List<TenderDb> getTendersWithSources(List<TenderDb> tenders, List<Long> sourceIds){
        List<TenderDb> result = new ArrayList<>();
        for(TenderDb tender : tenders){
            for(Long sourceId : sourceIds) {
                if(tender.getSource().getId() == sourceId) result.add(tender);
            }
        }
        return result;
    }

    private Tender getTenderTranslated(TenderDb source, String lang) {
        Tender tender = new Tender();
        tender.setId(source.getId());
        tender.setSource(source.getSource());
        tender.setSourceRefNumber(source.getSourceRefNumber());
        tender.setLink(source.getLink());
        tender.setClient(source.getClient());

        ZoneId tlnZone = ZoneId.of("Europe/Tallinn");
        ZonedDateTime zonedDateTime = source.getDate().atZone(tlnZone);
        tender.setDate(zonedDateTime);
        tender.setDeadline(source.getDeadline());

        switch (lang) {
            case "et":
                translateTenderEe(source, tender);
                break;
            case "lv":
                translateTenderLv(source, tender);
                break;
            case "lt":
                translateTenderLt(source, tender);
                break;
            case "ru":
                translateTenderRu(source, tender);
                break;
            default:
                translateTenderEn(source, tender);
        }
        return tender;
    }

    private Tender getTenderOriginal(TenderDb source) {
        Tender tender = new Tender();
        tender.setId(source.getId());
        tender.setSource(source.getSource());
        tender.setSourceRefNumber(source.getSourceRefNumber());
        tender.setLink(source.getLink());
        tender.setTitle(source.getTitle());
        tender.setDescription(source.getDescription());
        tender.setField(source.getField());
        tender.setClient(source.getClient());

        ZoneId tlnZone = ZoneId.of("Europe/Tallinn");
        ZonedDateTime zonedDateTime = source.getDate().atZone(tlnZone);
        tender.setDate(zonedDateTime);

        tender.setDeadline(source.getDeadline());
        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb cpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(cpvDb.getId());
            cpv.setCode(cpvDb.getCode());
            switch (source.getSource().getName()) {
                case "RHR":
                    cpv.setDescription(cpvDb.getDescriptionEe());
                    break;
                case "cvpp":
                    cpv.setDescription(cpvDb.getDescriptionLt());
                    break;
                case "eis.gov.lv":
                    cpv.setDescription(cpvDb.getDescriptionLv());
                    break;
                case "ru":
                    cpv.setDescription(cpvDb.getDescriptionRu());
                    break;
                default:
                    cpv.setDescription(cpvDb.getDescriptionEn());
                    break;
            }
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
        return tender;
    }

    private void translateTenderEe(TenderDb source, Tender tender) {
        tender.setTitle(source.getTitleEe());
        tender.setDescription(source.getDescriptionEe());
        tender.setField(source.getFieldEe());

        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb sourceCpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(sourceCpvDb.getId());
            cpv.setCode(sourceCpvDb.getCode());
            cpv.setDescription(sourceCpvDb.getDescriptionEe());
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
    }

    private void translateTenderLv(TenderDb source, Tender tender) {
        tender.setTitle(source.getTitleLv());
        tender.setDescription(source.getDescriptionLv());
        tender.setField(source.getFieldLv());

        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb sourceCpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(sourceCpvDb.getId());
            cpv.setCode(sourceCpvDb.getCode());
            cpv.setDescription(sourceCpvDb.getDescriptionLv());
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
    }

    private void translateTenderLt(TenderDb source, Tender tender) {
        tender.setTitle(source.getTitleLt());
        tender.setDescription(source.getDescriptionLt());
        tender.setField(source.getFieldLt());

        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb sourceCpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(sourceCpvDb.getId());
            cpv.setCode(sourceCpvDb.getCode());
            cpv.setDescription(sourceCpvDb.getDescriptionLt());
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
    }

    private void translateTenderEn(TenderDb source, Tender tender) {
        tender.setTitle(source.getTitleEn());
        tender.setDescription(source.getDescriptionEn());
        tender.setField(source.getFieldEn());

        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb sourceCpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(sourceCpvDb.getId());
            cpv.setCode(sourceCpvDb.getCode());
            cpv.setDescription(sourceCpvDb.getDescriptionEn());
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
    }

    private void translateTenderRu(TenderDb source, Tender tender) {
        tender.setTitle(source.getTitleRu());
        tender.setDescription(source.getDescriptionRu());
        tender.setField(source.getFieldRu());

        List<Cpv> cpvs = new ArrayList<>();
        for (CpvDb sourceCpvDb : source.getCpvDb()) {
            Cpv cpv = new Cpv();
            cpv.setId(sourceCpvDb.getId());
            cpv.setCode(sourceCpvDb.getCode());
            cpv.setDescription(sourceCpvDb.getDescriptionRu());
            cpvs.add(cpv);
        }

        tender.setCpv(cpvs);
    }
}