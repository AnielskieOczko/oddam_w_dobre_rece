package org.jankowskirafal.oddamwdobrerece.institutions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "institutions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Institution {

    @Id
    @GeneratedValue
    private Long institutionId;

    private String name;

    private String description;

}
