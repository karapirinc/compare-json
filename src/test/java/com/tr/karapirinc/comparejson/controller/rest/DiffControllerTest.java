package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.DiffResponse;
import com.tr.karapirinc.comparejson.service.impl.DiffServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiffControllerTest {

    @InjectMocks
    DiffController diffController;

    @Mock
    DiffServiceImpl diffService;

    private static final long TEST_ID = 1L;


    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void storeDiffSide_returnsError_whenSideIsNotValid() {
        final String notValidSideValue = "NOT_VALID_SIDE_VALUE";
        DiffResponse response = diffController.storeDiffSide(TEST_ID, notValidSideValue, "DUMMY TEST BODY".getBytes());
        assertEquals(ResultCode.ERROR, response.getResult());
        assertEquals("Unsupported type " + notValidSideValue + ".", response.getDesc());
    }


    @Test
    public void storeDiffSide_returnsJsonString_whenRequestIsValid() {
        String testBody = "{\"test:\"value\"\"}";

        DiffSide testSide = DiffSide.LEFT;

        when(diffService.storeOneSideOfDiff(TEST_ID, testSide, testBody.getBytes())).thenReturn(testBody);

        byte[] encodedBody = Base64.getEncoder().encode(testBody.getBytes());
        DiffResponse response = diffController.storeDiffSide(TEST_ID, testSide.getValue(), encodedBody);

        assertEquals(testBody, response.getDesc());
        assertEquals(ResultCode.SAVED_SUCCESSFULLY, response.getResult());
    }

    @Test
    public void checkDifference_returnsServiceResponse_whenRequestIsValid() {
        DiffResponse mockResp = new DiffResponse();
        mockResp.setResult(ResultCode.EQUAL);

        when(diffService.checkDifference(TEST_ID)).thenReturn(mockResp);
        DiffResponse resp = diffController.checkDifference(TEST_ID);
        assertEquals(mockResp.getResult(), resp.getResult());
    }

}