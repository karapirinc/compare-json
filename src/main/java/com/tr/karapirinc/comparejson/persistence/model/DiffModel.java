package com.tr.karapirinc.comparejson.persistence.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class DiffModel {

    @Id
    private Long id;
    private byte[] left;
    private byte[] right;
}
