package org.jankowskirafal.oddamwdobrerece.institutions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    Optional<Institution> findByName(String name);

    Page<Institution> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Institution i SET i.name = :name, i.description = :description WHERE i.institutionId = :id")
    void updateInstitution(@Param("id") Long id, @Param("name") String name, @Param("description") String description);

}
