package org.jankowskirafal.oddamwdobrerece;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jankowskirafal.oddamwdobrerece.categories.Category;
import org.jankowskirafal.oddamwdobrerece.categories.CategoryService;
import org.jankowskirafal.oddamwdobrerece.donations.Donation;
import org.jankowskirafal.oddamwdobrerece.donations.DonationService;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionService;
import org.jankowskirafal.oddamwdobrerece.users.Authority;
import org.jankowskirafal.oddamwdobrerece.users.AuthorityService;
import org.jankowskirafal.oddamwdobrerece.users.User;
import org.jankowskirafal.oddamwdobrerece.users.UserServiceImpl;
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


    private final InstitutionService institutionService;
    private final CategoryService categoryService;
    private final DonationService donationService;
    private final UserServiceImpl userServiceImpl;
    private final AuthorityService authorityService;

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

        log.info("Loading authorities");
        loadAuthorities();

        log.info("Loading users");
        loadUsers();

        log.info("Loading categories");
        loadCategories();

        log.info("Loading institutions");
        loadInstitutions();

        log.info("Loading donations");
        loadDonations();

        log.info("Loading test data completed");

    }

    private void loadAuthorities() {
        Authority authority = new Authority();
        authority.setName("ROLE_USER");

        Authority authority1 =  new Authority();
        authority1.setName("ROLE_ADMIN");

        authorityService.addNewAuthority(authority);
        authorityService.addNewAuthority(authority1);


    }

    private void loadUsers() {

        User user = new User();
        user.setEmail("testUser@gmail.com");
        user.setPassword("password");

        User user1 = new User();
        user1.setEmail("testUser1@gmail.com");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setEmail("testUser2@gmail.com");
        user2.setPassword("password2");

        Set<String> roles = Set.of("ROLE_USER");
        Set<String> roles1 = Set.of("ROLE_USER", "ROLE_ADMIN");
        Set<String> roles2 = Set.of("ROLE_ADMIN");



        userServiceImpl.createUser(user, roles);
        userServiceImpl.createUser(user1, roles1);
        userServiceImpl.createUser(user2, roles2);


    }

    private void loadCategories() {
        List<String> categoryTypes = List.of(
                "ksiazki",
                "ubrania",
                "zabawki");

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

        Institution institution5 = new Institution();
        institution5.setName("Test institution");
        institution5.setDescription("Test institution description");

        Institution institution6 = new Institution();
        institution6.setName("Test institution2");
        institution6.setDescription("Test institution description2");


        institutionService.saveInstitution(institution1);
        institutionService.saveInstitution(institution2);
        institutionService.saveInstitution(institution3);
        institutionService.saveInstitution(institution4);
        institutionService.saveInstitution(institution5);
        institutionService.saveInstitution(institution6);
    }

    @Transactional
    protected void loadDonations() {
        List<Institution> institutions = institutionService.getAll();
        List<Donation> donations = new ArrayList<>();
        List<User> users = userServiceImpl.getAll();
        users.add(null);

        for (int i = 0; i < 10; i++) {
            Donation donation = new Donation();

            donation.setQuantity(random.nextInt(10) + 1);

            List<Category> donationCategories = new ArrayList<>();
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

            donation.setUser(users.get(random.nextInt(users.size())));

            donations.add(donation);
        }

        donationService.saveAll(donations);
    }

}
