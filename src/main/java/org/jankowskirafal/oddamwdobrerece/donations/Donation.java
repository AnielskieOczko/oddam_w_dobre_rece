package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.jankowskirafal.oddamwdobrerece.categories.Category;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;

//    @NotEmpty(message = "Please select at least one category")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "donations_categories",
            joinColumns = @JoinColumn(name = "donation_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<Category> categories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institutionId", nullable = false)
    Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    DonationStatus status = DonationStatus.NEW; // default for new donation

//    @NotBlank(message = "Street is required")
    String street;
    String city;
    String zip;
    String phone;
    LocalDate pickUpDate;
    LocalTime pickUpTime;
    String pickUpComment;
    LocalDateTime creationDateTime = LocalDateTime.now();
    LocalDateTime updateDateTime;

}
