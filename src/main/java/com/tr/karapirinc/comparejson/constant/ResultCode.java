package com.tr.karapirinc.comparejson.constant;

import lombok.Getter;

@Getter
public enum ResultCode {
    EQUAL("Texts are equal"),
    NOT_EQUAL_SIZE("Size of the texts are not equal"),
    NOT_EQUAL("Texts are different"),
    ERROR("An error occured!"),
    SAVED_SUCCESSFULLY("Saved Successfully");

    private final String desc;

    ResultCode(String desc) {
        this.desc = desc;
    }
}
