package org.jankowskirafal.oddamwdobrerece.institutions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InstitutionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Test
    void findByName_shouldReturnInstitution_whenNameExists() {
        // Given
        Institution institution = new Institution();
        institution.setName("Test Institution");
        institution.setDescription("Test Description");
        entityManager.persist(institution);
        entityManager.flush();

        // When
        Optional<Institution> found = institutionRepository.findByName("Test Institution");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Institution");
        assertThat(found.get().getDescription()).isEqualTo("Test Description");
    }

    @Test
    void findByName_shouldReturnEmpty_whenNameDoesNotExist() {
        // When
        Optional<Institution> found = institutionRepository.findByName("Non-existent Institution");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatchingInstitutions() {
        // Given
        Institution institution1 = new Institution();
        institution1.setName("Test Institution");
        institution1.setDescription("Description 1");
        entityManager.persist(institution1);

        Institution institution2 = new Institution();
        institution2.setName("Another Test");
        institution2.setDescription("Description 2");
        entityManager.persist(institution2);

        entityManager.flush();

        // When
        Page<Institution> found = institutionRepository.findByNameContainingIgnoreCase("test", PageRequest.of(0, 10));

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent()).extracting(Institution::getName)
                .containsExactlyInAnyOrder("Test Institution", "Another Test");
    }

    @Test
    void updateInstitution_shouldUpdateNameAndDescription() {
        // Given
        Institution institution = new Institution();
        institution.setName("Original Name");
        institution.setDescription("Original Description");
        entityManager.persist(institution);
        entityManager.flush();

        Long id = institution.getInstitutionId();

        // When
        institutionRepository.updateInstitution(id, "Updated Name", "Updated Description");
        entityManager.clear();

        // Then
        Institution updatedInstitution = entityManager.find(Institution.class, id);
        assertThat(updatedInstitution.getName()).isEqualTo("Updated Name");
        assertThat(updatedInstitution.getDescription()).isEqualTo("Updated Description");
    }
}