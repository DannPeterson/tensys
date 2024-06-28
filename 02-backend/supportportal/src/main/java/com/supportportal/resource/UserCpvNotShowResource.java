package com.supportportal.resource;

import com.supportportal.domain.*;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericNotShowCpvAddedException;
import com.supportportal.service.CpvDbService;
import com.supportportal.service.UserCpvNotShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.supportportal.constant.UserImplConstant.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/userCpvNotShow")
public class UserCpvNotShowResource {
    private UserCpvNotShowService userCpvNotShowService;
    private CpvDbService cpvDbService;

    @Autowired
    public UserCpvNotShowResource(UserCpvNotShowService userCpvNotShowService,
                                  CpvDbService cpvDbService) {
        this.userCpvNotShowService = userCpvNotShowService;
        this.cpvDbService = cpvDbService;
    }

    @PostMapping("/add")
    public ResponseEntity<UserCpvNotShow> addUserCpvNotShow(@RequestParam("username") String username,
                                                            @RequestParam("cpvId") Long cpvId,
                                                            @RequestParam("lang") String lang) throws CpvAlreadyAddedException, MoreGenericNotShowCpvAddedException {

        CpvDb cpvDb = cpvDbService.getById(cpvId);
        UserCpvNotShowDb userCpvNotShowDb = new UserCpvNotShowDb();
        userCpvNotShowDb.setUsername(username);
        userCpvNotShowDb.setCpvDb(cpvDb);
        userCpvNotShowService.save(userCpvNotShowDb, lang);

        UserCpvNotShow result = getUserCpvNotShowTranslated(userCpvNotShowDb, lang);

        return new ResponseEntity<>(result, OK);
    }

    @PostMapping("/addCustom")
    public ResponseEntity<UserCpvNotShow> addCustomUserCpvNotShow(@RequestParam("username") String username,
                                                                  @RequestParam("cpvCode") String cpvCode,
                                                                  @RequestParam("cpvDescription") String cpvDescription,
                                                                  @RequestParam("lang") String lang) throws CpvAlreadyAddedException, MoreGenericNotShowCpvAddedException {
        CpvDb cpvDb = new CpvDb();
        cpvDb.setCode(cpvCode.trim());
        cpvDb.setDescription(cpvDescription.trim());
        cpvDb.setDescriptionEe(cpvDescription.trim());
        cpvDb.setDescriptionLv(cpvDescription.trim());
        cpvDb.setDescriptionLt(cpvDescription.trim());
        cpvDb.setDescriptionEn(cpvDescription.trim());
        cpvDb.setDescriptionRu(cpvDescription.trim());
        cpvDbService.save(cpvDb);

        UserCpvNotShowDb userCpvNotShowDb = new UserCpvNotShowDb();
        userCpvNotShowDb.setUsername(username);
        userCpvNotShowDb.setCpvDb(cpvDb);
        userCpvNotShowService.save(userCpvNotShowDb, lang);

        UserCpvNotShow result = getUserCpvNotShowTranslated(userCpvNotShowDb, lang);

        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/{username}/{lang}")
    public ResponseEntity<List<UserCpvNotShow>> getUserNotShowCpvs(@PathVariable("username") String username,
                                                                     @PathVariable("lang") String lang) {
        List<UserCpvNotShowDb> userCpvNotShowDbs = userCpvNotShowService.findAllByUsernameOrderByCpvDbIdAsc(username);
        List<UserCpvNotShow> result = new ArrayList<>();
        for(UserCpvNotShowDb userCpvNotShowDb : userCpvNotShowDbs) {
            result.add(getUserCpvNotShowTranslated(userCpvNotShowDb, lang));
        }
        return new ResponseEntity<>(result, OK);
    }

    @DeleteMapping("/delete/{id}/{lang}")
    public ResponseEntity<HttpResponse> deleteUserCpvNotShow(@PathVariable("id") Long id,
                                                             @PathVariable("lang") String language){
        userCpvNotShowService.deleteById(id);
        return response(OK, deletedSuccessConst(language));
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    private UserCpvNotShow getUserCpvNotShowTranslated(UserCpvNotShowDb source, String lang){
        UserCpvNotShow result = new UserCpvNotShow();
        result.setId(source.getId());
        result.setUsername(source.getUsername());

        Cpv cpv = new Cpv();
        cpv.setCode(source.getCpvDb().getCode());

        if(source.getCpvDb().getCode().contains("*")){
            cpv.setDescription(source.getCpvDb().getDescription());
            result.setCpv(cpv);
            return result;
        }

        switch (lang) {
            case "et": cpv.setDescription(source.getCpvDb().getDescriptionEe());
                break;
            case "lv": cpv.setDescription(source.getCpvDb().getDescriptionLv());
                break;
            case "lt": cpv.setDescription(source.getCpvDb().getDescriptionLt());
                break;
            case "ru": cpv.setDescription(source.getCpvDb().getDescriptionRu());
                break;
            default: cpv.setDescription(source.getCpvDb().getDescriptionEn());
        }
        result.setCpv(cpv);
        return result;
    }

    private String deletedSuccessConst(String language) {
        switch (language) {
            case "et": return USER_CPV_NOT_SHOW_DELETED_SUCCESSFULLY_ET;
            case "lt": return USER_CPV_NOT_SHOW_DELETED_SUCCESSFULLY_LT;
            case "lv": return USER_CPV_NOT_SHOW_DELETED_SUCCESSFULLY_LV;
            case "ru": return USER_CPV_NOT_SHOW_DELETED_SUCCESSFULLY_RU;
            default: return USER_CPV_NOT_SHOW_DELETED_SUCCESSFULLY_EN;
        }
    }
}