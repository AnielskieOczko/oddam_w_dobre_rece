package org.jankowskirafal.oddamwdobrerece.dtos;

import jakarta.validation.Valid;
import org.jankowskirafal.oddamwdobrerece.categories.Category;
import org.jankowskirafal.oddamwdobrerece.contactform.ContactForm;
import org.jankowskirafal.oddamwdobrerece.donations.Donation;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;

import java.util.List;

public record AddDonationFormDto(
        List<Category> categories,
        List<Institution> institutions,

        @Valid
        Donation donation,

        ContactForm contactForm

) {
}
