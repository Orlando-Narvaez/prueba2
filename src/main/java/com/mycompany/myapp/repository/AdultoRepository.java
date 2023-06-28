package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Adulto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Adulto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdultoRepository extends JpaRepository<Adulto, Long> {}
