package com.tr.karapirinc.comparejson.constant;

import lombok.Getter;

@Getter
public enum DiffSide {
    LEFT("left"), RIGHT("right");

    private String value;

    DiffSide(String value) {
        this.value = value;
    }
}
