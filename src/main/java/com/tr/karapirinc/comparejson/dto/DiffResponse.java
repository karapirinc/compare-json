package com.tr.karapirinc.comparejson.dto;

import com.tr.karapirinc.comparejson.constant.ResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class DiffResponse {
    private ResultCode result;
    private String desc;
    private Set<Integer> diffOffsets=new HashSet<>();
    private Integer length;

    public DiffResponse(ResultCode result) {
        this.setResult(result);
        this.setDesc(result.getDesc());
    }
}
