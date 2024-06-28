package com.supportportal.resource;

import com.supportportal.domain.*;
import com.supportportal.exception.domain.CpvAlreadyAddedException;
import com.supportportal.exception.domain.MoreGenericShowCpvAddedException;
import com.supportportal.service.CpvDbService;
import com.supportportal.service.UserCpvShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.supportportal.constant.UserImplConstant.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/userCpvShow")
public class UserCpvShowResource {
    private UserCpvShowService userCpvShowService;
    private CpvDbService cpvDbService;
    public static final String USER_CPV_SHOW_DELETED_SUCCESSFULLY = "CPV 'show only' filter deleted successfully";

    @Autowired
    public UserCpvShowResource(UserCpvShowService userCpvShowService,
                               CpvDbService cpvDbService){
        this.userCpvShowService = userCpvShowService;
        this.cpvDbService = cpvDbService;
    }



    @PostMapping("/add")
    public ResponseEntity<UserCpvShow> addUserCpvShow(@RequestParam("username") String username,
                                                        @RequestParam("cpvId") Long cpvId,
                                                        @RequestParam("lang") String lang) throws CpvAlreadyAddedException, MoreGenericShowCpvAddedException {
        CpvDb cpvDb = cpvDbService.getById(cpvId);
        UserCpvShowDb userCpvShowDb = new UserCpvShowDb();
        userCpvShowDb.setUsername(username);
        userCpvShowDb.setCpvDb(cpvDb);
        userCpvShowService.save(userCpvShowDb, lang);
        UserCpvShow result = getUserCpvShowTranslated(userCpvShowDb, lang);

        return new ResponseEntity<>(result, OK);
    }

    @PostMapping("/addCustom")
    public ResponseEntity<UserCpvShow> addCustomUserCpvShow(@RequestParam("username") String username,
                                                              @RequestParam("cpvCode") String cpvCode,
                                                              @RequestParam("cpvDescription") String cpvDescription,
                                                              @RequestParam("lang") String lang) throws CpvAlreadyAddedException, MoreGenericShowCpvAddedException {
        CpvDb cpvDb = new CpvDb();
        cpvDb.setCode(cpvCode.trim());
        cpvDb.setDescription(cpvDescription.trim());
        cpvDb.setDescriptionEe(cpvDescription.trim());
        cpvDb.setDescriptionLv(cpvDescription.trim());
        cpvDb.setDescriptionLt(cpvDescription.trim());
        cpvDb.setDescriptionEn(cpvDescription.trim());
        cpvDb.setDescriptionRu(cpvDescription.trim());
        cpvDbService.save(cpvDb);

        UserCpvShowDb userCpvShowDb = new UserCpvShowDb();
        userCpvShowDb.setUsername(username);
        userCpvShowDb.setCpvDb(cpvDb);
        userCpvShowService.save(userCpvShowDb, lang);

        UserCpvShow result = getUserCpvShowTranslated(userCpvShowDb, lang);
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping("/{username}/{lang}")
    public ResponseEntity<List<UserCpvShow>> getUserShowCpvs(@PathVariable("username") String username,
                                                             @PathVariable("lang") String lang) {
        List<UserCpvShowDb> userCpvShowDbs = userCpvShowService.findAllByUsernameOrderByCpvDbIdAsc(username);
        List<UserCpvShow> result = new ArrayList<>();
        for(UserCpvShowDb userCpvShowDb : userCpvShowDbs) {
            result.add(getUserCpvShowTranslated(userCpvShowDb, lang));
        }
        return new ResponseEntity<>(result, OK);
    }

    @DeleteMapping("/delete/{id}/{lang}")
    public ResponseEntity<HttpResponse> deleteUserCpvNotShow(@PathVariable("id") Long id,
                                                             @PathVariable("lang") String language){
        userCpvShowService.deleteById(id);
        return response(OK, deletedSuccessConst(language));
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }

    private UserCpvShow getUserCpvShowTranslated(UserCpvShowDb source, String lang){
        UserCpvShow result = new UserCpvShow();
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
