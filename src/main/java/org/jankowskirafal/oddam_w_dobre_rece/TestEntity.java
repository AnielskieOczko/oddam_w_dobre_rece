package org.jankowskirafal.oddam_w_dobre_rece;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "testEntities")
public class TestEntity {

    @Id
    @GeneratedValue
    Long id;

    private String name;
}
