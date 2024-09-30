package org.jankowskirafal.oddamwdobrerece.institutions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceImplTest {

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private InstitutionServiceImpl institutionService;

    private Institution institution1;
    private Institution institution2;

    @BeforeEach
    void setUp() {
        institution1 = new Institution();
        institution1.setInstitutionId(1L);
        institution1.setName("Institution 1");
        institution1.setDescription("Description 1");

        institution2 = new Institution();
        institution2.setInstitutionId(2L);
        institution2.setName("Institution 2");
        institution2.setDescription("Description 2");
    }

    @Test
    void getAllInstitutions_withoutSearch_shouldReturnAllInstitutions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Institution> expectedPage = new PageImpl<>(Arrays.asList(institution1, institution2));
        when(institutionRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<Institution> result = institutionService.getAllInstitutions(0, 10, null);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(institutionRepository).findAll(pageable);
    }

    @Test
    void getAllInstitutions_withSearch_shouldReturnFilteredInstitutions() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Institution> expectedPage = new PageImpl<>(Arrays.asList(institution1));
        when(institutionRepository.findByNameContainingIgnoreCase(eq("Institution 1"), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<Institution> result = institutionService.getAllInstitutions(0, 10, "Institution 1");

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(institutionRepository).findByNameContainingIgnoreCase(eq("Institution 1"), any(Pageable.class));
    }

    @Test
    void getAllInstitutionsForDropdown_shouldReturnAllInstitutions() {
        // Given
        List<Institution> expectedInstitutions = Arrays.asList(institution1, institution2);
        when(institutionRepository.findAll()).thenReturn(expectedInstitutions);

        // When
        List<Institution> result = institutionService.getAllInstitutionsForDropdown();

        // Then
        assertThat(result).isEqualTo(expectedInstitutions);
        verify(institutionRepository).findAll();
    }

    @Test
    void getInstitutionById_shouldReturnInstitution_whenExists() {
        // Given
        when(institutionRepository.findById(1L)).thenReturn(Optional.of(institution1));

        // When
        Optional<Institution> result = institutionService.getInstitutionById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(institution1);
        verify(institutionRepository).findById(1L);
    }

    @Test
    void getInstitutionById_shouldReturnEmpty_whenNotExists() {
        // Given
        when(institutionRepository.findById(3L)).thenReturn(Optional.empty());

        // When
        Optional<Institution> result = institutionService.getInstitutionById(3L);

        // Then
        assertThat(result).isEmpty();
        verify(institutionRepository).findById(3L);
    }

    @Test
    void saveInstitution_shouldReturnSavedInstitution() {
        // Given
        when(institutionRepository.save(institution1)).thenReturn(institution1);

        // When
        Institution result = institutionService.saveInstitution(institution1);

        // Then
        assertThat(result).isEqualTo(institution1);
        verify(institutionRepository).save(institution1);
    }

    @Test
    void updateInstitution_shouldCallRepositoryMethod() {
        // When
        institutionService.updateInstitution(institution1);

        // Then
        verify(institutionRepository).updateInstitution(institution1.getInstitutionId(), institution1.getName(), institution1.getDescription());
    }

    @Test
    void deleteInstitution_shouldCallRepositoryMethod() {
        // When
        institutionService.deleteInstitution(1L);

        // Then
        verify(institutionRepository).deleteById(1L);
    }

    @Test
    void getAll_shouldReturnAllInstitutions() {
        // Given
        List<Institution> expectedInstitutions = Arrays.asList(institution1, institution2);
        when(institutionRepository.findAll()).thenReturn(expectedInstitutions);

        // When
        List<Institution> result = institutionService.getAll();

        // Then
        assertThat(result).isEqualTo(expectedInstitutions);
        verify(institutionRepository).findAll();
    }
}