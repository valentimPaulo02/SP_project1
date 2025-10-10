package pt.unl.fct.pds.proj1server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import pt.unl.fct.pds.proj1server.model.MedDataKAnon;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidRepository;
import pt.unl.fct.pds.proj1server.repository.MedDataKAnonRepository;

import java.util.List;

@Service
public class KAnonymizationService {

    private final MedDataDeidRepository deidRepository;
    private final MedDataKAnonRepository kanonRepository;

    @Value("${privacy.k}")
    private int k;

    public KAnonymizationService(MedDataDeidRepository deidRepository,
                                MedDataKAnonRepository kanonRepository) {
        this.deidRepository = deidRepository;
        this.kanonRepository = kanonRepository;
    }

    public void runKAnonymization() {

        List<MedDataDeid> allDeid = deidRepository.findAll();
        kanonRepository.deleteAll();

        System.out.println("Running k-anonymity with k = " + k);
        System.out.println("Loaded " + allDeid.size() + " de-identified records.");

        // Step 4: (Later) Save generalized records into kanonRepository
        // kanonRepository.saveAll(anonymizedList);
    }
}
