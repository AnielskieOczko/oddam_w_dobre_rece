package org.jankowskirafal.oddamwdobrerece.donations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT SUM(d.quantity) FROM Donation d")
    Integer findTotalBagCount();

    @Query("SELECT COUNT(*) FROM Donation d")
    Integer findTotalDonationCount();


}
