package com.tr.karapirinc.comparejson.controller.rest;

import com.tr.karapirinc.comparejson.CompareJsonApplication;
import com.tr.karapirinc.comparejson.constant.ResultCode;
import com.tr.karapirinc.comparejson.persistence.DiffRepository;
import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CompareJsonApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class DiffControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiffRepository diffRepository;

    //Spring REST Docs related fields
    private static FieldDescriptor[] compareJsonResponseFieldsDoc = {fieldWithPath("result").description("Compare result code"),
            fieldWithPath("desc").description("Compare operation result description"),
            fieldWithPath("diffOffsets").description("Offsets of differences"),
            fieldWithPath("length").description("Length of data")};

    private static ParameterDescriptor idPathParamDoc = parameterWithName("id").description("Unique identifier of comparision");
    private static ParameterDescriptor[] pathParamsDoc = {idPathParamDoc,
            parameterWithName("side").description("Side of the comparision").attributes(key("constraints").value("Valid values are left or right"))};


    @Test
    public void whenValidInput_thenStoreBase64Text() throws Exception {
        final byte[] base64TestData = Base64.getEncoder().encode("TEST String".getBytes());


        mockMvc.perform(post("/v1/diff/{id}/{side}", 1, "left").contentType(MediaType.APPLICATION_JSON).content(base64TestData))
                .andDo(document("storeBase64",
                        pathParameters(pathParamsDoc),
                        responseFields(compareJsonResponseFieldsDoc)));

        List<DiffModel> found = diffRepository.findAll();
        assertThat(found).extracting(DiffModel::getLeft).containsOnly(Base64.getDecoder().decode(base64TestData));

    }

    @Test
    public void whenValidInputsEqual_thenReturnEqual() throws Exception {
        final byte[] base64TestData = Base64.getEncoder().encode("TEST String".getBytes());
        Long testId = 1L;
        mockMvc.perform(post("/v1/diff/{id}/{side}", testId, "left").contentType(MediaType.APPLICATION_JSON).content(base64TestData));
        mockMvc.perform(post("/v1/diff/{id}/{side}", testId, "right").contentType(MediaType.APPLICATION_JSON).content(base64TestData));

        mockMvc.perform(get("/v1/diff/{id}", testId).contentType(MediaType.APPLICATION_JSON)).
                andDo(document("diff",
                        pathParameters(idPathParamDoc),
                        responseFields(compareJsonResponseFieldsDoc)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.EQUAL.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.EQUAL.getDesc())))
                .andExpect(jsonPath("$.diffOffsets", hasSize(0)))
                .andExpect(jsonPath("$.length", IsNull.nullValue()));
    }

    @Test
    public void whenSizeOfInputsNotSame_thenReturnSizeNotEqual() throws Exception {
        final byte[] testData = Base64.getEncoder().encode("TEST String".getBytes());
        final byte[] longerTestData = Base64.getEncoder().encode("TEST String Longer".getBytes());
        mockMvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(testData));
        mockMvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(longerTestData));

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.NOT_EQUAL_SIZE.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.NOT_EQUAL_SIZE.getDesc())))
                .andExpect(jsonPath("$.diffOffsets", hasSize(0)))
                .andExpect(jsonPath("$.length", IsNull.nullValue()));
    }

    @Test
    public void whenInputsAreDifferentWithSameSize_thenReturnOffsetsAndLength() throws Exception {
        final byte[] testData = Base64.getEncoder().encode("TEST String".getBytes());
        final byte[] diffTestData = Base64.getEncoder().encode("DESD String".getBytes());

        mockMvc.perform(post("/v1/diff/1/left").contentType(MediaType.APPLICATION_JSON).content(testData));
        mockMvc.perform(post("/v1/diff/1/right").contentType(MediaType.APPLICATION_JSON).content(diffTestData));

        mockMvc.perform(get("/v1/diff/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result", is(ResultCode.NOT_EQUAL.name())))
                .andExpect(jsonPath("$.desc", is(ResultCode.NOT_EQUAL.getDesc())))
                .andExpect(jsonPath("$.diffOffsets[0]", is(0)))
                .andExpect(jsonPath("$.diffOffsets[1]", is(3)))
                .andExpect(jsonPath("$.length", is(11)));
    }
}