package dev.hbeck.kdl.objects;

import dev.hbeck.kdl.print.PrintConfig;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;

/**
 * Representation of a KDL number. Numbers may be base 16, 10, 8, or 2 as stored in the radix field. Base 10 numbers may
 * be fractional, but all others are limited to integers.
 */
public class KDLNumber implements KDLValue {
    private static final KDLNumber ZERO_TWO = KDLNumber.zero(10);
    private static final KDLNumber ZERO_EIGTH = KDLNumber.zero(10);
    private static final KDLNumber ZERO_TEN = KDLNumber.zero(10);
    private static final KDLNumber ZERO_SIXTEEN = KDLNumber.zero(10);

    private final BigDecimal value;
    private final int radix;

    public KDLNumber(BigDecimal value, int radix) {
        this.value = Objects.requireNonNull(value);
        this.radix = radix;
    }

    public BigDecimal getAsBigDecimal() {
        return value;
    }

    @Override
    public void writeKDL(Writer writer, PrintConfig printConfig) throws IOException {
        switch (radix) {
            case 10:
                writer.write(value.toString().replace('E', printConfig.getExponentChar()));
                break;
            case 2:
                writer.write("0b");
                writer.write(value.toBigIntegerExact().toString(radix));
                break;
            case 8:
                writer.write("0o");
                writer.write(value.toBigIntegerExact().toString(radix));
                break;
            case 16:
                writer.write("0x");
                writer.write(value.toBigIntegerExact().toString(radix));
                break;
        }
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public KDLString getAsString() {
        return KDLString.from(value.toString());
    }

    @Override
    public Optional<KDLNumber> getAsNumber() {
        return Optional.of(this);
    }

    @Override
    public Optional<KDLBoolean> getAsBoolean() {
        return Optional.empty();
    }

    /**
     * Get the Zero value for a given radix, which must be one of [2, 8, 10, 16]
     *
     * @param radix the radix for the zero value
     * @return a new number with the value 0 and the given radix
     */
    public static KDLNumber zero(int radix) {
        switch (radix) {
            case 2:
                return ZERO_TWO;
            case 8:
                return ZERO_EIGTH;
            case 10:
                return ZERO_TEN;
            case 16:
                return ZERO_SIXTEEN;
            default:
                throw new RuntimeException("Radix must be one of: [2, 8, 10, 16]");
        }
    }

    /**
     * Get a new number with value 0 and base 10
     *
     * @return the new 0 number
     */
    public static KDLNumber zero() {
        return zero(10);
    }

    public static KDLNumber from(BigDecimal val, int radix) {
        return new KDLNumber(val, radix);
    }

    public static KDLNumber from(BigDecimal val) {
        return from(val, 10);
    }

    public static KDLNumber from(BigInteger val, int radix) {
        return new KDLNumber(new BigDecimal(val), radix);
    }

    public static KDLNumber from(BigInteger val) {
        return from(val, 10);
    }

    public static Optional<KDLNumber> from(String val, int radix) {
        return from(val).filter(v -> v.radix == radix);
    }

    /**
     * Parse the provided string into a KDLNumber if possible.
     *
     * @param val the string to parse
     * @return an optional wrapping the new KDLNumber if the parse was successful, or empty() if not
     */
    public static Optional<KDLNumber> from(String val) {
        if (val.length() == 0) {
            return Optional.empty();
        }

        final int radix;
        final String toParse;
        if (val.charAt(0) == '0') {
            if (val.length() == 1) {
                return Optional.of(KDLNumber.zero());
            }

            switch (val.charAt(1)) {
                case 'x':
                    radix = 16;
                    toParse = val.substring(2);
                    break;
                case 'o':
                    radix = 8;
                    toParse = val.substring(2);
                    break;
                case 'b':
                    radix = 2;
                    toParse = val.substring(2);
                    break;
                default:
                    radix = 10;
                    toParse = val;
            }
        } else {
            radix = 10;
            toParse = val;
        }

        BigDecimal parsed;
        try {
            if (radix == 10) {
                parsed = new BigDecimal(toParse);
            } else {
                parsed = new BigDecimal(new BigInteger(toParse, radix));
            }
        } catch (NumberFormatException e) {
            parsed = null;
        }

        return Optional.ofNullable(parsed).map(bd -> from(bd, radix));
    }

    public static KDLNumber from(long val, int radix) {
        return new KDLNumber(new BigDecimal(val), radix);
    }

    public static KDLNumber from(long val) {
        return from(val, 10);
    }

    public static KDLNumber from(double val, int radix) {
        return new KDLNumber(new BigDecimal(val), radix);
    }

    public static KDLNumber from(double bigDecimal) {
        return from(bigDecimal, 10);
    }

    @Override
    public String toString() {
        return "KDLNumber{" +
                "value=" + value +
                ", radix=" + radix +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KDLNumber)) return false;
        KDLNumber kdlNumber = (KDLNumber) o;
        return radix == kdlNumber.radix && Objects.equals(value, kdlNumber.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, radix);
    }
}
