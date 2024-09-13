package org.jankowskirafal.oddamwdobrerece.donations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DonationFilterDTO {
    private Long institutionId;
    private LocalDate pickUpDate;
    private LocalTime pickUpTime;
    private String city;
    private List<Long> categoryIds;

    public boolean isEmpty() {
        return institutionId == null && pickUpDate == null && pickUpTime == null &&
                (city == null || city.isEmpty()) && (categoryIds == null || categoryIds.isEmpty());
    }
}
