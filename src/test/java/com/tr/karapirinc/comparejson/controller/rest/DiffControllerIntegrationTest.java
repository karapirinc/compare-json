package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.CompareJsonApplication;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import org.hamcrest.core.IsNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CompareJsonApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class DiffControllerIntegrationTest {

    //@Rule
    //public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private MockMvc mvc;


    @Autowired
    private DiffRepository diffRepository;


    @Ignore
    @Test
    public void whenValidInput_thenStoreBase64Text() throws Exception {
        final byte[] base64TestData = Base64.getEncoder().encode("TEST String".getBytes());
        mvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(base64TestData));

        List<DiffModel> found = diffRepository.findAll();
        assertThat(found).extracting(DiffModel::getLeft).containsOnly(base64TestData);

    }

    //TODO Rest DOC all services

    @Test
    public void whenValidInputsEqual_thenReturnEqual() throws Exception {
        final byte[] base64TestData = Base64.getEncoder().encode("TEST String".getBytes());
        mvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(base64TestData));
        mvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(base64TestData));

        mvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andDo(document("diff",
                        responseFields(
                        fieldWithPath("result").description("Compare result code"),
                        fieldWithPath("desc").description("Compare operation result description"),
                        fieldWithPath("diffOffsets").description("Offsets of differences"),
                        fieldWithPath("length").description("Length of data"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.EQUAL.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.EQUAL.getDesc())))
                .andExpect(jsonPath("$.diffOffsets", IsNull.nullValue()))
                .andExpect(jsonPath("$.length", IsNull.nullValue()));
    }

    @Test
    public void whenSizeOfInputsNotSame_thenReturnSizeNotEqual() throws Exception {
        final byte[] testData = Base64.getEncoder().encode("TEST String".getBytes());
        final byte[] longerTestData = Base64.getEncoder().encode("TEST String Longer".getBytes());
        mvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(testData));
        mvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(longerTestData));

        mvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.NOT_EQUAL_SIZE.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.NOT_EQUAL_SIZE.getDesc())))
                .andExpect(jsonPath("$.diffOffsets", IsNull.nullValue()))
                .andExpect(jsonPath("$.length", IsNull.nullValue()));
    }

    //TODO FIXME Assert offsets are wrong
    @Test
    public void whenInputsAreDifferentWithSameSize_thenReturnOffsetsAndLength() throws Exception {
        final byte[] testData = Base64.getEncoder().encode("TEST String".getBytes());
        final byte[] diffTestData = Base64.getEncoder().encode("DESD String".getBytes());
        mvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(testData));
        mvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(diffTestData));

        mvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.NOT_EQUAL.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.NOT_EQUAL.getDesc())))
                .andExpect(jsonPath("$.diffOffsets", is(0)))
                .andExpect(jsonPath("$.length", is(4)));
    }
}