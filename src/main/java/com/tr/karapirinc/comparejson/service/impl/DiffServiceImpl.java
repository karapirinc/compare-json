package com.tr.karapirinc.comparejson.service.impl;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ErrorMessage;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.DiffResponse;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import com.tr.karapirinc.comparejson.service.DiffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service responsible for storing and comparing byte arrays
 *
 * @author Yusuf Ismail Oktay
 * @since 0.0.1
 */
@Slf4j
@Service
public class DiffServiceImpl implements DiffService {

    @Autowired
    private DiffRepository diffRepository;

    /**
     * Evaluates previously saved byte arrays.
     *
     * @param id unique id of comparison operation
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code DiffResponse}
     * contains offsets of differences and length of the data too.
     */
    @Override
    public DiffResponse checkDifference(long id) {

        DiffModel diff = getDiffData(id);
        return compareDiffData(diff.getLeft(), diff.getRight());
    }

    /**
     * Fetches previously saved validated comparison data.
     *
     * @param id unique id of comparison operation
     * @return {@code DiffModel}
     * @throws IllegalStateException if not data exists with ID
     */
    public DiffModel getDiffData(long id) {
        DiffModel diff = diffRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(ErrorMessage.NO_DATA_FOUND));
        validateDiffData(diff);
        return diff;
    }

    /**
     * Finds out differences of 2 byte arrays
     * @param left byte array to be compared
     * @param right byte array to be compared
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code DiffResponse}
     * contains offsets of differences and length of the data too.
     */
    public DiffResponse compareDiffData(byte[] left, byte[] right) {
        ResultCode result;

        if (left.length != right.length)
            result= ResultCode.NOT_EQUAL_SIZE;
         else if (Arrays.equals(left, right))
            result= ResultCode.EQUAL;
         else
            result=  ResultCode.NOT_EQUAL;

        return buildDiffResponse(result,left,right);

    }

    private DiffResponse buildDiffResponse(ResultCode result, byte[] left, byte[] right) {
        DiffResponse resp=new DiffResponse(result);

        if(ResultCode.NOT_EQUAL.equals(result))
            setDiffDetailsToResponse(left, right, resp);

        return resp;
    }

    private void setDiffDetailsToResponse(byte[] left, byte[] right, DiffResponse resp) {
        Integer objectsLength = left.length;
        resp.setLength(objectsLength);

        for (int index = 0; index < objectsLength; index++) {
            if (left[index] != right[index]) {
                resp.getDiffOffsets().add(index);
            }
        }
    }

    /**
     * Validates data for future comparison operations.
     *
     * @param diff {@code DiffModel}
     * @throws IllegalStateException If one of the side data is missing in the DiffModel
     */
    public void validateDiffData(DiffModel diff) {
        if (diff.getLeft() == null || diff.getLeft().length == 0)
            throw new IllegalStateException("Left " + ErrorMessage.SIDE_IS_MISSING);
        if (diff.getRight() == null || diff.getRight().length == 0)
            throw new IllegalStateException("Right " + ErrorMessage.SIDE_IS_MISSING);
    }

    /**
     * Saves or updates comparison data to database
     *
     * @param id unique id of comparison operation
     * @param side Valid side parameter values: enum {@code DiffSide} indicates side of the comparison.
     * @param body byte array data to be compared
     * @return body byte array parameter as String
     */
    public String storeOneSideOfDiff(long id, DiffSide side, byte[] body) {
        log.debug("storeDiffSide  id {} to {} side with body {}", side, id, body);

        diffRepository.findById(id)
                .map(diffModel -> saveDiffModel(side, body, diffModel))
                .orElseGet(() -> saveDiffModel(side, body, new DiffModel(id)));

        return new String(body);
    }

    private DiffModel saveDiffModel(DiffSide side, byte[] body, DiffModel diffModel) {
        setDiffModelSide(side, body, diffModel);
        return diffRepository.save(diffModel);
    }

    private void setDiffModelSide(DiffSide side, byte[] body, DiffModel model) {
        switch (side) {
            case LEFT:
                model.setLeft(body);
                break;
            case RIGHT:
                model.setRight(body);
        }
    }
}
