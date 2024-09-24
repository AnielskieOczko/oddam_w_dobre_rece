package org.jankowskirafal.oddamwdobrerece.donations;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.dtos.HomePageDto;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionPair;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionServiceImpl;
import org.jankowskirafal.oddamwdobrerece.users.User;
import org.jankowskirafal.oddamwdobrerece.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final InstitutionServiceImpl institutionServiceImpl;
    private final UserRepository userRepository;

//    public Donation createDonation(Donation donation) {
//        return donationRepository.save(donation);
//    }

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
        List<Institution> institutions = institutionServiceImpl.getAll();
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

    public Page<Donation> getDonations(String search, DonationFilterDTO filterDTO, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        log.info("Logged in user: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        log.info("Principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        log.info("Authorities: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());

        if (isAdmin()) {
            // Admin - fetch all donations (with search and filters)
            return donationRepository.searchAndFilterDonations(
                    search,
                    filterDTO.getInstitutionId(),
                    filterDTO.getPickUpDate(),
                    filterDTO.getPickUpTime(),
                    filterDTO.getCity(),
                    filterDTO.getCategoryIds(),
                    pageable
            );
        } else {
            // Regular User - fetch donations for the current user (with search and filters)
            User currentUser = getCurrentUser();
            return donationRepository.searchAndFilterDonationsForUser(
                    search,
                    filterDTO.getInstitutionId(),
                    filterDTO.getPickUpDate(),
                    filterDTO.getPickUpTime(),
                    filterDTO.getCity(),
                    filterDTO.getCategoryIds(),
                    currentUser,
                    pageable
            );
        }
    }

    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    public void updateDonation(Donation donation) {

        donation.setUpdateDateTime(LocalDateTime.now());

        donationRepository.updateDonation(
                donation.getDonationId(),
                donation.getQuantity(),
                donation.getStreet(),
                donation.getCity(),
                donation.getZip(),
                donation.getPhone(),
                donation.getPickUpDate(),
                donation.getPickUpTime(),
                donation.getPickUpComment(),
                donation.getInstitution(),
                donation.getUser(),
                donation.getStatus(),
                donation.getUpdateDateTime()
        );
    }

    public void updateDonationStatus(Donation donation) {

        donation.setUpdateDateTime(LocalDateTime.now());

        donationRepository.updateDonationStatus(
                donation.getDonationId(),
                donation.getStatus(),
                donation.getUpdateDateTime()
        );
    }

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    private User getCurrentUser() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + userEmail));
    }

    // Helper method to check if the current user has the ADMIN role
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("ROLE_ADMIN"));
    }

}