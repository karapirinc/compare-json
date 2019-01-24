package com.tr.karapirinc.comparejson.dto;

import com.tr.karapirinc.comparejson.constant.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CompareJsonResponse {
    private ResultCode result;
    private String desc;
    private Set<Integer> diffOffsets;
    private Integer length;
}
