package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO multipart form data with octet-stream can be used for bandwith and performance improvement
 *
 */
@Slf4j
@RestController()
@RequestMapping("/v1/diff")
public class DiffController {

    //TODO Side can be enum
    @PostMapping("/{id}/{side}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public String store(@PathVariable Long id, @PathVariable String side, @RequestBody byte[] body) {
        log.debug("body {}",body);
        final byte[] decode = Base64.getDecoder().decode(body);
        String decodedBody=new String(decode);
        log.debug("store  id {} to {} side with body {}", side,id, decodedBody);
        return decodedBody;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CompareJsonResponse checkDifference(@PathVariable Long id) {
        log.debug("check difference of id {}", id);
        CompareJsonResponse resp=new CompareJsonResponse();
        return resp;
    }

}
