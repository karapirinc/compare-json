package com.tr.karapirinc.comparejson.service;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ErrorMessage;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
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
public class DiffService {

    @Autowired
    private DiffRepository diffRepository;

    /**
     * Evaluates previously saved byte arrays.
     *
     * @param id unique id of comparison operation
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code CompareJsonResponse}
     * contains offsets of differences and length of the data too.
     */
    public CompareJsonResponse checkDifference(long id) {

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
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code CompareJsonResponse}
     * contains offsets of differences and length of the data too.
     */
    public CompareJsonResponse compareDiffData(byte[] left, byte[] right) {

        CompareJsonResponse resp = new CompareJsonResponse();

        if (left.length != right.length) {
            resp.setResult(ResultCode.NOT_EQUAL_SIZE);
            resp.setDesc(ResultCode.NOT_EQUAL_SIZE.getDesc());
        } else if (Arrays.equals(left, right)) {
            resp.setResult(ResultCode.EQUAL);
            resp.setDesc(ResultCode.EQUAL.getDesc());
        } else {
            Integer objectsLength = left.length;
            resp.setResult(ResultCode.NOT_EQUAL);
            resp.setDesc(ResultCode.NOT_EQUAL.getDesc());
            resp.setLength(objectsLength);
            for (int index = 0; index < objectsLength; index++) {
                if (left[index] != right[index]) {
                    resp.getDiffOffsets().add(index);
                }
            }
        }
        return resp;
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
    public String saveDiffSide(long id, DiffSide side, byte[] body) {
        log.debug("storeDiffSide  id {} to {} side with body {}", side, id, body);

        diffRepository.findById(id)
                .map(diffModel -> {
                    setDiffModelSide(side, body, diffModel);
                    return diffRepository.save(diffModel);
                })
                .orElseGet(() -> {
                    DiffModel diffModel = new DiffModel(id);
                    setDiffModelSide(side, body, diffModel);
                    return diffRepository.save(diffModel);
                });

        return new String(body);
    }

    public void setDiffModelSide(DiffSide side, byte[] body, DiffModel model) {
        switch (side) {
            case LEFT:
                model.setLeft(body);
                break;
            case RIGHT:
                model.setRight(body);
        }
    }
}
