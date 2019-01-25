package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import com.tr.karapirinc.comparejson.service.DiffService;
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
    DiffService diffService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void storeDiffSide_returnsError_whenSideIsNotValid() {
        CompareJsonResponse response = diffController.storeDiffSide(1L, "XXXX", "XXX".getBytes());
        assertEquals(ResultCode.ERROR,response.getResult());
        assertEquals("Unsupported type XXXX.",response.getDesc());
    }


    @Test
    public void storeDiffSide_returnsJsonString_whenRequestIsValid() {
        String testBody="{\"test:\"value\"\"}";
        long testId = 1L;
        DiffSide testSide = DiffSide.LEFT;

        when(diffService.saveDiffSide(testId, testSide,testBody.getBytes())).thenReturn(testBody);

        byte[] encodedBody = Base64.getEncoder().encode(testBody.getBytes());
        CompareJsonResponse response = diffController.storeDiffSide(testId, testSide.getValue(), encodedBody);

        assertEquals(testBody, response.getDesc());
        assertEquals(ResultCode.SAVED_SUCCESSFULLY, response.getResult());
    }

    @Test
    public void checkDifference_returnsServiceResponse_whenRequestIsValid() {
        long testId = 1L;
        CompareJsonResponse mockResp=new CompareJsonResponse();
        mockResp.setResult(ResultCode.EQUAL);

        when(diffService.checkDifference(testId)).thenReturn(mockResp);
        CompareJsonResponse resp = diffController.checkDifference(testId);
        assertEquals(mockResp.getResult(),resp.getResult());
    }

}