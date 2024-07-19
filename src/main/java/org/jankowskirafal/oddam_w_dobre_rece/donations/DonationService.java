package org.jankowskirafal.oddam_w_dobre_rece.donations;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class DonationService {

    DonationRepository donationRepository;

    public Donation createDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<Donation> saveAll(List<Donation> donations) {
        return donationRepository.saveAll(donations);
    }


}
