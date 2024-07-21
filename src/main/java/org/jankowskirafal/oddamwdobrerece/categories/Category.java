package org.jankowskirafal.oddamwdobrerece.categories;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Category {

    @Id
    @GeneratedValue
    Long categoryId;

    @Enumerated(EnumType.STRING)
    CategoryType name;
}

