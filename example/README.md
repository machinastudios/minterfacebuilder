# MInterfaceBuilder Example

This example demonstrates how to use MInterfaceBuilder to parse HTML templates and generate Custom UI components.

## Structure

```
example/
├── README.md
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/machina/example/
    │   │       └── ExampleUsage.java
    │   └── resources/
    │       └── templates/
    │           └── example-page.html
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- HytaleServer.jar (located in `../lib/` or `../../mshared/lib/`)

## Running the Example

### Option 1: Run from Maven

```bash
cd example
mvn compile exec:java -Dexec.mainClass="com.machina.example.ExampleUsage"
```

### Option 2: Run from IDE

1. Import the `example` directory as a Maven project in your IDE
2. Ensure the `minterfacebuilder` dependency is available in your local Maven repository
3. Run `ExampleUsage.main()` method

## What the Example Shows

### Example 1: Parse HTML from String
Demonstrates the simplest usage - parsing an HTML string directly.

### Example 2: Parse HTML from File
Shows how to parse templates from files and enable automatic file watching for hot-reloading during development.

### Example 3: Using Template Variables
Demonstrates using template variables declared in the HTML template itself.

### Example 4: Modifying Variables After Parsing
Shows how to modify variables and reparse templates with new variable values.

## Template Example

The `example-page.html` template demonstrates:

- **Variables**: `@Title`, `@ButtonText`, `@ButtonColor`, `@Subtitle`
- **CSS Styles**: Color, padding, font-weight, text-align, width, height
- **i18n Paths**: `%ui.messages.welcome` (values starting with `%` are treated as i18n paths)
- **HTML Tags**: `div`, `label`, `button`, `input`

## Customizing the Example

You can modify `example-page.html` to test different HTML structures and CSS styles. The file watcher will automatically reload changes when enabled.

## Troubleshooting

### HytaleServer.jar not found

If you get an error about HytaleServer.jar not being found, ensure the file exists in one of these locations:
- `../lib/HytaleServer.jar` (relative to example directory)
- `../../mshared/lib/HytaleServer.jar` (relative to example directory)

You can also modify the `pom.xml` to point to the correct location.

### Dependencies not found

Make sure `minterfacebuilder` is installed in your local Maven repository:

```bash
cd ..
mvn clean install
```

Then rebuild the example:

```bash
cd example
mvn clean compile
```
