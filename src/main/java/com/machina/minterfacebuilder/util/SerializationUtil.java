package com.machina.minterfacebuilder.util;

public class SerializationUtil {
    /**
     * Check if the value is a primitive serializable type.
     * @param value The value to check.
     * @return True if the value is a primitive serializable type, false otherwise.
     */
    public static boolean isSerializable(Object value) {
        return value instanceof String
            || value instanceof Integer
            || value instanceof Double
            || value instanceof Float
            || value instanceof Long
            || value instanceof Short
            || value instanceof Byte
            || value instanceof Boolean;
    }
}
