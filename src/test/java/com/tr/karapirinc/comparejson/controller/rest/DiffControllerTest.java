package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Base64;

import static com.tr.karapirinc.comparejson.constant.ErrorMessage.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class DiffControllerTest {

    @InjectMocks
    DiffController diffController;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void store_returnsError_whenIdIsMissing() {
        String response = diffController.store(null, DiffSide.LEFT.getValue(), anyString().getBytes());
        assertEquals(ID_IS_MISSING, response);
    }

    @Test
    public void store_returnsError_whenSideIsMissing() {
        String response = diffController.store(any(), null, anyString().getBytes());
        assertEquals(SIDE_IS_MISSING, response);
    }

    @Test
    public void store_returnsError_whenBodyIsMissing() {
        String response = diffController.store(any(), DiffSide.LEFT.getValue(),null);
        assertEquals(BODY_IS_MISSING, response);
    }

    @Test
    public void store_returnsError_whenBodyIsNotAJson() {
        byte[] body="NON JSON STRING".getBytes();
        byte[] encodedBody = Base64.getEncoder().encode(body);
        String response = diffController.store(any(), DiffSide.LEFT.getValue(),encodedBody);
        assertEquals(BODY_IS_NOT_A_JSON_STRING, response);
    }
    @Test
    public void store_returnsJsonString_whenBodyIsAJson() {
        String body="{\"test:\"value\"\"}";
        byte[] encodedBody = Base64.getEncoder().encode(body.getBytes());
        String response = diffController.store(any(), DiffSide.LEFT.getValue(),encodedBody);
        assertEquals(body, response);
    }

    @Test(expected = Exception.class)
    public void checkDifference_returnsError_whenIdIsMissing() {
        CompareJsonResponse response = diffController.checkDifference(null);
        assertEquals(null, response);
    }

}