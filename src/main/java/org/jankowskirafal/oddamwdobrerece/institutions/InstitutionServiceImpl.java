package org.jankowskirafal.oddamwdobrerece.institutions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class InstitutionServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionRepository;


    @Override
    public Page<Institution> getAllInstitutions(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (search != null && !search.isEmpty()) {
            return institutionRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return institutionRepository.findAll(pageable);
    }

    @Override
    public Optional<Institution> getInstitutionById(Long id) {
        return institutionRepository.findById(id);
    }

    @Override
    public Institution saveInstitution(Institution institution) {
        return institutionRepository.save(institution);
    }

    @Override
    public void updateInstitution(Institution institution) {
        institutionRepository.updateInstitution(institution.getInstitutionId(), institution.getName(), institution.getDescription());
    }

    @Override
    public void deleteInstitution(Long id) {
        institutionRepository.deleteById(id);
    }

    @Override
    public List<Institution> getAll() {
        return institutionRepository.findAll();
    }

}
