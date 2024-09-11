package org.jankowskirafal.oddamwdobrerece.donations;

import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT SUM(d.quantity) FROM Donation d")
    Integer findTotalBagCount();

    @Query("SELECT COUNT(*) FROM Donation d")
    Integer findTotalDonationCount();


    @Modifying
    @Query("UPDATE Donation d " +
            "SET d.quantity = :quantity, d.street = :street, d.city = :city, d.zip = :zip, " +
            "d.phone = :phone, d.pickUpDate = :pickUpDate, d.pickUpTime = :pickUpTime, " +
            "d.pickUpComment = :pickUpComment, d.institution = :institution, d.user = :user " +
            "WHERE d.donationId = :donationId")
    void updateDonation(
            @Param("donationId") Long donationId,
            @Param("quantity") Integer quantity,
            @Param("street") String street,
            @Param("city") String city,
            @Param("zip") String zip,
            @Param("phone") String phone,
            @Param("pickUpDate") LocalDate pickUpDate,
            @Param("pickUpTime") LocalTime pickUpTime,
            @Param("pickUpComment") String pickUpComment,
            @Param("institution") Institution institution,
            @Param("user") User user);

    @Query("SELECT d FROM Donation d " +
            "LEFT JOIN d.user u " +
            "LEFT JOIN d.institution i " +
            "WHERE lower(d.street) LIKE lower(concat('%', :search, '%')) " +
            "OR lower(d.city) LIKE lower(concat('%', :search, '%')) " +
            "OR lower(d.zip) LIKE lower(concat('%', :search, '%')) " +
            "OR lower(u.email) LIKE lower(concat('%', :search, '%')) " +
            "OR lower(i.name) LIKE lower(concat('%', :search, '%'))")
    Page<Donation> searchDonations(@Param("search") String searchTerm, Pageable pageable);


}
