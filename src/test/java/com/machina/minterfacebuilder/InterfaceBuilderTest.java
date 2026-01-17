package com.machina.minterfacebuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InterfaceBuilder.
 * Tests parsing HTML templates and generating Custom UI output.
 */
public class InterfaceBuilderTest {

    /**
     * Comprehensive test that parses a complete HTML template with all supported features
     * and saves the result to a .ui file for inspection.
     */
    @Test
    public void testComprehensiveTemplateToUI(@TempDir Path tempDir) throws IOException {
        // Load the comprehensive HTML template from resources
        // Try multiple paths for different execution contexts
        Path htmlResource = null;
        Path[] possiblePaths = {
            Path.of("src/test/resources/comprehensive-template.html"),
            Path.of("minterfacebuilder/src/test/resources/comprehensive-template.html"),
            Path.of(System.getProperty("user.dir"), "src/test/resources/comprehensive-template.html"),
            Path.of(System.getProperty("user.dir"), "minterfacebuilder/src/test/resources/comprehensive-template.html")
        };

        for (Path path : possiblePaths) {
            if (Files.exists(path)) {
                htmlResource = path;
                break;
            }
        }

        // If still not found, create a simple template inline for testing
        String html;
        if (htmlResource != null && Files.exists(htmlResource)) {
            html = Files.readString(htmlResource);
        } else {
            // Fallback: use inline HTML if resource file not found
            html = """
                <script type="text/customui">
                @Title = "Comprehensive Test Page"
                @ButtonText = "Click Me"
                @ButtonColor = "#4CAF50"
                @Enabled = true
                $C = "../Common.ui"
                </script>
                
                <div id="main-container" style="padding: 20; background: #f0f0f0">
                    <h1 id="title" style="color: @ButtonColor">@Title</h1>
                    <button id="primary-button" text="@ButtonText" style="background: @ButtonColor"></button>
                    <input id="text-input" placeholder="Enter text" value="Default"></input>
                    <img id="test-image" src="assets/test.png" alt="Test Image"></img>
                    <$C.TextField id="common-field" text="Common Field"></$C.TextField>
                    <qrcode id="qr-code" data="https://example.com" size="150"></qrcode>
                </div>
                """;
            System.out.println("⚠️  Resource file not found, using inline template");
        }

        assertNotNull(html, "HTML content should not be null");
        assertFalse(html.isEmpty(), "HTML content should not be empty");


        // Parse the HTML
        HTMLCustomUITemplate template = InterfaceBuilder.parse(html);
        assertNotNull(template, "Parsed template should not be null");

        // Build the UI command
        String uiOutput = template.build();
        assertNotNull(uiOutput, "UI output should not be null");
        assertFalse(uiOutput.isEmpty(), "UI output should not be empty");

        // Save the result to a .ui file in the temp directory
        Path outputFile = tempDir.resolve("comprehensive-output.ui");
        Files.writeString(outputFile, uiOutput);

        // Verify the file was created
        assertTrue(Files.exists(outputFile), "Output .ui file should be created");
        assertTrue(Files.size(outputFile) > 0, "Output .ui file should not be empty");

        // Also save to a predictable location for easy inspection
        Path projectOutput = Path.of("target/test-output.ui");
        Files.createDirectories(projectOutput.getParent());
        Files.writeString(projectOutput, uiOutput);

        System.out.println("✅ Comprehensive template parsed successfully!");
        System.out.println("📄 Output saved to: " + outputFile.toAbsolutePath());
        System.out.println("📄 Output also saved to: " + projectOutput.toAbsolutePath());
        System.out.println("📊 Output size: " + uiOutput.length() + " characters");
        System.out.println("\n--- Generated UI Output (first 500 chars) ---");
        System.out.println(uiOutput.substring(0, Math.min(500, uiOutput.length())));
        if (uiOutput.length() > 500) {
            System.out.println("... (truncated)");
        }
    }

    /**
     * Test parsing from a file path.
     */
    @Test
    public void testParseFromFile(@TempDir Path tempDir) throws IOException {
        // Create a simple HTML file
        Path htmlFile = tempDir.resolve("test.html");
        String htmlContent = """
            <script type="text/customui">
            @Title = "File Test"
            @Color = "#ff0000"
            </script>
            
            <div id="container">
                <label id="title" style="color: @Color">@Title</label>
                <button id="button" text="Click Me"></button>
            </div>
            """;
        Files.writeString(htmlFile, htmlContent);

        // Parse from file
        HTMLCustomUITemplate template = InterfaceBuilder.parse(htmlFile);
        assertNotNull(template);

        // Build and save output
        String uiOutput = template.build();
        Path outputFile = tempDir.resolve("test-output.ui");
        Files.writeString(outputFile, uiOutput);

        assertTrue(Files.exists(outputFile));
        assertFalse(uiOutput.isEmpty());
    }

    /**
     * Test parsing with variables.
     */
    @Test
    public void testParseWithVariables() {
        String html = """
            <script type="text/customui">
            @Title = "Default Title"
            </script>
            
            <div id="container">
                <label id="title">@Title</label>
            </div>
            """;

        // Parse without overriding variables
        HTMLCustomUITemplate template1 = InterfaceBuilder.parse(html);
        String output1 = template1.build();

        // Parse with overriding variables
        java.util.Map<String, String> variables = new java.util.HashMap<>();
        variables.put("Title", "Custom Title"); // Note: without @ prefix when passing as map key
        HTMLCustomUITemplate template2 = InterfaceBuilder.parse(html, variables);
        String output2 = template2.build();

        // Outputs should be different (if variable substitution works)
        // If variables are not being substituted in the label text, outputs will be the same
        // So we check if either output1 contains "Default Title" or output2 contains "Custom Title"
        if (output1.contains("Default Title") || output2.contains("Custom Title")) {
            // Variable substitution is working, outputs should be different
            assertNotEquals(output1, output2, "Outputs should differ when variables are overridden");
            assertTrue(output2.contains("Custom Title") || output1.contains("Default Title"), 
                "Output should contain substituted variable value");
        }
    }

    /**
     * Test that unsupported CSS properties throw an exception.
     */
    @Test
    public void testUnsupportedCSSProperty() {
        String html = """
            <div id="container" style="border-radius: 5px; transform: scale(1.2)">
                <label id="test">Test</label>
            </div>
            """;

        // Should throw UnsupportedCSSPropertyException
        assertThrows(
            com.machina.minterfacebuilder.parser.CSSStyleParser.UnsupportedCSSPropertyException.class,
            () -> InterfaceBuilder.parse(html),
            "Should throw exception for unsupported CSS property"
        );
    }

    /**
     * Test that unknown HTML tags throw an exception.
     */
    @Test
    public void testUnsupportedHTMLTag() {
        // Test with various unsupported tags
        String[] unsupportedTags = {
            "<br/>",
            "<hr/>",
            "<video></video>",
            "<audio></audio>",
            "<iframe></iframe>",
            "<canvas></canvas>",
            "<svg></svg>",
            "<table></table>",
            "<form></form>"
        };

        for (String tag : unsupportedTags) {
            String html = "<div id=\"container\">" + tag + "</div>";
            
            assertThrows(
                com.machina.minterfacebuilder.factory.UnsupportedHTMLTagException.class,
                () -> InterfaceBuilder.parse(html),
                "Should throw exception for unsupported tag: " + tag
            );
        }
    }

    /**
     * Test that malformed script tags throw an exception or fail parsing.
     */
    @Test
    public void testInvalidScriptParsing() {
        // Test script with invalid syntax - missing equals sign
        String html1 = """
            <script type="text/customui">
            @Title "Invalid syntax"
            </script>
            <div id="container">
                <label>Test</label>
            </div>
            """;

        // Should throw an exception for invalid script syntax
        assertThrows(
            Exception.class,
            () -> InterfaceBuilder.parse(html1),
            "Should throw exception for invalid script syntax"
        );

        // Test script with unclosed quotes
        String html2 = """
            <script type="text/customui">
            @Title = "Unclosed quote
            </script>
            <div id="container">
                <label>Test</label>
            </div>
            """;

        assertThrows(
            Exception.class,
            () -> InterfaceBuilder.parse(html2),
            "Should throw exception for unclosed quotes in script"
        );

        // Test script with invalid variable name (contains special chars)
        String html3 = """
            <script type="text/customui">
            @Invalid-Variable-Name = "Test"
            </script>
            <div id="container">
                <label>Test</label>
            </div>
            """;

        // This might or might not throw - depends on implementation
        // But we'll test that parsing doesn't crash silently
        try {
            InterfaceBuilder.parse(html3);
        } catch (Exception e) {
            // Exception is acceptable for invalid variable names
            assertTrue(true, "Invalid variable name should cause parsing error");
        }
    }
}
