package com.supportportal.resource;

import com.supportportal.domain.HttpResponse;
import com.supportportal.domain.Source;
import com.supportportal.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = { "/source"})
public class SourceResource {
    private SourceService sourceService;

    @Autowired
    public SourceResource(SourceService sourceService){
        this.sourceService = sourceService;
    }

    @GetMapping("/all")
    private ResponseEntity<List<Source>> getAll(){
        List<Source> list = sourceService.getAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }
}
