package com.machina.minterfacebuilder.model;

/**
 * Represents a literal value that should not be quoted when substituted.
 * <p>
 * This class is used to wrap literal values that should be inserted directly
 * into the output without quotes. For example, numbers, boolean values, or
 * other non-string values.
 * </p>
 * <p>
 * <b>Usage:</b>
 * </p>
 * <pre>
 * // Create a literal boolean value
 * InterfaceLiteral literal = InterfaceLiteral.of("true");
 *
 * // Create a literal number
 * InterfaceLiteral number = InterfaceLiteral.of("123");
 * </pre>
 */
public class InterfaceLiteral {
    /**
     * The literal value string.
     */
    private final String value;

    /**
     * Private constructor. Use {@link #of(String)} to create instances.
     * @param value The literal value.
     */
    private InterfaceLiteral(String value) {
        this.value = value;
    }

    /**
     * Create a literal value that will be inserted without quotes.
     * @param value The literal value string.
     * @return An InterfaceLiteral instance.
     */
    public static InterfaceLiteral of(String value) {
        return new InterfaceLiteral(value);
    }

    /**
     * Get the literal value.
     * @return The literal value string.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InterfaceLiteral that = (InterfaceLiteral) obj;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
