package com.machina.minterfacebuilder.util.customui;

public class CustomUIComponentProperty {
    private PropertyType type;
    private String key;
    private Object value;

    public CustomUIComponentProperty(PropertyType type, String key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    /**
     * Get the key of the property.
     * @return The key of the property.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get the value of the property.
     * @return The value of the property.
     */
    public String getValue() {
        String valueString = "";

        switch (this.type) {
            case STRING:
                valueString = "\"" + this.value.toString() + "\"";
                break;
            case LITERAL:
            case BOOLEAN:
            case DOUBLE:
            case FLOAT:
            case LONG:
            case SHORT:
            case BYTE:
            case CHAR:
            case INT:
                valueString = this.value.toString();
                break;
            default:
                throw new IllegalArgumentException("Invalid property type: " + this.type);
        }

        return valueString;
    }

    public enum PropertyType {
        STRING,
        INT,
        DOUBLE,
        BOOLEAN,
        FLOAT,
        LONG,
        SHORT,
        BYTE,
        CHAR,
        LITERAL
    }
}
