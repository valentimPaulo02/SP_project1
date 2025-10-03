package pt.unl.fct.pds.proj1server.repository;

import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedDataDeidRepository extends JpaRepository<MedDataDeid, Long> {
    // Custom query methods can be defined here
}
