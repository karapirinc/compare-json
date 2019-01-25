package com.tr.karapirinc.comparejson.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DiffSide {
    LEFT("left"), RIGHT("right");

    private String value;

    DiffSide(String value) {
        this.value = value;
    }

    public static DiffSide find(String val) {
        return Arrays.stream(DiffSide.values())
                .filter(e -> e.value.equals(val))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type %s.", val)));
    }

}
