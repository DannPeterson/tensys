package com.supportportal.grabber;

import com.supportportal.domain.CpvDb;
import com.supportportal.domain.Source;
import com.supportportal.domain.TenderDb;
import com.supportportal.service.*;
import com.supportportal.utility.TenderTranslate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class GrabberSheduler {
    private TenderDbService tenderDbService;
    private CpvDbService cpvDbService;
    private TenderTranslate tenderTranslate;
    private UserCpvNotShowService userCpvNotShowService;
    private SourceService sourceService;
    private UserService userService;

    public GrabberSheduler(TenderDbService tenderDbService,
                           CpvDbService cpvDbService,
                           TenderTranslate tenderTranslate,
                           UserCpvNotShowService userCpvNotShowService,
                           SourceService sourceService,
                           UserService userService) {
        this.tenderDbService = tenderDbService;
        this.cpvDbService = cpvDbService;
        this.tenderTranslate = tenderTranslate;
        this.userCpvNotShowService = userCpvNotShowService;
        this.sourceService = sourceService;
        this.userService = userService;
    }

    //    @Scheduled(fixedRate = 3600000)
    private void addSources() {
        Source rhr = new Source();
        rhr.setName("RHR");
        rhr.setLink("https://riigihanked.riik.ee");
        sourceService.save(rhr);

        Source eis = new Source();
        eis.setName("eis.gov.lv");
        eis.setLink("https://eis.gov.lv");
        sourceService.save(eis);

        Source cvpp = new Source();
        cvpp.setName("cvpp");
        cvpp.setLink("https://cvpp.eviesiejipirkimai.lt/");
        sourceService.save(cvpp);
    }

//    @Scheduled(fixedRate = 7200000)
    private void getRhrTenders() {
        RhrGrabber rhrGrabber = new RhrGrabber();
        try {
            List<TenderDb> rssTenderDbs = rhrGrabber.getRssTenders();
            for (int i = 0; i < 100; i++) {
                System.out.println("<<< page " + i + " >>>");
                TenderDb tenderDb = rssTenderDbs.get(i);
                if (tenderDb.getDeadline().isAfter(LocalDate.now())) {
                    if (tenderDbService.getTendersDbByLink(tenderDb.getLink()).size() == 0) {
                        try {
                            tenderDb.setCpvDb(rhrGrabber.getTenderCpvs(tenderDb.getLink()));
                            tenderDb.setSource(sourceService.findByName("RHR"));
                            translateToRu(tenderDb);
                            tenderDbService.addNewTenderDb(tenderDb);
                            printTenderDbRus(tenderDb);
                        } catch (Exception e) {
                            System.out.println("<<< RHR getRssTenders >>> getTenderCpvs >>> exception >>>" + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("<<< RHR getRssTenders exception >>>");
            System.out.println(e.getMessage());
        }
    }

//    @Scheduled(fixedRate = 7200000, initialDelay = 2400000)
//    @Scheduled(fixedRate = 7200000)
    private void getEisGovLvTenders() {
        EisGovLvGrabber grabber = new EisGovLvGrabber();
        for (int i = 1; i <= 3; i++) {
            System.out.println("<<<< PAGE " + i + " >>>>");
            try {
                List<String> links = grabber.getTenderLinks(i);
                for (String link : links) {
                    System.out.println(link);
                    if (tenderDbService.getTendersDbByLink(link).size() == 0) {
                        try {
                            TenderDb tenderDb = grabber.getTenderFromLink(link);
                            if (tenderDb.getDeadline() != null && tenderDb.getDeadline().isAfter(LocalDate.now())) {
                                tenderDb.setSource(sourceService.findByName("eis.gov.lv"));
                                translateToRu(tenderDb);
                                tenderDbService.addNewTenderDb(tenderDb);
                                printTenderDbRus(tenderDb);
                            }
                        } catch (Exception e) {
                            System.out.println("<<< EisGovLvGrabber >>> getTenderFromLink >>> exception >>>");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("<<< EisGovLvGrabber >>> getTenderLinks >>> exception >>>");
            }
        }
    }

//    @Scheduled(fixedRate = 7200000, initialDelay = 4800000)
//    @Scheduled(fixedRate = 3600000)
    private void getCpvvTenders() {
        CpvvGrabber cpvvGrabber = new CpvvGrabber();
        for (int p = 1; p <= 3; p++) {
            try {
                List<String> links = cpvvGrabber.getTenderLinks(p);
                for (int i = 0; i < links.size(); i++) {
                    System.out.println("<<<< PAGE NUMBER " + p + " >>>>");
                    System.out.println("<<<< " + i + " >>>>");
                    if (tenderDbService.getTendersDbByLink(links.get(i)).size() == 0) {
                        try {
                            TenderDb tenderDb = cpvvGrabber.getTenderFromLink(links.get(i));
                            if (tenderDb != null && tenderDb.getDeadline().isAfter(LocalDate.now())) {
                                tenderDb.setSource(sourceService.findByName("cvpp"));
                                translateToRu(tenderDb);
                                tenderDbService.addNewTenderDb(tenderDb);
                                printTenderDbRus(tenderDb);
                            } else {
                                System.out.println("<<<<<<<<< Null or deadline is after today or wrong type (tender result or else)>>>>>>>>");
                            }
                        } catch (Exception e) {
                            System.out.println("<<< CpvvGrabber >>> getTenderFromLink >>> exception >>>");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("<<< CpvvGrabber >>> getTenderLinks >>> exception >>>");
            }
        }
    }

    private void printTenderRus(TenderDb tender) {
        System.out.println();
        System.out.println("ID: " + tender.getId());
        System.out.println("Source: " + tender.getSource().getName());
        System.out.println("Ref: " + tender.getSourceRefNumber());
        System.out.println("Link: " + tender.getLink());
        System.out.println("Title: " + tender.getTitleRu());
        System.out.println("Client: " + tender.getClient());
        System.out.println("Descr: " + tender.getDescriptionRu());
        System.out.println("Field: " + tender.getFieldRu());
        System.out.println("Date: " + tender.getDate());
        System.out.println("Deadl: " + tender.getDeadline());
        System.out.println("CPV:");
        for (CpvDb cpvDb : tender.getCpvDb()) {
            System.out.println(cpvDb.getCode() + " - " + cpvDb.getDescriptionRu());
        }
        System.out.println();
    }

    private void printTenderDbRus(TenderDb tender) {
        tender = tenderDbService.getTenderDbById(tender.getId());
        System.out.println();
        System.out.println("ID: " + tender.getId());
        System.out.println("Source: " + tender.getSource().getName());
        System.out.println("Ref: " + tender.getSourceRefNumber());
        System.out.println("Link: " + tender.getLink());
        System.out.println("Title: " + tender.getTitleRu());
        System.out.println("Client: " + tender.getClient());
        System.out.println("Descr: " + tender.getDescriptionRu());
        System.out.println("Field: " + tender.getFieldRu());
        System.out.println("Date: " + tender.getDate());
        System.out.println("Deadl: " + tender.getDeadline());
        System.out.println("CPV:");
        for (CpvDb cpvDb : tender.getCpvDb()) {
            System.out.println(cpvDb.getCode() + " - " + cpvDb.getDescriptionRu());
        }
        System.out.println();
    }

    private void translateToRu(TenderDb tenderDb) {
        String description = tenderDb.getDescription();
        if (description != null) {
            tenderDb.setDescriptionEe(description);
            tenderDb.setDescriptionLv(description);
            tenderDb.setDescriptionLt(description);
            tenderDb.setDescriptionEn(description);
            tenderDb.setDescriptionRu(tenderTranslate.translateText("ru", description));
        }

        String title = tenderDb.getTitle();
        if (title != null) {
            tenderDb.setTitleEe(title);
            tenderDb.setTitleLv(title);
            tenderDb.setTitleLt(title);
            tenderDb.setTitleEn(title);
            tenderDb.setTitleRu(tenderTranslate.translateText("ru", title));
        }

        String field = tenderDb.getField();
        if (field != null) {
            tenderDb.setFieldEe(field);
            tenderDb.setFieldLv(field);
            tenderDb.setFieldLt(field);
            tenderDb.setFieldEn(field);
            tenderDb.setFieldRu(tenderTranslate.translateText("ru", field));
        }
    }
}