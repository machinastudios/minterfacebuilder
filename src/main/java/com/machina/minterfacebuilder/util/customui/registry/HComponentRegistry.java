package com.machina.minterfacebuilder.util.customui.registry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.components.*;

/**
 * Registry mapping H component names to their respective classes.
 * This registry is used to automatically register all H components with ComponentFactory.
 */
public class HComponentRegistry {
    /**
     * Map of component tag names (lowercase) to their respective ComponentBuilder classes.
     * All components are registered with lowercase names for case-insensitive matching.
     */
    private static final Map<String, Class<? extends ComponentBuilder>> COMPONENT_MAP = new HashMap<>();

    /**
     * Static initializer to populate the component registry.
     */
    static {
        // Buttons (13 components)
        registerComponent(HTextButton.class);
        registerComponent(HButton.class);
        registerComponent(HCancelTextButton.class);
        registerComponent(HCancelButton.class);
        registerComponent(HSecondaryTextButton.class);
        registerComponent(HSecondaryButton.class);
        registerComponent(HTertiaryTextButton.class);
        registerComponent(HTertiaryButton.class);
        registerComponent(HSmallSecondaryTextButton.class);
        registerComponent(HSmallTertiaryTextButton.class);
        registerComponent(HCloseButton.class);
        registerComponent(HHeaderTextButton.class);
        registerComponent(HBackButton.class);

        // Inputs (4 components)
        registerComponent(HTextField.class);
        registerComponent(HNumberField.class);
        registerComponent(HDropdownBox.class);
        registerComponent(HHeaderSearch.class);

        // Checkboxes (2 components)
        registerComponent(HCheckBox.class);
        registerComponent(HCheckBoxWithLabel.class);

        // Containers (4 components)
        registerComponent(HPanel.class);
        registerComponent(HContainer.class);
        registerComponent(HDecoratedContainer.class);
        registerComponent(HPageOverlay.class);

        // Labels (4 components)
        registerComponent(HTitleLabel.class);
        registerComponent(HTitle.class);
        registerComponent(HSubtitle.class);
        registerComponent(HPanelTitle.class);

        // Others (7 components)
        registerComponent(HColorPicker.class);
        registerComponent(HScrollbar.class);
        registerComponent(HSpinner.class);
        registerComponent(HSlider.class);
        registerComponent(HContentSeparator.class);
        registerComponent(HVerticalSeparator.class);
        registerComponent(HPanelSeparatorFancy.class);

        // Special (3 components)
        registerComponent(HActionButtonContainer.class);
        registerComponent(HActionButtonSeparator.class);
        registerComponent(HHeaderSeparator.class);
    }

    /**
     * Get the component class for a given tag name.
     * @param tagName The tag name (case-insensitive).
     * @return The component class, or null if not found.
     */
    public static Class<? extends ComponentBuilder> getComponentClass(String tagName) {
        if (tagName == null) {
            return null;
        }

        return COMPONENT_MAP.get(tagName.toLowerCase());
    }

    /**
     * Check if a tag name is registered as an H component.
     * @param tagName The tag name (case-insensitive).
     * @return True if the tag is registered, false otherwise.
     */
    public static boolean isHComponent(String tagName) {
        if (tagName == null) {
            return false;
        }

        return COMPONENT_MAP.containsKey(tagName.toLowerCase());
    }

    /**
     * Get all registered component tag names.
     * @return A set of all registered tag names (lowercase).
     */
    public static java.util.Set<String> getAllComponentNames() {
        return COMPONENT_MAP.keySet();
    }

    /**
     * Get the full component registry map.
     * @return A copy of the component registry map.
     */
    public static Map<String, Class<? extends ComponentBuilder>> getComponentMap() {
        return new HashMap<>(COMPONENT_MAP);
    }

    /**
     * Register a component class with the component registry.
     * @param componentClass The component class to register.
     */
    private static void registerComponent(Class<? extends ComponentBuilder> componentClass) {
        try {
            // Get the TAG_NAME static field from the component class or its superclasses
            Field tagNameField = null;
            Class<?> currentClass = componentClass;

            while (currentClass != null) {
                try {
                    tagNameField = currentClass.getDeclaredField("TAG_NAME");
                    break;
                } catch (NoSuchFieldException e) {
                    currentClass = currentClass.getSuperclass();
                }
            }

            // If the field is not found, throw an error
            if (tagNameField == null) {
                throw new NoSuchFieldException("TAG_NAME field not found in class hierarchy of " + componentClass.getName());
            }

            tagNameField.setAccessible(true);
            String tagName = (String) tagNameField.get(null);

            // If tagName is null, throw an error
            if (tagName == null) {
                throw new NullPointerException("TAG_NAME is null for component class " + componentClass.getName());
            }

            COMPONENT_MAP.put(tagName, componentClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
