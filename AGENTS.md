# MInterfaceBuilder - Agent Guidelines

## Documentation Reference

**ALWAYS use the GitHub Pages documentation as the primary reference for API details:**

📚 **[API Documentation](https://machinastudios.github.io/minterfacebuilder/)**

The GitHub Pages site contains the complete, up-to-date Javadoc for all classes, methods, and APIs in MInterfaceBuilder. When answering questions about:

- Class methods and their signatures
- Method parameters and return types
- API usage examples
- Available options and configurations
- Plugin asset loading (`PluginAsset` utility)

**Always refer to or link to the GitHub Pages documentation** instead of only using code search or assumptions.

## Key Points

### InterfaceBuilder

The main entry point for parsing HTML to Custom UI. See the [InterfaceBuilder Javadoc](https://machinastudios.github.io/minterfacebuilder/com/machina/minterfacebuilder/InterfaceBuilder.html) for:

- `parse(String html)` - Parse HTML strings
- `parse(Path path)` - Parse HTML files
- `parseAsset()` - Parse assets from asset packs
- `registerCustomTag()` - Register custom component tags

### PluginAsset

Utility class for loading assets from plugins/mods. See the [PluginAsset Javadoc](https://machinastudios.github.io/minterfacebuilder/com/machina/minterfacebuilder/util/PluginAsset.html) for:

- `PluginAsset.of(plugin, "path/to/asset.html")` - Resolve plugin asset paths

### ParsedCustomUITemplate

Result of parsing HTML. Extends `ComponentBuilder`. See the [ParsedCustomUITemplate Javadoc](https://machinastudios.github.io/minterfacebuilder/com/machina/minterfacebuilder/model/ParsedCustomUITemplate.html) for:

- Variable management methods
- Component building methods

## When Helping Users

1. **Check the GitHub Pages docs first** before answering API questions
2. **Link to specific pages** in the documentation when relevant
3. **Verify method signatures** against the Javadoc before suggesting code
4. **Use examples from the README** and documentation when available

## Documentation URLs

- **Main Documentation**: https://machinastudios.github.io/minterfacebuilder/
- **Repository**: https://github.com/machinastudios/minterfacebuilder
- **README**: See the repository README.md for usage examples and quick start guide
