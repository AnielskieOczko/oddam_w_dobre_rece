package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.persistence.*;
import lombok.*;
import org.jankowskirafal.oddamwdobrerece.categories.Category;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "donations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
    List<Category> categories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutionId", nullable = false)
    Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    String street;
    String city;
    String zip;
    String phone;
    LocalDate pickUpDate;
    LocalTime pickUpTime;
    String pickUpComment;

}
