package org.jankowskirafal.oddamwdobrerece.donations;

import org.jankowskirafal.oddamwdobrerece.dtos.HomePageDto;
import org.jankowskirafal.oddamwdobrerece.users.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface DonationService {

    void saveAll(List<Donation> donations);

    Integer findTotalBagQuantity();

    Integer findTotalDonationCount();

    HomePageDto getDataForHomePage();

    Page<Donation> getDonations(String search, DonationFilterDTO filterDTO, int page, int size);

    void deleteDonation(Long id);

    void updateDonation(Donation donation);

    void updateDonationStatus(Donation donation);

    Donation saveDonation(Donation donation);

    Optional<Donation> getDonationById(Long id);

}
