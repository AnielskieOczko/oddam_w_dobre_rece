package org.jankowskirafal.oddam_w_dobre_rece.categories;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    Long categoryId;

    @Enumerated(EnumType.STRING)
    CategoryType name;
}

