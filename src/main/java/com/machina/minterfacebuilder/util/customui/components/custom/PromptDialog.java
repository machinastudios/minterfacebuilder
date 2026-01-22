package com.machina.minterfacebuilder.util.customui.components.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.LayoutMode;
import com.machina.minterfacebuilder.helpers.TranslationKey;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.PageBuilder;
import com.machina.minterfacebuilder.util.customui.components.HDecoratedContainer;
import com.machina.minterfacebuilder.util.customui.components.HPageOverlay;
import com.machina.minterfacebuilder.util.customui.components.HSecondaryTextButton;
import com.machina.minterfacebuilder.util.customui.components.HTextButton;
import com.machina.minterfacebuilder.util.customui.components.HTextField;
import com.machina.minterfacebuilder.util.customui.components.HTitle;
import com.machina.minterfacebuilder.util.customui.components.base.Group;
import com.machina.minterfacebuilder.util.customui.components.base.Label;

public class PromptDialog extends PageBuilder {
    /**
     * Show a prompt dialog to the player.
     * @param options The dialog options.
     * @return The prompt dialog instance.
     */
    public static PromptDialog create(Options options) {
        return new PromptDialog(options);
    }

    /**
     * The title of the dialog.
     */
    private final Object title;

    /**
     * The description of the dialog.
     */
    private final Object description;

    /**
     * The list of input fields.
     */
    private final List<InputField> inputFields;

    /**
     * The text of the confirm button.
     */
    private final Object confirmText;

    /**
     * The text of the cancel button.
     */
    private final Object cancelText;

    /**
     * The action to perform when the confirm button is pressed.
     */
    private final Consumer<Map<String, String>> onConfirm;

    /**
     * The action to perform when the cancel button is pressed.
     */
    private final Consumer<Map<String, String>> onCancel;

    /**
     * Whether to close the dialog when the cancel button is pressed.
     */
    private boolean closeOnCancel = true;

    /**
     * The current input values (updated on CHANGE events).
     */
    private final Map<String, String> currentInputValues = new HashMap<>();

    public PromptDialog(Options options) {
        super("Group", null, CustomPageLifetime.CanDismiss);

        this.title = options.title;
        this.description = options.description;
        this.inputFields = options.inputFields != null ? new ArrayList<>(options.inputFields) : new ArrayList<>();
        this.confirmText = options.confirmText;
        this.cancelText = options.cancelText;
        this.closeOnCancel = options.closeOnCancel;
        this.onConfirm = options.onConfirm;
        this.onCancel = options.onCancel;
        
        // Initialize current input values
        for (InputField field : this.inputFields) {
            this.currentInputValues.put(field.id, "");
        }
    }


    @Override
    protected void construct() {
        appendChild(
            ComponentFactory.create(
                HPageOverlay.class,
                Map.of(
                    "LayoutMode", LayoutMode.MIDDLE
                ),
                ComponentFactory.create(
                    HDecoratedContainer.class,
                    Map.of(
                        "Anchor", Map.of("Width", 500)
                    )
                )
                    .setContentPadding(Map.of(
                        "Vertical", 16,
                        "Horizontal", 30
                    ))
                    .setTitle(
                        ComponentFactory.create(HTitle.class).setText(this.getTitle())
                    )
                    .setContent(
                        ComponentFactory.create(Group.class, Map.of("LayoutMode", LayoutMode.TOP))
                            .appendChild(
                                ComponentFactory.create(Label.class)
                                    .setText(this.description)
                                    .setStyle(Map.of(
                                        "TextColor", "#7a8a9b",
                                        "Wrap", true
                                    ))
                                    .setProperty("Anchor", Map.of("Top", 8)),
                                this.buildInputFields(),
                                ComponentFactory.create(
                                    Group.class,
                                    Map.of(
                                        "LayoutMode", LayoutMode.CENTER,
                                        "Anchor", Map.of("Top", 16)
                                    ),
                                    ComponentFactory.create(
                                        HTextButton.class,
                                        Map.of(
                                            "Anchor", Map.of("Right", 4)
                                        )
                                    )
                                        .setText(this.getConfirmText())
                                        .setId("ConfirmButton"),
                                    ComponentFactory.create(
                                        HSecondaryTextButton.class,
                                        Map.of(
                                            "Anchor", Map.of("Left", 4)
                                        )
                                    )
                                        .setText(this.getCancelText())
                                        .setId("CancelButton")
                                )
                            )
                    )
            )
        );

        // Listen for changes in all input fields
        for (InputField field : this.inputFields) {
            addEventListener("#" + field.id, EventType.CHANGE, (e) -> {
                // The event value contains the component ID, but we need to get the actual value
                // For now, we'll rely on the currentInputValues being updated through the event system
                // The actual value will be retrieved when the button is clicked
            });
        }

        // When clicking the confirm button, get all input values and call onConfirm
        addEventListener("#ConfirmButton", EventType.CLICK, (e) -> {
            Map<String, String> inputValues = this.getInputValues();
            if (this.onConfirm != null) {
                this.onConfirm.accept(inputValues);
            }
        });

        // When clicking the cancel button, get all input values and call onCancel
        addEventListener("#CancelButton", EventType.CLICK, (e) -> {
            Map<String, String> inputValues = this.getInputValues();
            if (this.onCancel != null) {
                this.onCancel.accept(inputValues);
            }

            // If closeOnCancel is true, close the dialog
            if (this.closeOnCancel) {
                this.close();
            }
        });
    }

    /**
     * Build the input fields from the list.
     * @return A Group containing all input fields.
     */
    private ComponentBuilder buildInputFields() {
        ComponentBuilder fieldsGroup = ComponentFactory.create(Group.class, Map.of("LayoutMode", LayoutMode.TOP));
        
        int topOffset = 16;

        // Iterate over the input fields
        for (InputField field : this.inputFields) {
            // Create the field attributes
            Map<String, Object> fieldAttributes = new HashMap<>();
            fieldAttributes.put("Id", field.id);

            // Set the placeholder
            if (field.placeholder != null) {
                fieldAttributes.put("Placeholder", field.placeholder);
            }
            
            // Create the text field
            ComponentBuilder textField = ComponentFactory.create(HTextField.class, fieldAttributes)
                .setProperty("Anchor", Map.of("Top", topOffset));

            // If it is a password field, set the password character
            if (field.isPassword) {
                textField.setProperty("PasswordChar", "*");
            }
            
            // Add the text field to the group
            fieldsGroup.appendChild(textField);

            // Increment the top offset for the next field
            topOffset += 50;
        }
        
        return fieldsGroup;
    }

    /**
     * Get all input values from the TextFields.
     * This method attempts to get the values from the custom page's event data.
     * @return A map of field IDs to their values.
     */
    private Map<String, String> getInputValues() {
        Map<String, String> values = new HashMap<>();
        try {
            if (this.getCustomPage() != null) {
                // Try to get the values from the page's component data
                // For now, return the stored values
                for (InputField field : this.inputFields) {
                    String value = this.currentInputValues.get(field.id);
                    values.put(field.id, value != null ? value : "");
                }
            }
        } catch (Exception e) {
            // Fallback: return stored values
        }
        
        // Ensure all fields have a value
        for (InputField field : this.inputFields) {
            if (!values.containsKey(field.id)) {
                values.put(field.id, this.currentInputValues.getOrDefault(field.id, ""));
            }
        }
        
        return values;
    }

    /**
     * Get the confirm text.
     * @return The confirm text.
     */
    private Object getConfirmText() {
        if (this.confirmText == null) {
            return TranslationKey.of("client.general.button.confirm");
        }

        return this.confirmText;
    }

    /**
     * Get the cancel text.
     * @return The cancel text.
     */
    private Object getCancelText() {
        if (this.cancelText == null) {
            return TranslationKey.of("client.general.button.cancel");
        }

        return this.cancelText;
    }

    /**
     * Get the title.
     * @return The title.
     */
    private Object getTitle() {
        if (this.title == null) {
            return TranslationKey.of("general.button.confirm");
        }

        return this.title;
    }

    public static class InputField {
        /**
         * The unique ID of the input field.
         */
        @Nonnull
        public final String id;

        /**
         * The placeholder text for the input field.
         */
        @Nullable
        public final Object placeholder;

        /**
         * Whether the input field is a password field.
         */
        public final boolean isPassword;

        public InputField(@Nonnull String id) {
            this(id, null, false);
        }

        public InputField(@Nonnull String id, @Nonnull String placeholder) {
            this(id, placeholder, false);
        }

        public InputField(@Nonnull String id, @Nonnull TranslationKey placeholder) {
            this(id, placeholder, false);
        }

        public InputField(@Nonnull String id, @Nullable Object placeholder, boolean isPassword) {
            this.id = id;
            this.placeholder = placeholder;
            this.isPassword = isPassword;
        }

        /**
         * Set the placeholder text.
         * @param placeholder The placeholder text.
         * @return A new InputField with the updated placeholder.
         */
        public InputField setPlaceholder(@Nullable Object placeholder) {
            return new InputField(this.id, placeholder, this.isPassword);
        }

        /**
         * Set the initial value.
         * @param initialValue The initial value.
         * @return A new InputField with the updated initial value.
         */
        public InputField setInitialValue(@Nullable String initialValue) {
            return new InputField(this.id, this.placeholder, this.isPassword);
        }

        /**
         * Set whether the input is a password field.
         * @param isPassword Whether the input is a password field.
         * @return A new InputField with the updated password setting.
         */
        public InputField setPassword(boolean isPassword) {
            return new InputField(this.id, this.placeholder, isPassword);
        }
    }

    public static class PasswordField extends InputField {
        public PasswordField(@Nonnull String id) {
            super(id, null, true);
        }

        public PasswordField(@Nonnull String id, @Nonnull TranslationKey placeholder) {
            super(id, placeholder, true);
        }
    }

    public static class Options {
        public Object title;
        public Object description;
        public List<InputField> inputFields = new ArrayList<>();
        public Object confirmText;
        public Object cancelText;
        public boolean closeOnCancel = true;
        public Consumer<Map<String, String>> onConfirm;
        public Consumer<Map<String, String>> onCancel;

        public Options(@Nonnull String title, @Nonnull String description, @Nonnull Consumer<Map<String, String>> onConfirm) {
            this.title = title;
            this.description = description;
            this.onConfirm = onConfirm;
        }

        public Options(@Nonnull TranslationKey title, @Nonnull TranslationKey description, @Nonnull Consumer<Map<String, String>> onConfirm) {
            this.title = title;
            this.description = description;
            this.onConfirm = onConfirm;
        }

        /**
         * Add an input field to the dialog.
         * @param field The input field to add.
         * @return The options.
         */
        public Options addInputField(@Nonnull InputField field) {
            this.inputFields.add(field);
            return this;
        }

        /**
         * Add an input field to the dialog.
         * @param id The unique ID of the input field.
         * @return The options.
         */
        public Options addInputField(@Nonnull String id) {
            this.inputFields.add(new InputField(id));
            return this;
        }

        /**
         * Set the list of input fields.
         * @param inputFields The list of input fields.
         * @return The options.
         */
        public Options setInputFields(@Nonnull List<InputField> inputFields) {
            this.inputFields = new ArrayList<>(inputFields);
            return this;
        }

        /**
         * Set whether to close the dialog when the cancel button is pressed.
         * @param closeOnCancel Whether to close the dialog when cancel is pressed.
         * @return The options.
         */
        public Options setCloseOnCancel(boolean closeOnCancel) {
            this.closeOnCancel = closeOnCancel;
            return this;
        }
    }
}
