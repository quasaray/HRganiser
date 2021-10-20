package com.quasaray.hrganiser.repository;

import com.quasaray.hrganiser.domain.Organisation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Organisation entity.
 */
@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long>, JpaSpecificationExecutor<Organisation> {
    @Query(
        value = "select distinct organisation from Organisation organisation left join fetch organisation.staff",
        countQuery = "select count(distinct organisation) from Organisation organisation"
    )
    Page<Organisation> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct organisation from Organisation organisation left join fetch organisation.staff")
    List<Organisation> findAllWithEagerRelationships();

    @Query("select organisation from Organisation organisation left join fetch organisation.staff where organisation.id =:id")
    Optional<Organisation> findOneWithEagerRelationships(@Param("id") Long id);
}
