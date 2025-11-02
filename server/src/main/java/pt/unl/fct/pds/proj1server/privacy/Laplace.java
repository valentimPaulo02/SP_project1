package pt.unl.fct.pds.proj1server.privacy;

import java.security.SecureRandom;

public final class Laplace {
    private static final SecureRandom RNG = new SecureRandom();

    private Laplace() {}

    public static double sample(double scale) {
        if (scale <= 0) return 0.0;
        double u = RNG.nextDouble() - 0.5;
        double sign = Math.signum(u);
        return -scale * sign * Math.log(1 - 2 * Math.abs(u));
    }
}
