package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedDataKAnon;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataKAnonRepository;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/meddata/kanon")
public class MedDataKAnonController {

    @Autowired
    private MedDataKAnonRepository medDataKAnonRepository;

    @GetMapping
    public List<MedDataKAnon> getAllMedDataKAnon() {
        return medDataKAnonRepository.findAll();
    }

    @GetMapping("/{id}")
    public MedDataKAnon getMedDataKAnonById(@PathVariable Long id) {
        return medDataKAnonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "MedData not found with id: " + id));
    }

    @PostMapping("/count")
    public CountResponse getMedDataKAnonCount(@RequestBody CountRequest countRequest) {
        // TODO: Implement 
        return new CountResponse(countRequest.getAttribute(), 0);
    }
}
