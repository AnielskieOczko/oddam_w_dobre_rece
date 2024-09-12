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
import java.util.List;

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

    @Query("SELECT DISTINCT d FROM Donation d LEFT JOIN d.categories c " +
            "WHERE (:institutionId IS NULL OR d.institution.institutionId = :institutionId) " +
            "AND (:pickUpDate IS NULL OR d.pickUpDate = :pickUpDate) " +
            "AND (:pickUpTime IS NULL OR d.pickUpTime = :pickUpTime) " +
            "AND (:city IS NULL OR LOWER(d.city) = LOWER(:city)) " +
            "AND (:categoryIds IS NULL OR c.categoryId IN :categoryIds)")
    Page<Donation> findAllWithFilters(@Param("institutionId") Long institutionId,
                                      @Param("pickUpDate") LocalDate pickUpDate,
                                      @Param("pickUpTime") LocalTime pickUpTime,
                                      @Param("city") String city,
                                      @Param("categoryIds") List<Long> categoryIds,
                                      Pageable pageable);

    @Query("SELECT DISTINCT d FROM Donation d LEFT JOIN d.categories c " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "       LOWER(d.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "       LOWER(d.street) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "       LOWER(d.institution.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:institutionId IS NULL OR d.institution.institutionId = :institutionId) " +
            "AND (:pickUpDate IS NULL OR d.pickUpDate = :pickUpDate) " +
            "AND (:pickUpTime IS NULL OR d.pickUpTime = :pickUpTime) " +
            "AND (:city IS NULL OR :city = '' OR LOWER(d.city) = LOWER(:city)) " +
            "AND (COALESCE(:categoryIds, NULL) IS NULL OR c.categoryId IN :categoryIds)")
    Page<Donation> searchAndFilterDonations(@Param("search") String search,
                                            @Param("institutionId") Long institutionId,
                                            @Param("pickUpDate") LocalDate pickUpDate,
                                            @Param("pickUpTime") LocalTime pickUpTime,
                                            @Param("city") String city,
                                            @Param("categoryIds") List<Long> categoryIds,
                                            Pageable pageable);

}
