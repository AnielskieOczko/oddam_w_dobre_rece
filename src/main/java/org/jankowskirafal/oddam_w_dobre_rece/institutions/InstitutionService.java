package org.jankowskirafal.oddam_w_dobre_rece.institutions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public List<Institution> getAll() {

        return institutionRepository.findAll();
    }

    public Institution addInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }
}
