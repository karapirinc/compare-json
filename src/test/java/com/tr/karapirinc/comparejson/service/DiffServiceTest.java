package com.tr.karapirinc.comparejson.service;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.CompareJsonResponse;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiffServiceTest {

    @Mock
    DiffRepository diffRepository;

    @InjectMocks
    DiffService diffService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalStateException.class)
    public void validateDiffData_throwsException_whenDiffSideIsMissing() {
        diffService.validateDiffData(new DiffModel());
    }


    @Test(expected = IllegalStateException.class)
    public void getDiffData_throwsException_whenDataCanNotBeFound() {
        when(diffRepository.findById(anyLong())).thenReturn(Optional.empty());
        diffService.getDiffData(1);
    }
    @Test(expected = IllegalStateException.class)
    public void getDiffData_throwsException_whenDiffSideIsEmpty() {
        when(diffRepository.findById(anyLong())).thenReturn(Optional.of(new DiffModel()));

        diffService.getDiffData(1);

    }

    @Test
    public void compareDiffData_returnsEqual_whenInputsAreSame() {
        byte[] right="TEST".getBytes();
        byte[] left="TEST".getBytes();;
        CompareJsonResponse resp = diffService.compareDiffData(left, right);
        assertEquals(ResultCode.EQUAL,resp.getResult());
    }
    @Test
    public void compareDiffData_returnsNotEqualSize_whenInputsAreDifferentSize() {
        byte[] right="TEST".getBytes();
        byte[] left="TESTTEST".getBytes();;
        CompareJsonResponse resp = diffService.compareDiffData(left, right);
        assertEquals(ResultCode.NOT_EQUAL_SIZE,resp.getResult());
    }
    @Test
    public void compareDiffData_returnsNotEqual_whenInputsAreDifferent() {
        byte[] right="TEST1234".getBytes();
        byte[] left="TESTTEST".getBytes();;
        CompareJsonResponse resp = diffService.compareDiffData(left, right);
        assertEquals(ResultCode.NOT_EQUAL,resp.getResult());
        assertEquals(Integer.valueOf(8),resp.getLength());
        assertArrayEquals(new Integer[]{4,5,6,7},resp.getDiffOffsets().toArray());
    }
    @Test
    public void checkDifference_returnsEqual_whenDataIsValid() {
        long testId=1L;
        DiffModel testDiffModel = new DiffModel(testId);
        byte[] testData = "TEST".getBytes();
        testDiffModel.setLeft(testData);
        testDiffModel.setRight(testData);
        when(diffRepository.findById(testId)).thenReturn(Optional.of(testDiffModel));
        CompareJsonResponse resp = diffService.checkDifference(testId);
        assertEquals(ResultCode.EQUAL,resp.getResult());
    }

    @Test
    public void saveDiffSide_returnsDataString_whenDataFoundOnDatabase() {
        long testId=1L;
        DiffModel testDiffModel = new DiffModel(testId);
        byte[] testData = "TEST".getBytes();
        testDiffModel.setLeft(testData);

        when(diffRepository.findById(testId)).thenReturn(Optional.of(testDiffModel));
        String resp = diffService.saveDiffSide(testId, DiffSide.RIGHT, testData);
        assertEquals(new String(testData),resp);
        verify(diffRepository).save(testDiffModel);

    }

    @Test
    public void saveDiffSide_returnsDataString_whenDataCanNotBeFoundOnDatabase() {
        long testId=1L;
        byte[] testData = "TEST".getBytes();

        when(diffRepository.findById(testId)).thenReturn(Optional.empty());
        String resp = diffService.saveDiffSide(testId, DiffSide.LEFT, testData);
        assertEquals(new String(testData),resp);
        verify(diffRepository).save(any(DiffModel.class));

    }
}