package org.jankowskirafal.oddam_w_dobre_rece.donations;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jankowskirafal.oddam_w_dobre_rece.categories.Category;
import org.jankowskirafal.oddam_w_dobre_rece.institutions.Institution;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "donations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Donation {
    @Id
    @GeneratedValue
    private Long donationId;

    Integer quantity;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "donations_categories",
            joinColumns = @JoinColumn(name = "donation_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutionId", nullable = false)
    Institution institution;

    String street;
    String city;
    String zip;
    LocalDate pickUpDate;
    LocalTime pickUpTime;
    String pickUpComment;

}
