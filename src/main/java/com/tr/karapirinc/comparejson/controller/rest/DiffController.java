package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import com.tr.karapirinc.comparejson.service.DiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

/**
 * TODO multipart form data with octet-stream can be used for bandwith and performance improvement
 */
@Slf4j
@RestController()
@RequestMapping("/v1/diff")
public class DiffController {

    @Autowired
    private DiffService diffService;

    @PostMapping("/{id}/{side}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CompareJsonResponse storeDiffSide(@PathVariable Long id, @PathVariable String side, @RequestBody byte[] body) {

        CompareJsonResponse resp = new CompareJsonResponse();

        try {
            DiffSide diffSide = DiffSide.find(side);
            final byte[] decodedBody = Base64.getDecoder().decode(body);
            String decodedJson = diffService.saveDiffSide(id, diffSide, decodedBody);
            resp.setDesc(decodedJson);
            resp.setResult(ResultCode.SAVED_SUCCESSFULLY);
        } catch (IllegalArgumentException e) {
            resp.setResult(ResultCode.ERROR);
            resp.setDesc(e.getMessage());
        }

        return resp;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CompareJsonResponse checkDifference(@PathVariable Long id) {
        log.debug("check difference of id {}", id);
        return diffService.checkDifference(id);
    }

}
