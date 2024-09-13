package org.jankowskirafal.oddamwdobrerece.institutions;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InstitutionService {

    Page<Institution> getAllInstitutions(int page, int size, String search);
    public List<Institution> getAllInstitutionsForDropdown();
    Optional<Institution> getInstitutionById(Long id);
    Institution saveInstitution(Institution institution);
    void updateInstitution(Institution institution);
    void deleteInstitution(Long id);

    List<Institution> getAll();

}
