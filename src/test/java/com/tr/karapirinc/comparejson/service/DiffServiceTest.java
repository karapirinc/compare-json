package com.tr.karapirinc.comparejson.service;

import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Test
    public void checkDifference() {
    }

    @Test
    public void saveDiffSide() {
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateDiffData_throwsException_whenDiffSideIsMissing() {
        diffService.validateDiffData(new DiffModel());
    }
}