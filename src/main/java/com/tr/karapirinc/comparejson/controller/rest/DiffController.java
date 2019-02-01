package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.DiffResponse;
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
class DiffController {

    @Autowired
    DiffService diffService;

    /**
     * Stores Base64 binary data which can be used for comparing with side indicator
     *
     * @param id unique id of comparison operation.
     * @param side Valid side parameter values: left/right {@code DiffSide} indicates side of the comparison.
     * @param body Base64 byte array data to be compared
     * @return Result of operation as ERROR or SAVED_SUCCESSFULLY {@code DiffResponse}
     *
     * TODO multipart form data with octet-stream can be used for less bandwith usage and performance improvement
     */
    @PostMapping("/{id}/{side}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DiffResponse storeDiffSide(@PathVariable Long id, @PathVariable String side, @RequestBody byte[] body) {

        DiffResponse resp;

        try {
            DiffSide diffSide = DiffSide.find(side);
            String decodedPayload = diffService.storeOneSideOfDiff(id, diffSide, decode(body));
            resp=new DiffResponse(ResultCode.SAVED_SUCCESSFULLY);
            resp.setDesc(decodedPayload);
        } catch (IllegalArgumentException e) {
            resp=new DiffResponse(ResultCode.ERROR);
            resp.setDesc(e.getMessage());
        }

        return resp;
    }

    private byte[] decode(@RequestBody byte[] body) {
        return Base64.getDecoder().decode(body);
    }

    /**
     * Compares previously saved Base64 binary data.
     *
     * @param id Unique ID of previously saved data
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code DiffResponse}
     * contains offsets of differences and length of the data too.
     */
    @GetMapping("/{id}")
    @ResponseBody
    public DiffResponse checkDifference(@PathVariable Long id) {
        log.debug("check difference of id {}", id);
        return diffService.checkDifference(id);
    }

}
