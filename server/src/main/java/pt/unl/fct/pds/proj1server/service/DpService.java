package pt.unl.fct.pds.proj1server.service;

import org.springframework.beans.factory.annotation.Value;
import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.unl.fct.pds.proj1server.model.AverageRequest;
import pt.unl.fct.pds.proj1server.model.AverageResponse;
import pt.unl.fct.pds.proj1server.model.CountRequest;
import pt.unl.fct.pds.proj1server.model.CountResponse;
import pt.unl.fct.pds.proj1server.privacy.Laplace;
import pt.unl.fct.pds.proj1server.privacy.PrivacyBudget;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DpService {

    @PersistenceContext
    private EntityManager db;

    private final PrivacyBudget budget;
    private final int ageMin;
    private final int ageMax; 

    public DpService(PrivacyBudget budget,
                    @Value("${privacy.clip.age.min}") int ageMin,
                    @Value("${privacy.clip.age.max}") int ageMax) {
        this.budget = budget;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }


    // Handle count query for the database -----
    @Transactional(readOnly = true)
    public Optional<CountResponse> dpCount(CountRequest req) {
      if (req.getAttribute() != null && !req.getAttribute().isBlank()) {
          sanitizeField(req.getAttribute(), Set.of("diagnosis", "gender", "age"));
      }

      if (!budget.tryConsume()) return Optional.empty();

      long count = execCount(req.getAttribute(), req.getValue());

      double eps = budget.getEpsilonPerQuery();
      double sensitivity = 1.0;
      double scale = sensitivity / eps;

      double noisy = count + Laplace.sample(scale);
      int noisyInt = (int) Math.max(0, Math.round(noisy));

      String attribute = noisyInt == 0 ?
                          "none" : req.getAttribute() == null ? 
                          "none" : req.getAttribute() + " = " + req.getValue();

      return Optional.of(new CountResponse(
              attribute,
              noisyInt,
              eps,
              budget.getRemaining(),
              sensitivity
      ));
    }

    private long execCount(String attribute, String value) {
      if (attribute == null || attribute.isBlank()) {
          return 0;
      }

      TypedQuery<Long> query = db.createQuery(
              "SELECT COUNT(m) FROM MedData m WHERE m." + attribute + " = :val", Long.class);
      query.setParameter("val", value);
      return query.getSingleResult();
    }
    // Handle count query for the database -----


    // Handle average query for the database -----
    @Transactional(readOnly = true)
    public Optional<AverageResponse> dpAverage(AverageRequest req) {
      if (req.getFilterAttribute() != null && !req.getFilterAttribute().isBlank()) {
          sanitizeField(req.getFilterAttribute(), Set.of("diagnosis", "gender"));
      }

      if (!budget.tryConsume()) return Optional.empty();

      List<Integer> ages = execAverage(req.getFilterAttribute(), req.getFilterValue());
      long size = ages.size();

      double eps = budget.getEpsilonPerQuery();
      double epsHalf = eps / 2.0;
      double sumSensitivity = (double) (ageMax - ageMin);
      double countSensitivity = 1.0;

      // if json is empty (no filterattribute or value correct)
      if (size == 0) {
          return Optional.of(new AverageResponse(
                  "none", 
                  0.0, 
                  eps, 
                  budget.getRemaining(), 
                  sumSensitivity, 
                  countSensitivity
          ));
      }

      long clippedSum = ages.stream()
              .map(a -> Math.max(ageMin, Math.min(ageMax, a)))
              .mapToInt(Integer::intValue)
              .sum();

      double noisySum = clippedSum + Laplace.sample(sumSensitivity / epsHalf);
      double noisyCount = size + Laplace.sample(countSensitivity / epsHalf);
      if (noisyCount <= 1e-9) noisyCount = 1e-9;

      double noisyAvg = noisySum / noisyCount;

      return Optional.of(new AverageResponse(
              "age",
              noisyAvg,
              eps,
              budget.getRemaining(),
              sumSensitivity,
              countSensitivity
      ));
    }

    private List<Integer> execAverage(String filterAttribute, String filterValue) {
      if (filterAttribute == null || filterAttribute.isBlank()) {
          return db.createQuery("SELECT m.age FROM MedData m", Integer.class).getResultList();
      }

      TypedQuery<Integer> query = db.createQuery(
              "SELECT m.age FROM MedData m WHERE m." + filterAttribute + " = :val", Integer.class);
      query.setParameter("val", filterValue);
      return query.getResultList();
    }
    // Handle average query for the database -----


    // Ensuring sanitization for every query
    private String sanitizeField(String attribute, Set<String> allowed) {
        String sanitized = attribute.toLowerCase().trim();
        if (!allowed.contains(sanitized)) {
            throw new IllegalArgumentException("Unsupported attribute: " + attribute);
        }
        return sanitized;
    }
}
