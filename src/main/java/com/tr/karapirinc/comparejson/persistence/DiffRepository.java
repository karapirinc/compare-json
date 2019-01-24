package com.tr.karapirinc.comparejson.persistence;

import com.tr.karapirinc.comparejson.persistence.model.DiffModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  DiffRepository  extends JpaRepository<DiffModel, Long> {
}
