package com.machina.example;

import javax.annotation.Nonnull;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.machina.minterfacebuilder.InterfaceBuilder;
import com.machina.minterfacebuilder.util.PluginAsset;

/**
 * Example page demonstrating how to use MInterfaceBuilder with InteractiveCustomUIPage
 * and handle UI events (button clicks, input changes, etc.).
 * <p>
 * This example shows:
 * <ul>
 *   <li>Loading HTML template from plugin assets using PluginAsset</li>
 *   <li>Compiling template once during initialization for better performance</li>
 *   <li>Binding UI events (button clicks, input changes)</li>
 *   <li>Handling event data in handleDataEvent</li>
 * </ul>
 */
public class ExamplePage extends InteractiveCustomUIPage<ExamplePage.ExampleEventData> {

    /**
     * Compiled template - parsed once during initialization to avoid overhead
     */
    private static final com.machina.minterfacebuilder.model.ParsedCustomUITemplate PAGE_TEMPLATE;

    static {
        try {
            // Parse template from plugin assets at class initialization
            // Note: In a real plugin, you would use: PluginAsset.of(pluginInstance, "path/to/template.html")
            // For this example, we use a relative path (in real usage, use PluginAsset.of)
            java.nio.file.Path templatePath = java.nio.file.Paths.get("example/src/main/resources/templates/example-page.html");
            PAGE_TEMPLATE = InterfaceBuilder.parse(templatePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load example page template", e);
        }
    }

    /**
     * Player input values
     */
    private String inputValue = null;

    public ExamplePage(@Nonnull com.hypixel.hytale.server.core.universe.PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanClose, ExampleEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, 
                     @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
        
        // Build the pre-compiled template and append to command builder
        // This is more efficient than parsing every time
        String uiString = PAGE_TEMPLATE.build();
        commandBuilder.append(uiString);

        // Bind input field to collect value changes
        // When the user types in the input, it will trigger an event with the new value
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#InputField",
            EventData.of(ExampleEventData.KEY_INPUT, "#InputField.Value"),
            false
        );

        // Bind button clicks
        // When the user clicks a button, it will trigger an event with the button ID
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SubmitButton",
            EventData.of(ExampleEventData.KEY_BUTTON, "SubmitButton"),
            true
        );

        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CancelButton",
            EventData.of(ExampleEventData.KEY_BUTTON, "CancelButton"),
            true
        );

        // Bind dropdown changes
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#DropdownField",
            EventData.of(ExampleEventData.KEY_DROPDOWN, "#DropdownField.Value"),
            false
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, 
                               @Nonnull ExampleEventData data) {
        super.handleDataEvent(ref, store, data);

        // Get player component
        Player playerComponent = store.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) {
            return;
        }

        // Handle input field changes
        if (data.inputValue != null) {
            this.inputValue = data.inputValue;
            // Input value was updated - you can validate, format, or react to changes here
            return;
        }

        // Handle dropdown changes
        if (data.dropdownValue != null) {
            // Dropdown selection was changed
            return;
        }

        // Handle button clicks
        if (data.buttonPressed == null) {
            return;
        }

        if (data.buttonPressed.equals("SubmitButton")) {
            // Handle submit button click
            if (this.inputValue == null || this.inputValue.isEmpty()) {
                // Show error message using UICommandBuilder
                UICommandBuilder commandBuilder = new UICommandBuilder();
                commandBuilder.set("#Error.Visible", true);
                commandBuilder.set("#Error.Text", "Please enter a value!");
                this.sendUpdate(commandBuilder);
                return;
            }

            // Process the form data
            // ... your logic here ...

            // Close the page on success
            this.close();
        } else if (data.buttonPressed.equals("CancelButton")) {
            // Handle cancel button click - just close the page
            this.close();
        }
    }

    /**
     * Event data class for ExamplePage.
     * This class holds the data sent from the UI when events are triggered.
     */
    public static class ExampleEventData {
        /**
         * Input field key
         */
        private static final String KEY_INPUT = "@Input";

        /**
         * Button key
         */
        private static final String KEY_BUTTON = "Button";

        /**
         * Dropdown key
         */
        private static final String KEY_DROPDOWN = "@Dropdown";

        /**
         * The CODEC for ExampleEventData
         */
        public static final BuilderCodec<ExampleEventData> CODEC = BuilderCodec.<ExampleEventData>builder(ExampleEventData.class, ExampleEventData::new)
            .addField(
                new KeyedCodec<>(KEY_INPUT, Codec.STRING),
                (entry, s) -> entry.inputValue = s,
                (entry) -> entry.inputValue
            )
            .addField(
                new KeyedCodec<>(KEY_BUTTON, Codec.STRING),
                (entry, s) -> entry.buttonPressed = s,
                (entry) -> entry.buttonPressed
            )
            .addField(
                new KeyedCodec<>(KEY_DROPDOWN, Codec.STRING),
                (entry, s) -> entry.dropdownValue = s,
                (entry) -> entry.dropdownValue
            )
            .build();

        /**
         * The input field value
         */
        public String inputValue;

        /**
         * The button that was pressed
         */
        public String buttonPressed;

        /**
         * The dropdown selected value
         */
        public String dropdownValue;
    }
}
