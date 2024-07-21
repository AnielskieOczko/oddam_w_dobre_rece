package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.dtos.HomePageDto;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionPair;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final InstitutionService institutionService;

    public Donation createDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public void saveAll(List<Donation> donations) {
        donationRepository.saveAll(donations);
    }

    public Integer findTotalBagQuantity() {
        return donationRepository.findTotalBagCount();
    }

    public Integer findTotalDonationCount() {
        return donationRepository.findTotalDonationCount();
    }

    public HomePageDto getDataForHomePage() {

        Integer donatedGifts = donationRepository.findTotalDonationCount();
        Integer donatedBags = donationRepository.findTotalBagCount();
        List<Institution> institutions = institutionService.getAll();
        List<InstitutionPair> institutionPairs;

        institutionPairs = IntStream.range(0, institutions.size())
                .filter(value -> value % 2 == 0)
                .mapToObj(value -> new InstitutionPair(
                        institutions.get(value),
                        value + 1 < institutions.size() ? institutions.get(value + 1) : null
                )).toList();


        log.info("DonatedGifts: {}", donatedGifts);
        log.info("donatedBags: {}", donatedBags);
        log.info("institutions: {}", institutions);
        log.info("institutions pairs: {}", institutionPairs);
        log.info("institutions pairs size: {}", institutionPairs.size());


        return new HomePageDto(donatedGifts, donatedBags, institutionPairs);
    }

}
