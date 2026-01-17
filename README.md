# MInterfaceBuilder

A powerful HTML to Custom UI parser and builder for Hytale. This library allows you to write UI components in HTML and automatically convert them to Hytale's Custom UI format.

📚 **[View API Documentation](https://machinastudios.github.io/minterfacebuilder/)**

## Features

- **HTML to Custom UI Conversion**: Write UI components in familiar HTML syntax
- **Variable Support**: Use template variables with `@VariableName = "value"` syntax
- **Custom Components**: Register your own custom HTML tags that map to ComponentBuilder instances
- **Self-Closing Tags**: Support for both `<tag />` and `<tag/>` formats
- **Common UI References**: Support for `$C` and `$Common` prefixes to reference Common.ui components
- **Style Mapping**: Automatic conversion of CSS styles to Custom UI properties
- **Caching**: Automatic caching of parsed templates by file path

## Installation

### Using GitHub Releases (Recommended)

MInterfaceBuilder automatically builds and releases JARs when code is pushed to the `master` branch. You can use these releases in your projects.

#### Maven

**Option 1: Using local folder with `systemPath` (Recommended for Hytale plugins)**

1. Download the JAR file from the [GitHub Releases](https://github.com/machinastudios/minterfacebuilder/releases) page.

2. Place it in your project's `lib` directory (create it if it doesn't exist):
   ```
   your-plugin/
     lib/
       minterfacebuilder-1.0.0-SNAPSHOT.jar
   ```

3. Add the dependency to your `pom.xml` using `systemPath`:

```xml
<dependency>
    <groupId>com.machina</groupId>
    <artifactId>minterfacebuilder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/minterfacebuilder-1.0.0-SNAPSHOT.jar</systemPath>
</dependency>
```

**Option 2: Install to local Maven repository**

1. Download the JAR file from the [GitHub Releases](https://github.com/machinastudios/minterfacebuilder/releases) page.

2. Install it to your local Maven repository:

```bash
mvn install:install-file \
  -Dfile=minterfacebuilder-1.0.0-SNAPSHOT.jar \
  -DgroupId=com.machina \
  -DartifactId=minterfacebuilder \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar
```

3. Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.machina</groupId>
    <artifactId>minterfacebuilder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Optional:** Install sources and javadoc JARs for better IDE support:

```bash
# Install sources JAR
mvn install:install-file \
  -Dfile=minterfacebuilder-1.0.0-SNAPSHOT-sources.jar \
  -DgroupId=com.machina \
  -DartifactId=minterfacebuilder \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -Dclassifier=sources

# Install javadoc JAR
mvn install:install-file \
  -Dfile=minterfacebuilder-1.0.0-SNAPSHOT-javadoc.jar \
  -DgroupId=com.machina \
  -DartifactId=minterfacebuilder \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -Dclassifier=javadoc
```

#### Gradle

1. Download the JAR file from the [GitHub Releases](https://github.com/machinastudios/minterfacebuilder/releases) page.

2. Place it in your project's `libs` directory (create it if it doesn't exist):
   ```
   your-project/
     libs/
       minterfacebuilder-1.0.0-SNAPSHOT.jar
   ```

3. Add the dependency to your `build.gradle`:

```gradle
dependencies {
    implementation files('libs/minterfacebuilder-1.0.0-SNAPSHOT.jar')
}
```

**Alternative:** Install to local Maven repository and use as Maven dependency:

```bash
# Install to local Maven repository (same as Maven example above)
mvn install:install-file \
  -Dfile=minterfacebuilder-1.0.0-SNAPSHOT.jar \
  -DgroupId=com.machina \
  -DartifactId=minterfacebuilder \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar
```

Then in `build.gradle`:

```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.machina:minterfacebuilder:1.0.0-SNAPSHOT'
}
```

### Building from Source

If you want to build from source:

```bash
git clone https://github.com/machinastudios/minterfacebuilder.git
cd minterfacebuilder
mvn clean install
```

Then add as a dependency:

```xml
<dependency>
    <groupId>com.machina</groupId>
    <artifactId>minterfacebuilder</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Quick Start

### Basic Usage

```java
import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;

// Parse HTML string
String html = """
    <div id="container">
        <button id="submit">Submit</button>
    </div>
    """;

HTMLCustomUITemplate template = InterfaceBuilder.parse(html);

// Get the UI string
String uiString = template.build();
```

**Performance Tip:** For better performance, it's recommended to parse (compile) templates once during initialization and store them as class properties, rather than parsing them every time they're needed. This avoids parsing overhead in performance-critical threads:

```java
public class MyPage extends InteractiveCustomUIPage<MyEventData> {
    // Parse template once during class initialization or in constructor
    private static final HTMLCustomUITemplate PAGE_TEMPLATE = InterfaceBuilder.parse(
        Paths.get("Common/UI/Custom/Pages/MyPage.html")
    );
    
    @Override
    public void build(...) {
        // Use pre-parsed template - no parsing overhead
        String uiString = PAGE_TEMPLATE.build();
        commandBuilder.append(uiString);
    }
}
```

### Using Variables

Variables must be declared inside `<script type="text/customui"></script>` tags:

```java
String html = """
    <script type="text/customui">
    @ButtonText = "Click Me"
    @ButtonColor = "#ff0000"
    </script>
    
    <div id="container">
        <button id="submit" style="color: @ButtonColor" text="@ButtonText"></button>
    </div>
    """;

HTMLCustomUITemplate template = InterfaceBuilder.parse(html);

// Modify variables after parsing
template.setVariable("ButtonText", "New Text");
template.setVariable("ButtonColor", "#00ff00");

// Get UI string with updated variables
String uiString = template.build();
```

### Internationalization (i18n) Support

Values starting with `%` are treated as literal i18n paths and are not processed for variable substitution. These values are passed directly to components without modification, allowing the i18n system to resolve them at runtime.

```java
String html = """
    <div id="container">
        <button id="submit" :text="%ui.button.submit"></button>
        <label :text="%messages.welcome"></label>
    </div>
    """;

HTMLCustomUITemplate template = InterfaceBuilder.parse(html);
```

In the example above, `%ui.button.submit` and `%messages.welcome` are i18n paths that will be resolved by your i18n system. They are not processed for variable substitution and are passed as-is to the component.

**Note:** i18n paths must start with `%` and are case-sensitive. They work in any HTML attribute value.

### Parsing from File

```java
import java.nio.file.Path;
import java.nio.file.Paths;

Path htmlFile = Paths.get("ui/my-page.html");
HTMLCustomUITemplate template = InterfaceBuilder.parse(htmlFile);

// Files are automatically cached
HTMLCustomUITemplate cached = InterfaceBuilder.parse(htmlFile); // Uses cache
```

### Providing Variables

```java
Map<String, String> variables = new HashMap<>();
variables.put("Title", "Welcome");
variables.put("Color", "#ffffff");

HTMLCustomUITemplate template = InterfaceBuilder.parse(html, variables);
```

## Supported HTML Tags

### Layout Tags
- `<div>`, `<section>`, `<article>`, `<header>`, `<footer>`, `<nav>`, `<main>` → `Group`
- `<ul>`, `<ol>` → `Group`
- `<li>` → `Label`

### Form Tags
- `<button>` → `Button`
- `<input>` or `<input type="text">` → `$C.@TextField`
- `<input type="number">` → `$C.@NumberField`
- `<input type="password">` → `$C.@TextField` (with `PasswordChar: "*"`)
- `<input type="color">` → `ColorPicker`
- `<input type="checkbox">` → `$C.@CheckBoxWithLabel` (supports `label` attribute)
- `<textarea>` → `$C.@MultilineTextField`
- `<select>` → `$C.@DropdownBox` (supports `<option>` children)

All input fields (`<input>`, `<textarea>`) support:
- `readonly` attribute → `ReadOnly` property
- `maxlength` attribute → `MaxLength` property

**Checkbox-specific attributes:**
- `label` attribute → Sets the label text for `CheckBoxWithLabel`
- `checked` attribute → Sets the checkbox value to `true`
- `value` attribute → Sets the checkbox value

**Select/Dropdown:**
- `<option>` children are parsed as dropdown options
- Options can have `value` attribute or text content as the option value

### Text Tags
- `<label>`, `<p>`, `<span>`, `<h1>` through `<h6>` → `Label`

### Media Tags
- `<img>` → `Image`

### Custom Tags
- `<qrcode>` / `<qr-code>` → `QRCodeComponent` (built-in)

### Unsupported Tags

Tags that are not explicitly supported will throw an `UnsupportedHTMLTagException` during parsing. Common unsupported tags include:

- `<br>` / `<br/>` - Line breaks are not supported by Hytale Custom UI
- `<hr>` / `<hr/>` - Horizontal rules are not supported by Hytale Custom UI
- `<meta>`, `<link>` - HTML metadata tags are not supported

If you need similar functionality, use `<div>` or `<Group>` components with appropriate styling instead.

## Attributes

### Common Attributes
- `id` - Sets the component ID (used with `#` selector)
- `class` - CSS class (can be used for style references)
- `style` - Inline CSS styles (see Style Support below)

### Tag-Specific Attributes

#### `<input>`
- `type` - Input type (`text`, `password`)
- `placeholder` - Placeholder text
- `value` - Default value
- `maxlength` - Maximum length

#### `<button>`
- `value` / `text` - Button text
- `id` - Button ID

#### `<img>`
- `src` - Image source
- `alt` - Alternative text (used as tooltip)
- `width` - Image width (via style)
- `height` - Image height (via style)

## Style Support

CSS styles are automatically converted to Custom UI properties:

### Positioning
- `style.top` → `Anchor.Top`
- `style.left` → `Anchor.Left`
- `style.right` → `Anchor.Right`
- `style.bottom` → `Anchor.Bottom`
- `style.width` → `Anchor.Width`
- `style.height` → `Anchor.Height`

### Colors
- `style.color` → `TextColor`
- `style.backgroundColor` / `style.background` → `Background`
  - Supports color values: `#ff0000`, `rgb(255,0,0)`, etc.
  - Supports `url()` for images: `background: url('image.png')` → `Background: image.png`

### Layout
- `style.padding` → `Padding` (simplified - single value)
- `style.text-align` → `LayoutMode` (`center`, `left`, `right`)
- `style.display: none` → `Visible: false`

### Typography
- `style.font-weight: bold` → `RenderBold: true`

## Common UI Components

### Using Aliases

Reference components from Common.ui using `$C` or `$Common` prefix:

```html
<$C.TextButton id="btn">Click Me</$C.TextButton>
<$Common.SecondaryTextButton id="cancel">Cancel</$Common.SecondaryTextButton>
```

This creates:
```
$C.@TextButton #btn {
    Text: Click Me;
}

$C.@SecondaryTextButton #cancel {
    Text: Cancel;
}
```

### Using Direct Component Names

You can also use Hytale component names directly without aliases. This is useful when you want to use components that are not from Common.ui or when you prefer explicit component names:

```html
<TextField id="username" placeholder="Enter username"></TextField>
<TextButton id="submit">Submit</TextButton>
<NumberField id="age"></NumberField>
<MultilineTextField id="description" maxlength="500"></MultilineTextField>
```

This creates:
```
TextField #username {
    PlaceholderText: "Enter username";
}

TextButton #submit {
    Text: Submit;
}

NumberField #age {
}

MultilineTextField #description {
    MaxLength: 500;
}
```

**Note:** When using direct component names, make sure they are valid Hytale Custom UI components. Invalid component names will throw an `UnsupportedHTMLTagException` during parsing.

### Supported Raw Hytale Components

You can use any of these Hytale Custom UI components directly by their name:

#### Container Components
- `Group` - Container element for organizing other elements

#### Text Components
- `Label` - Text display element

#### Button Components
- `Button` - Clickable button without text
- `TextButton` - Button with text label
- `BackButton` - Pre-styled back navigation button

#### Input Components
- `TextField` - Single-line text input field
- `NumberField` - Numeric input field
- `MultilineTextField` - Multi-line text input field
- `CheckBox` - Toggle checkbox
- `CheckBoxWithLabel` - Checkbox with adjacent label
- `DropdownBox` - Dropdown selection box

#### Media Components
- `Image` - Static image
- `Sprite` - Animated or static image with frame support

#### Other Components
- `ColorPicker` - Color selection component

**Example:**
```html
<Group id="container">
    <Label id="title">Welcome</Label>
    <TextField id="username" placeholder="Enter username"></TextField>
    <NumberField id="age"></NumberField>
    <MultilineTextField id="description" maxlength="500"></MultilineTextField>
    <CheckBox id="agree" value="true"></CheckBox>
    <DropdownBox id="country"></DropdownBox>
    <TextButton id="submit">Submit</TextButton>
</Group>
```

## Custom Components

Register your own custom HTML tags:

### Using a Class

```java
public class MyCustomComponent extends ComponentBuilder {
    public MyCustomComponent(Map<String, String> attributes) {
        super("Group");
        String value = attributes.get("value");
        this.setProperty("Text", value);
    }
}

// Register it
InterfaceBuilder.registerCustomTag("mycomponent", MyCustomComponent.class);
```

Then use it in HTML:
```html
<mycomponent value="Hello"></mycomponent>
```

### Using a Factory Function

```java
InterfaceBuilder.registerCustomTag("mycomponent", attributes -> {
    ComponentBuilder cb = ComponentBuilder.create("Group");
    String value = attributes.get("value");
    cb.setProperty("Text", value);
    return cb;
});
```

## Built-in Custom Components

### QR Code Component

The QR Code component is registered by default. Use it like this:

```html
<qrcode id="myQR" data="https://example.com" blocksize="8"></qrcode>
```

Or using the hyphenated version:
```html
<qr-code data="https://example.com"></qr-code>
```

**Attributes:**
- `data` or `value` - The data to encode in the QR code (required)
- `blocksize` or `block-size` - Size of each QR code block (default: 6)
- `id` - Component ID

**Note:** If `QRCodeUtil` from the `mauth` plugin is available, it will be used automatically. Otherwise, a placeholder component is created.

## Self-Closing Tags

Both formats are supported:
```html
<input type="text" />
<img src="image.png"/>
<qrcode data="test" />
```

## HTMLCustomUITemplate

The `HTMLCustomUITemplate` class extends `ComponentBuilder`, so you can use it directly:

```java
HTMLCustomUITemplate template = InterfaceBuilder.parse(html);

// Use as ComponentBuilder
template.setProperty("SomeProperty", "value");
template.appendChild(anotherComponent);

// Access variables
template.setVariable("VarName", "value");
String varValue = template.getVariable("VarName");

// Build UI string
String uiString = template.build();

// Get UICommandBuilder (for dynamic updates)
UICommandBuilder commandBuilder = template.buildUICommandBuilder();
```

## Variable Substitution

Variables are substituted during parsing. You can:

1. Define variables in HTML inside `<script type="text/customui">` tags:
```html
<script type="text/customui">
@Title = "Welcome"
@Color = "#ff0000"
</script>

<div style="color: @Color">
    <label>@Title</label>
</div>
```

**Note:** Variables and properties must be declared inside `<script type="text/customui"></script>` tags.
The content of this script tag is applied to the root component (for properties/aliases) and
variables are extracted for substitution in the template.

2. Provide variables when parsing:
```java
Map<String, String> vars = new HashMap<>();
vars.put("Title", "Hello");
vars.put("Color", "#00ff00");
HTMLCustomUITemplate template = InterfaceBuilder.parse(html, vars);
```

3. Modify variables after parsing:
```java
template.setVariable("Title", "New Title");
```

Variables can be used in any attribute value:
```html
<button style="color: @ButtonColor" text="@ButtonText"></button>
<input placeholder="@PlaceholderText" value="@DefaultValue"></input>
```

**Internationalization (i18n) Paths:**

Values starting with `%` are treated as literal i18n paths and are not processed for variable substitution. This allows you to use i18n paths directly in your HTML templates:

```html
<button :text="%ui.button.submit" />
<label :text="%messages.welcome" />
<div :tooltip="%tooltips.help" />
```

i18n paths are passed directly to components without modification, allowing your i18n system to resolve them at runtime. They work in any HTML attribute value and are case-sensitive.

**Note:** i18n paths must start with `%` and will not be processed for variable substitution, even if they contain `@VariableName` patterns.

**Script Tag for Variables and Properties:**

Variables and properties/aliases must be declared inside `<script type="text/customui"></script>` tags:

```html
<script type="text/customui">
@Title = "Welcome"              <!-- Variable -->
@Color = "#ff0000"              <!-- Variable -->
PropertyName = "value"          <!-- Property applied to root component -->
AliasName = "aliasValue"        <!-- Alias applied to root component -->
Padding = 20                    <!-- Property value (treated as string) -->
</script>

<div style="color: @Color">
    <label>@Title</label>
</div>
```

- **Variables** (starting with `@`) are extracted for substitution in the template
- **Properties/Aliases** (not starting with `@`) are applied directly to the root component
- Properties support string, number, and boolean types automatically
**Dynamic Binding with `:` Prefix in HTML Attributes:**

The `:` prefix in HTML attributes (similar to Vue's `:` binding) allows you to use CommonUI code bindings:

- **Variable binding**: `<span :text="@Title" />` binds `text` to the variable `@Title` (substitutes variable value)
- **Objects**: `<div :padding="(Left: 7)" />` binds `padding` to a CommonUI object
- **Mixed**: `<div :padding="(Horizontal: 12, Left: @LeftValue)" />` uses variables inside objects

Without `:`, attributes are treated as string literals (variables are still substituted):

- **String literal**: `<span text="@Title" />` sets `text` to the literal string `"@Title"` (after variable substitution)
- **Regular attribute**: `<span text="Hello" />` sets `text` to `"Hello"`

This is useful when you need to pass CommonUI objects, bind to variables dynamically, or use variable values inside objects.

**Conditional Visibility with `m-show` and `m-if`:**

Both `m-show` and `m-if` are equivalent and control component visibility by setting the `Visible` property:

- **Boolean**: `<div m-show="true" />` → `Visible: true`, `<div m-show="false" />` → `Visible: false`
- **Variable**: `<div m-if="@IsVisible" />` → `Visible: @IsVisible` (substitutes variable value)
- **Number**: `<div m-show="1" />` → `Visible: true` (non-zero = visible), `<div m-show="0" />` → `Visible: false`

Examples:
```html
<div m-show="true">Always visible</div>
<div m-if="@Enabled">Only visible if @Enabled is true</div>
<div m-show="false">Hidden</div>
<span m-if="1">Visible (non-zero = true)</span>
```

## Caching

Templates parsed from files are automatically cached:

```java
Path file = Paths.get("ui/page.html");

// First parse - reads from file
HTMLCustomUITemplate template1 = InterfaceBuilder.parse(file);

// Second parse - uses cache
HTMLCustomUITemplate template2 = InterfaceBuilder.parse(file);
```

Clear the cache if needed:
```java
InterfaceBuilder.clearCache(); // Clear all
InterfaceBuilder.removeFromCache(file); // Remove specific file
```

**Note:** Cache is not used when custom variables are provided, as variables affect the output.

## Examples

### Complete Example

```java
import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// HTML with variables
String html = """
    @Title = "Login"
    @ButtonText = "Sign In"
    
    <div id="loginContainer">
        <label style="font-weight: bold">@Title</label>
        
        <input type="password" id="passwordField" placeholder="Password" />
        
        <button id="submitButton" style="color: @ButtonColor">@ButtonText</button>
    </div>
    """;

// Provide additional variables
Map<String, String> vars = new HashMap<>();
vars.put("ButtonColor", "#4a90e2");

// Parse
HTMLCustomUITemplate template = InterfaceBuilder.parse(html, vars);

// Modify after parsing
template.setVariable("Title", "Sign In");
template.setVariable("ButtonText", "Login Now");

// Get UI string
String uiString = template.build();
```

### Using in InteractiveCustomUIPage with Event Handling

For better performance, compile templates once during initialization:

```java
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.util.PluginAsset;
import java.nio.file.Path;

public class MyPage extends InteractiveCustomUIPage<MyPage.MyEventData> {
    // Parse template once during class initialization (recommended for performance)
    private static final HTMLCustomUITemplate PAGE_TEMPLATE = InterfaceBuilder.parse(
        PluginAsset.of(pluginInstance, "Common/UI/Custom/Pages/MyPage.html")
    );
    
    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder commandBuilder, 
                     UIEventBuilder eventBuilder, Store<EntityStore> store) {
        // Build pre-compiled template (no parsing overhead)
        String uiString = PAGE_TEMPLATE.build();
        commandBuilder.append(uiString);
        
        // Bind input field to collect value changes
        // When user types, triggers event with new value
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#InputField",
            EventData.of(MyEventData.KEY_INPUT, "#InputField.Value"),
            false
        );
        
        // Bind button clicks
        // When user clicks, triggers event with button ID
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SubmitButton",
            EventData.of(MyEventData.KEY_BUTTON, "SubmitButton"),
            true
        );
    }
    
    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, 
                               MyEventData data) {
        // Handle input changes
        if (data.inputValue != null) {
            // Input value changed - process it
            return;
        }
        
        // Handle button clicks
        if (data.buttonPressed != null) {
            if (data.buttonPressed.equals("SubmitButton")) {
                // Handle submit button click
            }
        }
    }
    
    // Event data class (see example below for full implementation)
    public static class MyEventData {
        // ... CODEC and fields ...
    }
}
```

See the [example plugin](example/src/main/java/com/machina/example/ExamplePage.java) for a complete working example with event handling.

## API Reference

For complete API documentation, see the [Javadoc](https://machinastudios.github.io/minterfacebuilder/).

### InterfaceBuilder

#### Static Methods

- `parse(String html)` - Parse HTML string
- `parse(String html, Map<String, String> variables)` - Parse HTML with variables
- `parse(Path path)` - Parse HTML file
- `parse(Path path, Map<String, String> variables)` - Parse HTML file with variables
- `registerCustomTag(String tagName, Function<Map<String, String>, ComponentBuilder> factory)` - Register custom tag with factory
- `registerCustomTag(String tagName, Class<? extends ComponentBuilder> componentClass)` - Register custom tag with class
- `clearCache()` - Clear all cached templates
- `removeFromCache(Path path)` - Remove specific file from cache

### HTMLCustomUITemplate

Extends `ComponentBuilder` - all ComponentBuilder methods are available.

#### Variable Methods

- `setVariable(String name, String value)` - Set a variable (returns `this` for chaining)
- `getVariable(String name)` - Get a variable value
- `setVariables(Map<String, String> variables)` - Set multiple variables
- `getVariables()` - Get all variables (returns copy)

#### Build Methods

- `build()` - Build UI string (from ComponentBuilder)
- `build(ComponentBuilderSettings settings)` - Build UI string with settings
- `buildUICommandBuilder()` - Build UICommandBuilder for dynamic updates
- `buildEventDataCodec()` - Build a `BuilderCodec<DynamicEventData>` based on template variables

#### File Persistence Methods

- `loadFromFile(String filename, Path configPath, Path defaultAssetPath)` - Load template from file (static method)
  - If file exists in `configPath`, uses that file
  - If not, copies from `defaultAssetPath` to `configPath` and uses the copied file
  - Throws `FileNotFoundException` if file not found in either location
  - Throws `IOException` if copying fails
- `saveToFile(String filename, Path path)` - Save template to file
- `saveToFile(String filename)` - Save template to default path (if set)

### HTMLCustomUIPage

A convenience class that extends `InteractiveCustomUIPage` and automatically converts HTML content to Custom UI format. This class simplifies the creation of Custom UI pages by handling HTML parsing and template management automatically.

**Important:** `HTMLCustomUIPage` requires a `PlayerRef` in all constructors. Use `PlayerUtil.openPage()` or similar methods to open the page on the world thread.

#### Constructors

- `HTMLCustomUIPage(PlayerRef playerRef)` - Create with default lifetime (`CanDismiss`) and dynamic codec
- `HTMLCustomUIPage(PlayerRef playerRef, BuilderCodec<T> eventDataCodec)` - Create with custom codec
- `HTMLCustomUIPage(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec)` - Full constructor
- `HTMLCustomUIPage(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, Map<String, String> overrideVariables)` - Create with variable overrides
- `HTMLCustomUIPage(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, Map<String, String> overrideVariables)` - Full constructor with variables

#### Static Factory Methods

- `build(PlayerRef playerRef)` - Build instance from calling subclass (uses reflection to find and instantiate)
- `fromHTML(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, String html)` - Create from HTML string
- `fromHTML(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, String html)` - Create from HTML string with custom lifetime
- `fromHTML(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, String html, Map<String, String> variables)` - Create from HTML string with variables
- `fromHTML(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, String html, Map<String, String> variables)` - Full fromHTML variant
- `fromFile(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, Path htmlPath)` - Create from HTML file
- `fromFile(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, Path htmlPath)` - Create from HTML file with custom lifetime
- `fromFile(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, Path htmlPath, Map<String, String> variables)` - Create from HTML file with variables
- `fromFile(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, Path htmlPath, Map<String, String> variables)` - Full fromFile variant
- `fromAsset(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, String assetPath)` - Create from plugin asset (requires AssetPack)
- `fromAsset(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, String assetPath)` - Create from plugin asset with custom lifetime
- `fromAsset(PlayerRef playerRef, BuilderCodec<T> eventDataCodec, String assetPath, Map<String, String> variables)` - Create from plugin asset with variables
- `fromAsset(PlayerRef playerRef, CustomPageLifetime lifetime, BuilderCodec<T> eventDataCodec, String assetPath, Map<String, String> variables)` - Full fromAsset variant

#### Instance Methods

- `withOutputPath(Path outputPath, String filename)` - Set output path for customizable template file (where users can edit)
- `withInputPath(Path inputPath)` - Set input path for default template file directory (from mod assets)
- `buildInstance()` - Build the Custom UI string (instance method, uses cached template or loads from paths)
- `getParsedTemplate()` - Get the parsed `HTMLCustomUITemplate` (lazy-loaded if using paths)

#### Usage Examples

**Simple usage with build() method:**

```java
public class MyPage extends HTMLCustomUIPage<HTMLCustomUITemplate.DynamicEventData> {
    // Constructor required by build()
    protected MyPage(PlayerRef playerRef) {
        super(playerRef); // Codec will be built dynamically after paths are set
        
        // Configure paths (optional)
        withOutputPath(plugin.getConfigDirectory(), "pages/my-page.html");
        Path inputFile = plugin.getModAssetPath("Common/UI/Custom/Pages/MyPage.html");
        if (inputFile != null) {
            withInputPath(inputFile.getParent()); // Input path is a directory
        }
    }
    
    // Build and open page
    public static void show(PlayerRef playerRef, Ref<EntityStore> ref, Store<EntityStore> store) {
        MyPage page = MyPage.build(playerRef);
        // Page is ready to use - it will be opened via InteractiveCustomUIPage.build()
        // when the page is actually displayed to the player
    }
}
```

**Using with PlayerUtil.openPage (recommended for thread safety):**

```java
PlayerUtil.openPage(
    player,
    (p, ref, store) -> CustomHTMLPage.build(p.getPlayerRef())
)
.thenAccept(result -> {
    if (!result.success) {
        // Handle error
    }
});
```

**Using with HTML file paths:**

```java
// Simple file loading (requires PlayerRef and BuilderCodec)
BuilderCodec<MyEventData> codec = ...; // Your codec
HTMLCustomUIPage page = HTMLCustomUIPage.fromFile(
    playerRef,
    codec,
    Path.of("pages/login.html")
);

// With custom lifetime and variables
Map<String, String> vars = new HashMap<>();
vars.put("Title", "Login");
HTMLCustomUIPage page = HTMLCustomUIPage.fromFile(
    playerRef,
    CustomPageLifetime.CantClose,
    codec,
    Path.of("pages/login.html"),
    vars
);
```

**Using with plugin assets:**

```java
// Load from plugin's AssetPack
HTMLCustomUIPage page = HTMLCustomUIPage.fromAsset(
    playerRef,
    codec,
    pluginInstance,
    "Common/UI/Custom/Pages/Settings.html"
);

// With variables
Map<String, String> vars = new HashMap<>();
vars.put("PlayerName", player.getName());
HTMLCustomUIPage page = HTMLCustomUIPage.fromAsset(
    playerRef,
    CustomPageLifetime.CanDismiss,
    codec,
    pluginInstance,
    "Common/UI/Custom/Pages/Profile.html",
    vars
);
```

**Using with customizable template files:**

This pattern allows users to customize HTML templates while keeping defaults in mod assets:

```java
public class SettingsPage extends HTMLCustomUIPage<HTMLCustomUITemplate.DynamicEventData> {
    protected SettingsPage(PlayerRef playerRef) {
        super(playerRef); // Dynamic codec will be built after paths are set
        
        // Output: User can edit this file (in config folder)
        withOutputPath(plugin.getConfigDirectory(), "pages/settings.html");
        
        // Input: Default template directory (in mod assets)
        Path inputFile = plugin.getModAssetPath("Common/UI/Custom/Pages/Settings.html");
        if (inputFile != null) {
            withInputPath(inputFile.getParent()); // Pass directory, not file
        }
        
        // On first load, if outputPath doesn't exist, it copies from inputPath
        // After that, only outputPath is used (users can customize it)
    }
    
    public static SettingsPage build(PlayerRef playerRef) {
        return HTMLCustomUIPage.build(playerRef);
    }
}
```

#### Key Features

1. **PlayerRef Required**: All constructors require a `PlayerRef` parameter for proper initialization.

2. **Automatic Dynamic Codec**: When using `build()` with `HTMLCustomUITemplate.DynamicEventData`, the codec is automatically generated from template variables.

3. **Template Caching**: Templates are cached after first parse to improve performance.

4. **Path-Based Templates**: Supports both input (default) and output (customizable) paths for template files.
   - `inputPath`: Directory path containing the default template file (from mod assets)
   - `outputPath`: Full path (directory + filename) where the customizable template is stored (in config folder)
   - On first load, if `outputPath` doesn't exist, it's copied from `inputPath`
   - After that, only `outputPath` is used (users can customize it)

5. **Variable Overrides**: Can provide variables during construction that override template variables.

6. **Default Lifetime**: Default lifetime is `CustomPageLifetime.CanDismiss` if not specified.

7. **Thread Safety**: Use `PlayerUtil.openPage()` to ensure page opening happens on the world thread.

## Limitations

- CSS support is limited to commonly used properties
- Nested style objects are not fully supported
- Some HTML features may not have direct Custom UI equivalents
- Percentage-based sizes are approximated

## Roadmap

The following features are planned for future releases:

### Event Binding with Lambda

Add support for binding events directly in Java code using lambda expressions:

```java
PAGE_TEMPLATE.on(Event.Activating, "#Selector", () -> {
    // Do something when the component is activated
    getLogger().info("Button clicked!");
});
```

This will provide a type-safe way to handle UI events without manually using `UICommandBuilder`.

### Dynamic Interface Updates

Add methods to update interface components after rendering:

```java
// Update a component by ID - supports both HTML and Hytale properties
PAGE_TEMPLATE.getElementById("Selector").update("text", "New Text"); // HTML property
PAGE_TEMPLATE.getElementById("Selector").update("Text", "New Text"); // Hytale property (same result)
PAGE_TEMPLATE.getElementById("Selector").update("visible", false); // HTML property
PAGE_TEMPLATE.getElementById("Selector").update("Visible", false); // Hytale property (same result)

// Update using CSS selector (querySelector)
PAGE_TEMPLATE.querySelector("#Group #Selector").update("visible", false);
PAGE_TEMPLATE.querySelector(".myClass").update("color", "#FF0000"); // HTML property → maps to TextColor

// Update style properties using HTML names
PAGE_TEMPLATE.querySelector(".myClass").update("font-size", 16); // HTML property → maps to FontSize in Style
PAGE_TEMPLATE.querySelector(".myClass").update("text-align", "center"); // HTML property → maps to HorizontalAlignment
```

These methods will be simple wrappers around `UICommandBuilder.set(selector, value)`, making it easier to update UI components dynamically. HTML properties (like `color`, `font-size`, `text-align`) will be automatically mapped to their corresponding Hytale Custom UI properties (like `TextColor`, `Style.FontSize`, `Style.HorizontalAlignment`) using the same mapping logic used during HTML parsing.

#### Class Selectors Support

Add support for virtual class selectors to select multiple elements at once:

```html
<button id="btn1" class="primary-button">Button 1</button>
<button id="btn2" class="primary-button">Button 2</button>
<button id="btn3" class="secondary-button">Button 3</button>
```

```java
// Update all elements with the same class at once - supports both HTML and Hytale properties
PAGE_TEMPLATE.querySelectorAll(".primary-button").update("visible", false); // HTML property
PAGE_TEMPLATE.querySelectorAll(".primary-button").update("Visible", false); // Hytale property (same result)
PAGE_TEMPLATE.querySelectorAll(".secondary-button").update("color", "#FF0000"); // HTML property → maps to TextColor
PAGE_TEMPLATE.querySelectorAll(".secondary-button").update("font-size", 18); // HTML property → maps to Style.FontSize

// Class selectors will be mapped internally to multiple element selectors
// This provides a convenient way to update groups of components simultaneously
// HTML properties are automatically mapped to Hytale properties using the same mapping logic
```

Class selectors will be a virtual feature that maps internally to individual element IDs, allowing you to select and update multiple components at once without manually iterating through each one.

### Automatic Input Value Reading

Automatically read values from input fields into variables using the `name` attribute:

```html
<input type="text" id="username" name="username" />
<input type="password" id="password" name="password" />
<input type="number" id="age" name="user.age" />
```

When the user types in these fields, the values will automatically be stored in an internal `HashMap<String, Object>` using the `name` attribute value as the key. You can access these values programmatically:

```java
// Get all input values
Map<String, Object> formData = PAGE_TEMPLATE.getInputValues();

// Get specific value by name
String username = (String) PAGE_TEMPLATE.getInputValue("username");
String password = (String) PAGE_TEMPLATE.getInputValue("password");
Integer age = (Integer) PAGE_TEMPLATE.getInputValue("user.age");
```

The `name` attribute supports dot notation (e.g., `name="user.age"`) for nested structures, which will be stored in the HashMap accordingly.

### Server-Side m-if

Add support for server-side conditional rendering using `m-if`:

```html
<div m-if="@IsAdmin">Admin Panel</div>
<div m-if="@IsLoggedIn">User Dashboard</div>
```

The `m-if` attribute will evaluate server-side conditions and only render components when the condition is true, reducing the amount of UI data sent to clients.

## Contributing

When adding new features:

1. Update this README with examples
2. Add Javadoc comments in English
3. Follow existing code style
4. Test with various HTML inputs

## Community

💬 **Join our Discord community!**

Get help, share your ideas, and connect with other developers:
- 🆘 **Support**: Get help with setup and troubleshooting
- 💡 **Suggestions**: Share your ideas and feedback
- 🤝 **Community**: Connect with other Hytale developers

**👉 [Join Discord Server](https://discord.gg/QAFrzj48EN)**

## License

Part of the Machina plugin ecosystem for Hytale.
