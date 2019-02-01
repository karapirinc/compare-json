package com.tr.karapirinc.comparejson.service;

import com.tr.karapirinc.comparejson.constant.DiffSide;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.dto.DiffResponse;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import com.tr.karapirinc.comparejson.service.impl.DiffServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiffServiceImplTest {

    @Mock
    DiffRepository diffRepository;

    @InjectMocks
    DiffServiceImpl diffService;

    private static long TEST_ID=1L;
    private static byte[] TEST_DATA="TEST".getBytes();

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
        diffService.getDiffData(TEST_ID);
    }
    @Test(expected = IllegalStateException.class)
    public void getDiffData_throwsException_whenDiffSideIsEmpty() {
        when(diffRepository.findById(anyLong())).thenReturn(Optional.of(new DiffModel()));

        diffService.getDiffData(TEST_ID);

    }

    @Test
    public void compareDiffData_returnsEqual_whenInputsAreSame() {
        DiffResponse resp = diffService.compareDiffData(TEST_DATA, TEST_DATA);
        assertEquals(ResultCode.EQUAL,resp.getResult());
    }
    @Test
    public void compareDiffData_returnsNotEqualSize_whenInputsAreDifferentSize() {
        byte[] right=TEST_DATA;
        byte[] left= "TESTTEST".getBytes();
        DiffResponse resp = diffService.compareDiffData(left, right);
        assertEquals(ResultCode.NOT_EQUAL_SIZE,resp.getResult());
    }
    @Test
    public void compareDiffData_returnsNotEqual_whenInputsAreDifferent() {
        byte[] right="TEST1234".getBytes();
        byte[] left="TESTTEST".getBytes();
        DiffResponse resp = diffService.compareDiffData(left, right);
        assertEquals(ResultCode.NOT_EQUAL,resp.getResult());
        assertEquals(Integer.valueOf(8),resp.getLength());
        assertArrayEquals(new Integer[]{4,5,6,7},resp.getDiffOffsets().toArray());
    }
    @Test
    public void checkDifference_returnsEqual_whenDataIsValid() {

        DiffModel testDiffModel = new DiffModel(TEST_ID);
        testDiffModel.setLeft(TEST_DATA);
        testDiffModel.setRight(TEST_DATA);
        when(diffRepository.findById(TEST_ID)).thenReturn(Optional.of(testDiffModel));
        DiffResponse resp = diffService.checkDifference(TEST_ID);
        assertEquals(ResultCode.EQUAL,resp.getResult());
    }

    @Test
    public void saveDiffSide_returnsDataString_whenDataFoundOnDatabase() {
        DiffModel testDiffModel = new DiffModel(TEST_ID);
        testDiffModel.setLeft(TEST_DATA);

        when(diffRepository.findById(TEST_ID)).thenReturn(Optional.of(testDiffModel));
        String resp = diffService.storeOneSideOfDiff(TEST_ID, DiffSide.RIGHT, TEST_DATA);
        assertEquals(new String(TEST_DATA),resp);
        verify(diffRepository).save(testDiffModel);

    }

    @Test
    public void saveDiffSide_returnsDataString_whenDataCanNotBeFoundOnDatabase() {

        when(diffRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        String resp = diffService.storeOneSideOfDiff(TEST_ID, DiffSide.LEFT, TEST_DATA);
        assertEquals(new String(TEST_DATA),resp);
        verify(diffRepository).save(any(DiffModel.class));

    }
}