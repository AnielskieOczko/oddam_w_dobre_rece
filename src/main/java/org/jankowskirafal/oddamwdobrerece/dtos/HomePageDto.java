package org.jankowskirafal.oddamwdobrerece.dtos;

import org.jankowskirafal.oddamwdobrerece.institutions.InstitutionPair;

import java.util.List;

public record HomePageDto(
        int donatedGifts,
        int donatedBags,
        List<InstitutionPair> institutionPairs
) {
}
