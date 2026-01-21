package com.machina.minterfacebuilder.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

public class FnCall extends LiteralValue {
    /**
     * Create a new FnCall.
     * @param functionName The name of the function.
     * @param arguments The arguments of the function.
     * @return A new FnCall.
     */
    public static FnCall of(String functionName, Map<String, Object> arguments) {
        return new FnCall(functionName, arguments);
    }

    private final String functionName;
    private final Map<String, Object> arguments;

    public FnCall(String functionName, Map<String, Object> arguments) {
        super(null);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    /**
     * Get the value of the FnCall.
     * @return The value of the FnCall.
     */
    public String getValue(int indentLevel) {
        String argumentsString;

        if (this.arguments.isEmpty()) {
            argumentsString = "";
        } else {
            List<String> arguments = new ArrayList<>();

            // Iterate over the arguments and add them to the list
            for (Map.Entry<String, Object> entry : this.arguments.entrySet()) {
                // If the value is a map, format it
                if (entry.getValue() instanceof Map<?, ?>) {
                    arguments.add(
                        entry.getKey() + ": "
                        + ComponentBuilder.formatNestedMap((Map<?, ?>) entry.getValue(), indentLevel, ComponentBuilder.NestingStyle.PARENTHESIS)
                    );
                } else {
                    arguments.add(
                        entry.getKey() + ": "
                        + ComponentBuilder.stringifyValue(entry.getValue())
                    );
                }
            }

            argumentsString = (
                ComponentBuilder.INDENT.repeat(indentLevel)
                + String.join(",\n" + ComponentBuilder.INDENT.repeat(indentLevel + 1), arguments)
            );
        }

        return (
            this.functionName + "(\n"
                + argumentsString + "\n" + ComponentBuilder.INDENT.repeat(indentLevel)
            + ")");
    }
}
