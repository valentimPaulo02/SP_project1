package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedDataDeid;
import pt.unl.fct.pds.proj1server.repository.MedDataDeidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/meddata/deid")
public class MedDataDeidController {

    @Autowired
    private MedDataDeidRepository medDataDeidRepository;

    @GetMapping
    public List<MedDataDeid> getAllMedDataDeid() {
        return medDataDeidRepository.findAll();
    }

    @GetMapping("/{id}")
    public MedDataDeid getMedDataDeidById(@PathVariable Long id) {
        return medDataDeidRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "MedData not found with id: " + id));
    }
}
