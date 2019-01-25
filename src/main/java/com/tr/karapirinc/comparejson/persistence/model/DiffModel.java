package com.tr.karapirinc.comparejson.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DiffModel {

    public DiffModel(long id) {
        this.id=id;
    }

    @Id
    @Column(nullable = false)
    private Long id;
    @Column
    private byte[] left;
    @Column
    private byte[] right;


}
