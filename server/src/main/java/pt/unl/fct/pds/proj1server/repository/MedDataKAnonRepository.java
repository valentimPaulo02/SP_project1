package pt.unl.fct.pds.proj1server.repository;

import pt.unl.fct.pds.proj1server.model.MedDataKAnon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedDataKAnonRepository extends JpaRepository<MedDataKAnon, Long> {
    // Custom query methods can be defined here
}

