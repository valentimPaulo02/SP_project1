package pt.unl.fct.pds.proj1server.controller;

import pt.unl.fct.pds.proj1server.service.KAnonymizationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/kanonymization")
public class KAnonymizationController {

    private final KAnonymizationService kAnonymizationService;

    public KAnonymizationController(KAnonymizationService kAnonymizationService) {
        this.kAnonymizationService = kAnonymizationService;
    }


    @PostMapping("/run")
    public Map<String, Integer> runKAnonymity() {
        Map<String, Integer> response = new HashMap<>();
        try {
            kAnonymizationService.runKAnonymization();
            response.put("success", 1);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", 0);
        }
        return response;
    }
}
