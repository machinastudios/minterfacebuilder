package com.machina.minterfacebuilder.model;

import java.util.Map;

import com.machina.minterfacebuilder.util.SerializationUtil;

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
public class LiteralValue {
    /**
     * The literal value string.
     */
    private final String value;

    /**
     * Create a literal value that will be inserted without quotes.
     * @param value The literal value string.
     * @return An InterfaceLiteral instance.
     */
    public static LiteralValue of(String value) {
        return new LiteralValue(value);
    }

    /**
     * Create a literal value that will be inserted without quotes.
     * @param value The literal value.
     * @return An InterfaceLiteral instance.
     */
    public static LiteralValue of(Object value) {
        // If the value is a primitive serializable type
        if (SerializationUtil.isSerializable(value)) {
            return new LiteralValue(String.valueOf(value));
        }

        throw new IllegalArgumentException("Value is not a primitive serializable type: " + value.getClass().getName());
    }

    /**
     * Create a literal value that will be inserted without quotes.
     * @param value The literal value.
     * @return An InterfaceLiteral instance.
     */
    public static LiteralValue of(Map<String, Object> value) {
        return of(value, ':');
    }

    /**
     * Create a literal value that will be inserted without quotes.
     * @param value The literal value.
     * @param delimiter The delimiter to use between entries.
     * @return An InterfaceLiteral instance.
     */
    public static LiteralValue of(Map<String, Object> value, char delimiter) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Object> entry : value.entrySet()) {
            builder.append(entry.getKey()).append(delimiter + " ").append(entry.getValue().toString()).append(", ");
        }

        return new LiteralValue(builder.toString());
    }

    /**
     * Private constructor. Use {@link #of(String)} to create instances.
     * @param value The literal value.
     */
    protected LiteralValue(String value) {
        this.value = value;
    }

    /**
     * Get the literal value.
     * @return The literal value string.
     */
    public String getValue() {
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
        LiteralValue that = (LiteralValue) obj;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
