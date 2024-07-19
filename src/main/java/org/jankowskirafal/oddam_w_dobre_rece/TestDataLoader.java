package org.jankowskirafal.oddam_w_dobre_rece;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddam_w_dobre_rece.categories.Category;
import org.jankowskirafal.oddam_w_dobre_rece.categories.CategoryService;
import org.jankowskirafal.oddam_w_dobre_rece.categories.CategoryType;
import org.jankowskirafal.oddam_w_dobre_rece.donations.Donation;
import org.jankowskirafal.oddam_w_dobre_rece.donations.DonationService;
import org.jankowskirafal.oddam_w_dobre_rece.institutions.Institution;
import org.jankowskirafal.oddam_w_dobre_rece.institutions.InstitutionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
@Transactional
public class TestDataLoader {


    InstitutionService institutionService;
    CategoryService categoryService;
    DonationService donationService;

    private static final Random random = new Random();

    // Sample data for addresses and comments
    private static final String[] STREETS = {
            "123 Main St", "456 Oak Ave", "789 Pine Ln", "1001 Elm Blvd", "1212 Birch Rd"
    };
    private static final String[] CITIES = {
            "Anytown", "Springfield", "Oakville", "Riverdale", "Hilltop"
    };
    private static final String[] ZIP_CODES = {
            "12345", "67890", "54321", "09876", "10101"
    };
    private static final String[] COMMENTS = {
            "Some items in good condition.",
            "Clothes and toys for donation.",
            "Books and furniture, please collect by Friday.",
            "Gently used electronics.",
            "Thank you for your support!"
    };

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartUp() {

        log.info("Starting up");
        log.info("Loading categories");
        loadCategories();

        log.info("Loading institutions");
        loadInstitutions();

        log.info("Loading donations");
        loadDonations();


        log.info("Loading test data completed");

    }

    private void loadCategories() {
        List<CategoryType> categoryTypes = List.of(
                CategoryType.BOOKS,
                CategoryType.CLOTHING,
                CategoryType.FOOD);

        categoryTypes.forEach(categoryType -> {
            Category category = new Category();
            category.setName(categoryType);
            categoryService.addCategory(category);
        });
    }

    private void loadInstitutions() {
        // Create Institution objects
        Institution institution1 = new Institution();
        institution1.setName("Dbam o Zdrowie");
        institution1.setDescription("Pomoc dzieciom z ubogich rodzin.");

        Institution institution2 = new Institution();
        institution2.setName("A kogo");
        institution2.setDescription("Pomoc wybudzaniu dzieci ze spiaczki.");

        Institution institution3 = new Institution();
        institution3.setName("Dla dzieci");
        institution3.setDescription("Pomoc osobom znajdujacym sie w trudnej sytuacji zyciowej.");

        Institution institution4 = new Institution();
        institution4.setName("Bez domu");
        institution4.setDescription("Pomoc dla osob nie posiadajacych miejsca zamieszkania");


        institutionService.addInstitution(institution1);
        institutionService.addInstitution(institution2);
        institutionService.addInstitution(institution3);
        institutionService.addInstitution(institution4);
    }

    @Transactional
    protected void loadDonations() {
        List<Institution> institutions = institutionService.getAll();
        List<Donation> donations = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Donation donation = new Donation();

            donation.setQuantity(random.nextInt(10) + 1);

            Set<Category> donationCategories = new HashSet<>();
            int numCategories = random.nextInt(3) + 1;

            for (int j = 0; j < numCategories; j++) {
                // Fetch a random existing Category from the database
                Long randomCategoryId = (long) random.nextInt(categoryService.getAllCategories().size()) + 1; // Assuming you have at least one category
                Optional<Category> category = Optional.ofNullable(categoryService.getCategoryById(randomCategoryId));
                category.ifPresent(donationCategories::add);
            }
            donation.setCategories(donationCategories);

            // Randomly assign an institution (only ONCE)
            donation.setInstitution(institutions.get(random.nextInt(institutions.size())));

            donation.setStreet(STREETS[random.nextInt(STREETS.length)]);
            donation.setCity(CITIES[random.nextInt(CITIES.length)]);
            donation.setZip(ZIP_CODES[random.nextInt(ZIP_CODES.length)]);

            LocalDate today = LocalDate.now();
            int daysToAdd = random.nextInt(14);
            donation.setPickUpDate(today.plusDays(daysToAdd));

            donation.setPickUpTime(LocalTime.of(random.nextInt(12) + 8, 0));

            donation.setPickUpComment(COMMENTS[random.nextInt(COMMENTS.length)]);

            donations.add(donation);
        }

        donationService.saveAll(donations);
    }

}
