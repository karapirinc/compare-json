package com.tr.karapirinc.comparejson.service;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.dto.DiffResponse;
import org.springframework.stereotype.Service;

/**
 * Service responsible for storing and comparing byte arrays
 *
 * @author Yusuf Ismail Oktay
 * @since 0.0.1
 */
@Service
public interface DiffService {

    /**
     * Saves or updates comparison data to database
     *
     * @param id unique id of comparison operation
     * @param diffSide Valid side parameter values: enum {@code DiffSide} indicates side of the comparison.
     * @param decodedBody byte array data to be compared
     * @return body byte array parameter as String
     */
    String storeOneSideOfDiff(long id, DiffSide diffSide, byte[] decodedBody);


    /**
     * Evaluates previously saved byte arrays.
     *
     * @param id unique id of comparison operation
     * @return result of comparison. EQUAL, NOT_EQUAL_SIZE AND NOT_EQUAL. If result NOT_EQUAL response object {@code DiffResponse}
     * contains offsets of differences and length of the data too.
     */
    DiffResponse checkDifference(long id);
}
