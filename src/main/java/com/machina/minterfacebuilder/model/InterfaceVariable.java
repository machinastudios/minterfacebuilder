package com.machina.minterfacebuilder.model;

/**
 * Represents a typed variable value in InterfaceBuilder templates.
 * <p>
 * This class encapsulates variable values with their types (boolean, color, string, or literal).
 * Variables can be used in templates and will be substituted appropriately based on their type.
 * </p>
 * <p>
 * <b>Supported types:</b>
 * </p>
 * <ul>
 *   <li><b>Boolean</b>: true/false values</li>
 *   <li><b>Color</b>: Color values (hex format like #FF0000)</li>
 *   <li><b>String</b>: Regular string values</li>
 *   <li><b>Literal</b>: Literal values that are inserted without quotes</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * </p>
 * <pre>
 * // Create a boolean variable
 * InterfaceVariable boolVar = InterfaceVariable.booleanValue(true);
 *
 * // Create a color variable
 * InterfaceVariable colorVar = InterfaceVariable.colorValue("#FF0000");
 *
 * // Create a string variable
 * InterfaceVariable stringVar = InterfaceVariable.stringValue("Hello");
 *
 * // Create a literal variable
 * InterfaceVariable literalVar = InterfaceVariable.literalValue("123");
 * </pre>
 */
public class InterfaceVariable {
    /**
     * Variable type enum.
     */
    public enum Type {
        BOOLEAN,
        COLOR,
        STRING,
        LITERAL
    }

    /**
     * The variable value as a string.
     */
    private final String value;

    /**
     * The variable type.
     */
    private final Type type;

    /**
     * Private constructor. Use factory methods to create instances.
     * @param value The variable value.
     * @param type The variable type.
     */
    private InterfaceVariable(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Create a boolean variable.
     * @param value The boolean value.
     * @return An InterfaceVariable with boolean type.
     */
    public static InterfaceVariable booleanValue(boolean value) {
        return new InterfaceVariable(String.valueOf(value), Type.BOOLEAN);
    }

    /**
     * Create a boolean variable from string.
     * @param value The boolean value as string ("true" or "false").
     * @return An InterfaceVariable with boolean type.
     */
    public static InterfaceVariable booleanValue(String value) {
        // Normalize boolean strings
        String normalized = value.trim().toLowerCase();
        if (normalized.equals("true") || normalized.equals("1") || normalized.equals("yes")) {
            return new InterfaceVariable("true", Type.BOOLEAN);
        } else {
            return new InterfaceVariable("false", Type.BOOLEAN);
        }
    }

    /**
     * Create a color variable.
     * @param value The color value (hex format like #FF0000).
     * @return An InterfaceVariable with color type.
     */
    public static InterfaceVariable colorValue(String value) {
        return new InterfaceVariable(value.trim(), Type.COLOR);
    }

    /**
     * Create a string variable.
     * @param value The string value.
     * @return An InterfaceVariable with string type.
     */
    public static InterfaceVariable stringValue(String value) {
        return new InterfaceVariable(value, Type.STRING);
    }

    /**
     * Create a literal variable.
     * @param value The literal value.
     * @return An InterfaceVariable with literal type.
     */
    public static InterfaceVariable literalValue(String value) {
        return new InterfaceVariable(value, Type.LITERAL);
    }

    /**
     * Create a literal variable from an InterfaceLiteral.
     * @param literal The InterfaceLiteral instance.
     * @return An InterfaceVariable with literal type.
     */
    public static InterfaceVariable literalValue(LiteralValue literal) {
        return new InterfaceVariable(literal.getValue(), Type.LITERAL);
    }

    /**
     * Get the variable value as a string.
     * @return The variable value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the variable type.
     * @return The variable type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the value formatted for substitution in templates.
     * <p>
     * For string values, this returns the value as-is (will be used inside quotes).
     * For literal values, this returns the value without quotes.
     * For boolean values, this returns "true" or "false".
     * For color values, this returns the color hex string.
     * </p>
     * @return The formatted value for substitution.
     */
    public String getSubstitutionValue() {
        return value;
    }

    /**
     * Check if this variable is a boolean type.
     * @return True if the type is BOOLEAN.
     */
    public boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    /**
     * Check if this variable is a color type.
     * @return True if the type is COLOR.
     */
    public boolean isColor() {
        return type == Type.COLOR;
    }

    /**
     * Check if this variable is a string type.
     * @return True if the type is STRING.
     */
    public boolean isString() {
        return type == Type.STRING;
    }

    /**
     * Check if this variable is a literal type.
     * @return True if the type is LITERAL.
     */
    public boolean isLiteral() {
        return type == Type.LITERAL;
    }

    @Override
    public String toString() {
        return "InterfaceVariable{" +
            "value='" + value + '\'' +
            ", type=" + type +
            '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InterfaceVariable that = (InterfaceVariable) obj;
        if (type != that.type) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
