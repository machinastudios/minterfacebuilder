package com.machina.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.model.ParsedCustomUITemplate;

/**
 * Example demonstrating how to use MInterfaceBuilder.
 * <p>
 * This example shows:
 * <ul>
 *   <li>Parsing HTML templates from strings</li>
 *   <li>Parsing HTML templates from files</li>
 *   <li>Using template variables</li>
 *   <li>Modifying variables after parsing</li>
 *   <li>Building Custom UI output</li>
 * </ul>
 */
public class ExampleUsage {

    public static void main(String[] args) {
        System.out.println("=== MInterfaceBuilder Example ===\n");

        // Example 1: Parse HTML from string
        example1_ParseFromString();

        // Example 2: Parse HTML from file
        example2_ParseFromFile();

        // Example 3: Using variables
        example3_UsingVariables();

        // Example 4: Modifying variables after parsing
        example4_ModifyVariables();

        // Example 5: Using CommonUI code with `:` prefix
        example5_CommonUICode();
    }

    /**
     * Example 1: Parse HTML template from a string.
     */
    private static void example1_ParseFromString() {
        System.out.println("Example 1: Parse HTML from String");

        String html = """
            <div id="container" style="padding: 10px">
                <label id="title" style="font-weight: bold">Hello World</label>
                <button id="submit" style="color: #ff0000">Submit</button>
            </div>
            """;

        ParsedCustomUITemplate template = InterfaceBuilder.parse(html);
        String uiString = template.build();

        System.out.println("Generated UI:");
        System.out.println(uiString);
        System.out.println();
    }

    /**
     * Example 2: Parse HTML template from a file.
     */
    private static void example2_ParseFromFile() {
        System.out.println("Example 2: Parse HTML from File");

        try {
            // Get the template file path
            Path templatePath = Paths.get("src/main/resources/templates/example-page.html")
                .toAbsolutePath()
                .normalize();

            System.out.println("Loading template from: " + templatePath);

            // Parse the template file
            ParsedCustomUITemplate template = InterfaceBuilder.parse(templatePath);

            // Build the UI
            String uiString = template.build();

            System.out.println("Generated UI:");
            System.out.println(uiString);
            System.out.println();

            // Enable file watching for automatic reloading
            InterfaceBuilder.watchFileChanges(templatePath);
            System.out.println("File watching enabled for automatic reloading");
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error parsing file: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    /**
     * Example 3: Using template variables.
     */
    private static void example3_UsingVariables() {
        System.out.println("Example 3: Using Template Variables");

        String html = """
            <script type="text/customui">
            @Title = "My Application"
            @ButtonColor = "#2196F3"
            @Enabled = true
            </script>
            
            <div id="container">
                <label id="title" style="color: @ButtonColor">@Title</label>
                <button id="button" style="background: @ButtonColor">Click Me</button>
            </div>
            """;

        ParsedCustomUITemplate template = InterfaceBuilder.parse(html);

        // Access variables
        System.out.println("Template variables:");
        Map<String, String> variables = template.getVariables();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue());
        }

        String uiString = template.build();
        System.out.println("\nGenerated UI:");
        System.out.println(uiString);
        System.out.println();
    }

    /**
     * Example 4: Modifying variables after parsing.
     */
    private static void example4_ModifyVariables() {
        System.out.println("Example 4: Modifying Variables After Parsing");

        String html = """
            <script type="text/customui">
            @Title = "Initial Title"
            @ButtonText = "Initial Button"
            </script>
            
            <div id="container">
                <label id="title">@Title</label>
                <button id="button">@ButtonText</button>
            </div>
            """;

        ParsedCustomUITemplate template = InterfaceBuilder.parse(html);

        System.out.println("Initial UI:");
        System.out.println(template.build());

        // Modify variables
        template.setVariable("Title", "Updated Title");
        template.setVariable("ButtonText", "Updated Button");

        System.out.println("\nUpdated UI (note: variables are substituted during parsing):");
        System.out.println(template.build());
        System.out.println();

        // Note: Variables are substituted during parsing, so modifying them after
        // parsing won't affect the output. To apply new values, you would need to
        // reparse with updated variables:
        Map<String, String> newVariables = new HashMap<>();
        newVariables.put("Title", "New Title via Map");
        newVariables.put("ButtonText", "New Button via Map");

        ParsedCustomUITemplate newTemplate = InterfaceBuilder.parse(html, newVariables);
        System.out.println("Reparsed UI with new variables:");
        System.out.println(newTemplate.build());
        System.out.println();
    }

    /**
     * Example 5: Using dynamic binding with `:` prefix in HTML attributes.
     * The `:` prefix (like Vue binding) allows you to use CommonUI code in attributes.
     */
    private static void example5_CommonUICode() {
        System.out.println("Example 5: Using Dynamic Binding with `:` Prefix");

        String html = """
            <script type="text/customui">
            @Title = "My Application"
            @LeftPadding = 10
            </script>
            
            <div id="container">
                <label id="title">@Title</label>
                <span :text="@Title" :padding="(Left: @LeftPadding)" />
                <span text="@Title" />
            </div>
            """;

        ParsedCustomUITemplate template = InterfaceBuilder.parse(html);
        String uiString = template.build();

        System.out.println("Generated UI (with dynamic bindings):");
        System.out.println(uiString);
        System.out.println();
    }
}
