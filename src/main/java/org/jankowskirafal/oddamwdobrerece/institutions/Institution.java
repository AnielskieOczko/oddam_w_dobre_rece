package org.jankowskirafal.oddamwdobrerece.institutions;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "{institution.name.NotBlank}")
    @Size(max = 255, message = "{institution.name.Size}")
    private String name;

    @NotBlank(message = "{institution.description.NotBlank}")
    @Size(max = 1000, message = "{institution.description.Size}")
    private String description;

}
