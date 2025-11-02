package pt.unl.fct.pds.proj1server.privacy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PrivacyBudget {

    private final double epsilonPerQuery;
    private double remaining;

    public PrivacyBudget(@Value("${privacy.epsilon-total}") double epsilonTotal,
                        @Value("${privacy.epsilon-per-query}") double epsilonPerQuery) {
        this.epsilonPerQuery = epsilonPerQuery;
        this.remaining = epsilonTotal;
    }

    public double getEpsilonPerQuery() {
        return epsilonPerQuery;
    }

    public double getRemaining() {
        return remaining;
    }

    public boolean tryConsume() {
        if (remaining < epsilonPerQuery) {
            return false;
        }
        remaining -= epsilonPerQuery;
        return true;
    }
}
