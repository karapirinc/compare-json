package com.tr.karapirinc.comparejson.util;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Base64;

public class Base64Test {

    @Test
    @Ignore("TODO Not Implemented")
    public void base64encoderTest(){

        final byte[] testData = Base64.getEncoder().encode("TEST String".getBytes());
        final byte[] diffTestData = Base64.getEncoder().encode("DESD String".getBytes());
        System.out.println(testData.length);

        for(int index = 0; index < testData.length; index++){
            if(testData[index] != diffTestData[index]){
                System.out.println(index);
            }
        }
    }
}
