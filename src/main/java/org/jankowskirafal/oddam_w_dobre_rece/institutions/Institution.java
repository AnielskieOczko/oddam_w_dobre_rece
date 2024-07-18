package org.jankowskirafal.oddam_w_dobre_rece.institutions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institutions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Institution {

    @Id
    @GeneratedValue
    private Long institutionId;

    String name;

    String description;

}
