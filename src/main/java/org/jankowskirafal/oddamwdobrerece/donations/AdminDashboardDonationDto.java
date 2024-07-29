package org.jankowskirafal.oddamwdobrerece.donations;

import org.jankowskirafal.oddamwdobrerece.categories.Category;
import org.jankowskirafal.oddamwdobrerece.institutions.Institution;
import org.jankowskirafal.oddamwdobrerece.users.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AdminDashboardDonationDto(
        Long donationId,
        Integer quantity,
        List<Category> categories,
        Institution institution,
        User user,
        String street,
        String city,
        String zip,
        String phone,
        LocalDate pickUpDate,
        LocalTime pickUpTime,
        String pickUpComment
) {
}
