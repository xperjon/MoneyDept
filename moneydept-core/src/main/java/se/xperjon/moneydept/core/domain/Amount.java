package se.xperjon.moneydept.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Jon-Erik
 */
public class Amount {

    private final BigDecimal value;

    public Amount(double value) {
        this.value = new BigDecimal(value);
    }

    public Amount(long value) {
        this.value = new BigDecimal(value);
    }

    public Amount(BigDecimal value) {
        this.value = value;
    }

    public Amount divide(Amount amount) {
        BigDecimal divideBy = amount.getAsBigDecimal();
        return new Amount(value.divide(divideBy, 2, RoundingMode.HALF_UP));
    }

    private BigDecimal getAsBigDecimal() {
        return value;
    }

    public Amount add(Amount amount) {
        BigDecimal addValue = amount.getAsBigDecimal();
        return new Amount(value.add(addValue));
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }

    public Amount multiply(Amount amount) {
        BigDecimal a = amount.getAsBigDecimal();
        return new Amount(value.multiply(a));
    }

}
