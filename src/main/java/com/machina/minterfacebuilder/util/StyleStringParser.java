package com.machina.minterfacebuilder.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A token parser for parsing key-value pairs from strings.
 * Supports formats like: "key1: value1, key2: value2, key3: \"quoted value\""
 */
public class StyleStringParser {
    private final String input;
    private int position;
    private final char delimiter;
    private final char separator;

    /**
     * Create a new token parser.
     * @param input The input string to parse.
     * @param separator The character that separates key-value pairs (default: ',').
     * @param delimiter The character that separates keys from values (default: ':').
     */
    public StyleStringParser(String input, char separator, char delimiter) {
        this.input = input != null ? input : "";
        this.position = 0;
        this.separator = separator;
        this.delimiter = delimiter;
    }

    /**
     * Create a new token parser with default separator (',') and delimiter (':').
     * @param input The input string to parse.
     */
    public StyleStringParser(String input) {
        this(input, ',', ':');
    }

    /**
     * Parse the input string into a map of key-value pairs.
     * @return A map containing the parsed key-value pairs.
     */
    public Map<String, Object> parseToMap() {
        Map<String, Object> result = new HashMap<>();
        
        while (position < input.length()) {
            skipWhitespace();
            
            if (position >= input.length()) {
                break;
            }

            String key = parseKey();
            if (key == null || key.isEmpty()) {
                break;
            }

            skipWhitespace();
            
            if (position >= input.length() || input.charAt(position) != delimiter) {
                // No delimiter found, skip to next separator
                skipToNextSeparator();
                continue;
            }

            position++; // Skip delimiter
            skipWhitespace();

            String value = parseValue();
            
            // Don't overwrite Maps with Strings
            if (!result.containsKey(key) || !(result.get(key) instanceof Map<?, ?>)) {
                result.put(key, value);
            }

            skipWhitespace();
            
            if (position < input.length() && input.charAt(position) == separator) {
                position++; // Skip separator
            }
        }

        return result;
    }

    /**
     * Parse a key from the current position.
     * @return The parsed key, or null if not found.
     */
    private String parseKey() {
        int start = position;
        
        while (position < input.length()) {
            char ch = input.charAt(position);
            
            if (ch == delimiter || ch == separator) {
                break;
            }
            
            position++;
        }

        if (start == position) {
            return null;
        }

        return input.substring(start, position).trim();
    }

    /**
     * Parse a value from the current position.
     * Handles quoted strings and unquoted values.
     * @return The parsed value.
     */
    private String parseValue() {
        if (position >= input.length()) {
            return "";
        }

        char firstChar = input.charAt(position);
        
        // Handle quoted strings
        if (firstChar == '"' || firstChar == '\'') {
            return parseQuotedString(firstChar);
        }

        // Handle unquoted values
        return parseUnquotedValue();
    }

    /**
     * Parse a quoted string.
     * @param quoteChar The quote character ('"' or '\'').
     * @return The parsed string without quotes.
     */
    private String parseQuotedString(char quoteChar) {
        position++; // Skip opening quote
        
        StringBuilder value = new StringBuilder();
        boolean escaped = false;

        while (position < input.length()) {
            char ch = input.charAt(position);

            if (escaped) {
                value.append(ch);
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == quoteChar) {
                position++; // Skip closing quote
                return value.toString();
            } else {
                value.append(ch);
            }

            position++;
        }

        // Unclosed quote, return what we have
        return value.toString();
    }

    /**
     * Parse an unquoted value.
     * @return The parsed value.
     */
    private String parseUnquotedValue() {
        int start = position;

        while (position < input.length()) {
            char ch = input.charAt(position);

            if (ch == separator) {
                break;
            }

            position++;
        }

        String value = input.substring(start, position).trim();
        
        // Remove surrounding quotes if present (edge case)
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                value = value.substring(1, value.length() - 1);
            }
        }

        return value;
    }

    /**
     * Skip whitespace characters from the current position.
     */
    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    /**
     * Skip to the next separator character.
     */
    private void skipToNextSeparator() {
        while (position < input.length()) {
            if (input.charAt(position) == separator) {
                position++; // Skip separator
                break;
            }
            position++;
        }
    }

    /**
     * Parse a string with key-value pairs separated by commas and delimited by colons.
     * Convenience method for the common case.
     * @param input The input string (e.g., "key1: value1, key2: value2").
     * @return A map containing the parsed key-value pairs.
     */
    public static Map<String, Object> parseKeyValuePairs(String input) {
        return new StyleStringParser(input).parseToMap();
    }

    /**
     * Parse a string with key-value pairs using custom separator and delimiter.
     * @param input The input string.
     * @param separator The character that separates key-value pairs.
     * @param delimiter The character that separates keys from values.
     * @return A map containing the parsed key-value pairs.
     */
    public static Map<String, Object> parseKeyValuePairs(String input, char separator, char delimiter) {
        return new StyleStringParser(input, separator, delimiter).parseToMap();
    }
}