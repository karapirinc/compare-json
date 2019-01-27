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
 * Exposes RESTful services for comparing Base64 data
 *
 * @author Yusuf Ismail Oktay
 * @since 0.0.1
 */
@Slf4j
@RestController()
@RequestMapping("/v1/diff")
public class DiffController {

    @Autowired
    private DiffService diffService;

    /**
     * Stores Base64 binary data which can be used for comparing with side indicator
     *
     * @param id unique id of comparison operation.
     * @param side Valid side parameter values: left/right {@code DiffSide} indicates side of the comparison.
     * @param body Base64 JSON byte array data to be compared
     * @return Result of operation as ERROR or SAVED_SUCCESSFULLY {@code CompareJsonResponse}
     *
     * TODO multipart form data with octet-stream can be used for less bandwith usage and performance improvement
     */
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

    /**
     * Compares previously saved Base64 binary data.
     *
     * @param id Unique ID of previously saved data
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code CompareJsonResponse}
     * contains offsets of differences and length of the data too.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public CompareJsonResponse checkDifference(@PathVariable Long id) {
        log.debug("check difference of id {}", id);
        return diffService.checkDifference(id);
    }

}
