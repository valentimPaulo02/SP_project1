package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.model.MedData;
import pt.unl.fct.pds.proj1server.model.AverageRequest;
import pt.unl.fct.pds.proj1server.model.AverageResponse;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.repository.MedDataRepository;
import pt.unl.fct.pds.proj1server.service.DpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/meddata")
public class MedDataController {

    @Autowired
    private MedDataRepository medDataRepository;

    private final DpService dpService;

    public MedDataController(DpService dpService) {
        this.dpService = dpService;
    }

    @GetMapping
    public List<MedData> getAllMedData() {
        return medDataRepository.findAll();
    }

    @GetMapping("/{id}")
    public MedData getMedDataById(@PathVariable Long id) {
        return medDataRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "MedData not found with id: " + id));
    }

    @PostMapping("/count")
    public CountResponse getMedDataCount(@RequestBody CountRequest countRequest) {
        var res = dpService.dpCount(countRequest);
        if (res.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Privacy budget exhausted");
        }
        return res.get();
    }

    @PostMapping("/average")
    public AverageResponse getMedDataAverageAge(@RequestBody AverageRequest req) {
        var res = dpService.dpAverage(req);
        if (res.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Privacy budget exhausted");
        }
        return res.get();
    }
}
