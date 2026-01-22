package com.machina.minterfacebuilder.util.customui.helpers;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.helpers.Color;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

/**
 * Helper class for applying Common.ui component styles and properties to ComponentBuilder instances.
 * All styles are expanded inline without aliases, using direct values from Common.ui.
 */
public class CommonUIHelper {
    // Constants from Common.ui (expanded inline)
    private static final int BUTTON_BORDER = 12;
    private static final int PRIMARY_BUTTON_HEIGHT = 44;
    private static final int SMALL_BUTTON_HEIGHT = 32;
    private static final int BUTTON_PADDING = 24;
    private static final String DISABLED_COLOR = "#797b7c";
    private static final int DROPDOWN_BOX_HEIGHT = 32;
    
    // ========== Private Helper Methods for Building Styles ==========
    
    /**
     * Build default button label style (inline, no aliases).
     * Based on @DefaultButtonLabelStyle from Common.ui (line 42-49)
     */
    private static Map<String, Object> buildDefaultButtonLabelStyle() {
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 17);
        labelStyle.put("TextColor", "#bfcdd5");
        labelStyle.put("RenderBold", true);
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        return labelStyle;
    }
    
    /**
     * Build disabled button label style.
     * Based on @DefaultButtonDisabledLabelStyle from Common.ui (line 51-54)
     */
    private static Map<String, Object> buildDisabledButtonLabelStyle() {
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 17);
        labelStyle.put("TextColor", DISABLED_COLOR);
        labelStyle.put("RenderBold", true);
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        return labelStyle;
    }
    
    /**
     * Build secondary button label style.
     * Based on @SecondaryButtonLabelStyle from Common.ui (line 60-63)
     */
    private static Map<String, Object> buildSecondaryButtonLabelStyle() {
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 17);
        labelStyle.put("TextColor", "#bdcbd3");
        labelStyle.put("RenderBold", true);
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        return labelStyle;
    }
    
    /**
     * Build small button label style.
     * Based on @SmallButtonLabelStyle from Common.ui (line 70-73)
     */
    private static Map<String, Object> buildSmallButtonLabelStyle() {
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 14);
        labelStyle.put("TextColor", "#bfcdd5");
        labelStyle.put("RenderBold", true);
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        return labelStyle;
    }
    
    /**
     * Build small secondary button label style.
     * Based on @SmallSecondaryButtonLabelStyle from Common.ui (line 80-83)
     */
    private static Map<String, Object> buildSmallSecondaryButtonLabelStyle() {
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("FontSize", 14);
        labelStyle.put("TextColor", "#bdcbd3");
        labelStyle.put("RenderBold", true);
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        labelStyle.put("HorizontalAlignment", Alignment.CENTER);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        return labelStyle;
    }
    
    /**
     * Build patch style for square button background.
     */
    private static String buildSquareButtonBackground(String state) {
        String textureName = "Primary_Square";
        if ("Hovered".equals(state)) textureName = "Primary_Square_Hovered";
        else if ("Pressed".equals(state)) textureName = "Primary_Square_Pressed";
        else if ("Disabled".equals(state)) textureName = "Disabled";
        return "PatchStyle(TexturePath: \"Common/Buttons/" + textureName + ".png\", Border: " + BUTTON_BORDER + ")";
    }
    
    /**
     * Build patch style for destructive button background.
     */
    private static String buildDestructiveButtonBackground(String state) {
        String textureName = "Destructive";
        if ("Hovered".equals(state)) textureName = "Destructive_Hovered";
        else if ("Pressed".equals(state)) textureName = "Destructive_Pressed";
        else if ("Disabled".equals(state)) textureName = "Disabled";
        return "PatchStyle(TexturePath: \"Common/Buttons/" + textureName + ".png\", Border: " + BUTTON_BORDER + ")";
    }
    
    /**
     * Build patch style for secondary button background.
     */
    private static String buildSecondaryButtonBackground(String state) {
        String textureName = "Secondary";
        if ("Hovered".equals(state)) textureName = "Secondary_Hovered";
        else if ("Pressed".equals(state)) textureName = "Secondary_Pressed";
        else if ("Disabled".equals(state)) textureName = "Disabled";
        return "PatchStyle(TexturePath: \"Common/Buttons/" + textureName + ".png\", Border: " + BUTTON_BORDER + ")";
    }
    
    /**
     * Build patch style for tertiary button background.
     */
    private static String buildTertiaryButtonBackground(String state) {
        String textureName = "Tertiary";
        if ("Hovered".equals(state)) textureName = "Tertiary_Hovered";
        else if ("Pressed".equals(state)) textureName = "Tertiary_Pressed";
        else if ("Disabled".equals(state)) textureName = "Disabled";
        return "PatchStyle(TexturePath: \"Common/Buttons/" + textureName + ".png\", Border: " + BUTTON_BORDER + ")";
    }
    
    /**
     * Build TextButtonStyle for primary button with given sounds.
     */
    private static Map<String, Object> buildPrimaryTextButtonStyle(Map<String, Object> sounds) {
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary.png\", VerticalBorder: " + BUTTON_BORDER + ", HorizontalBorder: 80)");
        defaultState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary_Hovered.png\", VerticalBorder: " + BUTTON_BORDER + ", HorizontalBorder: 80)");
        hoveredState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Primary_Pressed.png\", VerticalBorder: " + BUTTON_BORDER + ", HorizontalBorder: 80)");
        pressedState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", "PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", VerticalBorder: " + BUTTON_BORDER + ", HorizontalBorder: 80)");
        disabledState.put("LabelStyle", buildDisabledButtonLabelStyle());
        style.put("Disabled", disabledState);
        
        if (sounds != null) {
            style.put("Sounds", sounds);
        }
        
        return style;
    }
    
    /**
     * Build ButtonStyle for square button with given sounds.
     */
    private static Map<String, Object> buildSquareButtonStyle(Map<String, Object> sounds) {
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildSquareButtonBackground("Default"));
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildSquareButtonBackground("Hovered"));
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildSquareButtonBackground("Pressed"));
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildSquareButtonBackground("Disabled"));
        style.put("Disabled", disabledState);
        
        if (sounds != null) {
            style.put("Sounds", sounds);
        }
        
        return style;
    }
    
    // ========== Public Apply Methods for Buttons ==========
    
    /**
     * Apply TextButton style and properties based on @TextButton from Common.ui (line 167-180).
     */
    public static void applyTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        if (attributes != null && attributes.containsKey("anchor")) {
            // Can merge additional anchor properties if needed
        }
        builder.setProperty("Anchor", anchor);
        
        // Set Style
        Map<String, Object> sounds = SoundsHelper.getButtonsLight();
        if (attributes != null && attributes.containsKey("sounds")) {
            // Allow override, but for now use default
        }
        builder.setProperty("Style", buildPrimaryTextButtonStyle(sounds));
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", BUTTON_PADDING);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply Button style and properties based on @Button from Common.ui (line 182-194).
     */
    public static void applyButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor (square button)
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style
        Map<String, Object> sounds = SoundsHelper.getButtonsLight();
        builder.setProperty("Style", buildSquareButtonStyle(sounds));
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", BUTTON_PADDING);
        builder.setProperty("Padding", padding);
    }
    
    /**
     * Apply CancelTextButton style and properties based on @CancelTextButton from Common.ui (line 196-209).
     */
    public static void applyCancelTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (destructive button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildDestructiveButtonBackground("Default"));
        defaultState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildDestructiveButtonBackground("Hovered"));
        hoveredState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildDestructiveButtonBackground("Pressed"));
        pressedState.put("LabelStyle", buildDefaultButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildDestructiveButtonBackground("Disabled"));
        disabledState.put("LabelStyle", buildDisabledButtonLabelStyle());
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsCancel());
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", BUTTON_PADDING);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply CancelButton style and properties based on @CancelButton from Common.ui (line 211-223).
     */
    public static void applyCancelButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor (square destructive button)
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (destructive button style without label)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildDestructiveButtonBackground("Default"));
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildDestructiveButtonBackground("Hovered"));
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildDestructiveButtonBackground("Pressed"));
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildDestructiveButtonBackground("Disabled"));
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply SecondaryTextButton style and properties based on @SecondaryTextButton from Common.ui (line 255-268).
     */
    public static void applySecondaryTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (secondary button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildSecondaryButtonBackground("Default"));
        defaultState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildSecondaryButtonBackground("Hovered"));
        hoveredState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildSecondaryButtonBackground("Pressed"));
        pressedState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildSecondaryButtonBackground("Disabled"));
        Map<String, Object> disabledLabelStyle = new HashMap<>(buildSecondaryButtonLabelStyle());
        disabledLabelStyle.put("TextColor", DISABLED_COLOR);
        disabledState.put("LabelStyle", disabledLabelStyle);
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", BUTTON_PADDING);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply SecondaryButton style and properties based on @SecondaryButton from Common.ui (line 270-282).
     */
    public static void applySecondaryButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor (square secondary button)
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (secondary button style without label)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildSecondaryButtonBackground("Default"));
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildSecondaryButtonBackground("Hovered"));
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildSecondaryButtonBackground("Pressed"));
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildSecondaryButtonBackground("Disabled"));
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply TertiaryTextButton style and properties based on @TertiaryTextButton from Common.ui (line 284-297).
     */
    public static void applyTertiaryTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (tertiary button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildTertiaryButtonBackground("Default"));
        defaultState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildTertiaryButtonBackground("Hovered"));
        hoveredState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildTertiaryButtonBackground("Pressed"));
        pressedState.put("LabelStyle", buildSecondaryButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildTertiaryButtonBackground("Disabled"));
        Map<String, Object> disabledLabelStyle = new HashMap<>(buildSecondaryButtonLabelStyle());
        disabledLabelStyle.put("TextColor", DISABLED_COLOR);
        disabledState.put("LabelStyle", disabledLabelStyle);
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", BUTTON_PADDING);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply TertiaryButton style and properties based on @TertiaryButton from Common.ui (line 299-311).
     */
    public static void applyTertiaryButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor (square tertiary button)
        Map<String, Object> anchor = new HashMap<>();
        int height = PRIMARY_BUTTON_HEIGHT;
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (tertiary button style without label)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildTertiaryButtonBackground("Default"));
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildTertiaryButtonBackground("Hovered"));
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildTertiaryButtonBackground("Pressed"));
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildTertiaryButtonBackground("Disabled"));
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply SmallSecondaryTextButton style and properties based on @SmallSecondaryTextButton from Common.ui (line 225-238).
     */
    public static void applySmallSecondaryTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", SMALL_BUTTON_HEIGHT);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (small secondary button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildSecondaryButtonBackground("Default"));
        defaultState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildSecondaryButtonBackground("Hovered"));
        hoveredState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildSecondaryButtonBackground("Pressed"));
        pressedState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildSecondaryButtonBackground("Disabled"));
        Map<String, Object> disabledLabelStyle = new HashMap<>(buildSmallSecondaryButtonLabelStyle());
        disabledLabelStyle.put("TextColor", DISABLED_COLOR);
        disabledState.put("LabelStyle", disabledLabelStyle);
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 16);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply SmallTertiaryTextButton style and properties based on @SmallTertiaryTextButton from Common.ui (line 240-253).
     */
    public static void applySmallTertiaryTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", SMALL_BUTTON_HEIGHT);
        builder.setProperty("Anchor", anchor);
        
        // Set Style (small tertiary button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", buildTertiaryButtonBackground("Default"));
        defaultState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", buildTertiaryButtonBackground("Hovered"));
        hoveredState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", buildTertiaryButtonBackground("Pressed"));
        pressedState.put("LabelStyle", buildSmallSecondaryButtonLabelStyle());
        style.put("Pressed", pressedState);
        
        Map<String, Object> disabledState = new HashMap<>();
        disabledState.put("Background", buildTertiaryButtonBackground("Disabled"));
        Map<String, Object> disabledLabelStyle = new HashMap<>(buildSmallSecondaryButtonLabelStyle());
        disabledLabelStyle.put("TextColor", DISABLED_COLOR);
        disabledState.put("LabelStyle", disabledLabelStyle);
        style.put("Disabled", disabledState);
        
        style.put("Sounds", SoundsHelper.getButtonsLight());
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 16);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply CloseButton style and properties based on @CloseButton from Common.ui (line 338-346).
     */
    public static void applyCloseButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Top", -16);
        anchor.put("Right", -16);
        anchor.put("Width", 32);
        anchor.put("Height", 32);
        builder.setProperty("Anchor", anchor);
        
        // Set Style
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("Background", "Common/ContainerCloseButton.png");
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("Background", "Common/ContainerCloseButtonHovered.png");
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("Background", "Common/ContainerCloseButtonPressed.png");
        style.put("Pressed", pressedState);
        
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply HeaderTextButton style and properties based on @HeaderTextButton from Common.ui (line 692-695).
     */
    public static void applyHeaderTextButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Style (header text button style)
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> defaultLabelStyle = new HashMap<>();
        defaultLabelStyle.put("FontSize", 15);
        defaultLabelStyle.put("VerticalAlignment", Alignment.CENTER);
        defaultLabelStyle.put("RenderUppercase", true);
        defaultLabelStyle.put("TextColor", "#d3d6db");
        defaultLabelStyle.put("FontName", "Default");
        defaultLabelStyle.put("RenderBold", true);
        defaultLabelStyle.put("LetterSpacing", 1);
        
        Map<String, Object> defaultState = new HashMap<>();
        defaultState.put("LabelStyle", defaultLabelStyle);
        style.put("Default", defaultState);
        
        Map<String, Object> hoveredLabelStyle = new HashMap<>(defaultLabelStyle);
        hoveredLabelStyle.put("TextColor", "#eaebee");
        Map<String, Object> hoveredState = new HashMap<>();
        hoveredState.put("LabelStyle", hoveredLabelStyle);
        style.put("Hovered", hoveredState);
        
        Map<String, Object> pressedLabelStyle = new HashMap<>(defaultLabelStyle);
        pressedLabelStyle.put("TextColor", "#b6bbc2");
        Map<String, Object> pressedState = new HashMap<>();
        pressedState.put("LabelStyle", pressedLabelStyle);
        style.put("Pressed", pressedState);
        
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Right", 22);
        padding.put("Left", 15);
        padding.put("Bottom", 1);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply BackButton style and properties based on @BackButton from Common.ui (line 832-837).
     */
    public static void applyBackButton(ComponentBuilder builder, Map<String, String> attributes) {
        // Set LayoutMode
        builder.setProperty("LayoutMode", "Left");
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Left", 50);
        anchor.put("Bottom", 50);
        anchor.put("Width", 110);
        anchor.put("Height", 27);
        builder.setProperty("Anchor", anchor);
        
        // BackButton is just a Group container with BackButton child
        // The actual BackButton element is created by the child
    }
    
    // ========== Public Apply Methods for Inputs ==========
    
    /**
     * Apply TextField style and properties based on @TextField from Common.ui (line 424-432).
     */
    public static void applyTextField(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Style
        Map<String, Object> style = new HashMap<>();
        builder.setProperty("Style", style);
        
        // Set PlaceholderStyle
        Map<String, Object> placeholderStyle = new HashMap<>();
        placeholderStyle.put("TextColor", "#6e7da1");
        builder.setProperty("PlaceholderStyle", placeholderStyle);
        
        // Set Background
        builder.setProperty("Background", LiteralValue.of("PatchStyle(TexturePath: \"Common/InputBox.png\", Border: 16)"));
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 38);
        builder.setProperty("Anchor", anchor);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 10);
        builder.setProperty("Padding", padding);
    }
    
    /**
     * Apply NumberField style and properties based on @NumberField from Common.ui (line 434-442).
     */
    public static void applyNumberField(ComponentBuilder builder, Map<String, String> attributes) {
        // Same as TextField
        applyTextField(builder, attributes);
    }
    
    /**
     * Apply DropdownBox style and properties based on @DropdownBox from Common.ui (line 479-484).
     */
    public static void applyDropdownBox(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 330);
        anchor.put("Height", DROPDOWN_BOX_HEIGHT);
        builder.setProperty("Anchor", anchor);
        
        // Build DropdownBoxStyle (complex style with many properties)
        Map<String, Object> style = new HashMap<>();
        
        // Backgrounds
        style.put("DefaultBackground", "PatchStyle(TexturePath: \"Common/Dropdown.png\", Border: 16)");
        style.put("HoveredBackground", "PatchStyle(TexturePath: \"Common/DropdownHovered.png\", Border: 16)");
        style.put("PressedBackground", "PatchStyle(TexturePath: \"Common/DropdownPressed.png\", Border: 16)");
        
        // Arrow textures
        style.put("DefaultArrowTexturePath", "Common/DropdownCaret.png");
        style.put("HoveredArrowTexturePath", "Common/DropdownCaret.png");
        style.put("PressedArrowTexturePath", "Common/DropdownPressedCaret.png");
        style.put("ArrowWidth", 13);
        style.put("ArrowHeight", 18);
        
        // Label styles
        Map<String, Object> labelStyle = new HashMap<>();
        labelStyle.put("TextColor", "#96a9be");
        labelStyle.put("RenderUppercase", true);
        labelStyle.put("VerticalAlignment", Alignment.CENTER);
        labelStyle.put("FontSize", 13);
        style.put("LabelStyle", labelStyle);
        
        Map<String, Object> entryLabelStyle = new HashMap<>(labelStyle);
        entryLabelStyle.put("TextColor", "#b7cedd");
        style.put("EntryLabelStyle", entryLabelStyle);
        
        // Other properties
        style.put("HorizontalPadding", 8);
        style.put("PanelAlign", Alignment.RIGHT);
        style.put("PanelOffset", 7);
        style.put("EntryHeight", 31);
        style.put("EntriesInViewport", 10);
        style.put("HorizontalEntryPadding", 7);
        style.put("HoveredEntryBackground", Color.of("#0a0f17"));
        style.put("PressedEntryBackground", Color.of("#0f1621"));
        style.put("FocusOutlineSize", 1);
        style.put("FocusOutlineColor", "#ffffff(0.4)");
        
        // Scrollbar style (simplified for now)
        Map<String, Object> scrollbarStyle = new HashMap<>();
        scrollbarStyle.put("Spacing", 6);
        scrollbarStyle.put("Size", 6);
        scrollbarStyle.put("Background", LiteralValue.of("PatchStyle(TexturePath: \"Common/Scrollbar.png\", Border: 3)"));
        scrollbarStyle.put("Handle", LiteralValue.of("PatchStyle(TexturePath: \"Common/ScrollbarHandle.png\", Border: 3)"));
        style.put("PanelScrollbarStyle", scrollbarStyle);
        
        style.put("PanelBackground", "PatchStyle(TexturePath: \"Common/DropdownBox.png\", Border: 16)");
        style.put("PanelPadding", 6);
        
        // Sounds
        style.put("Sounds", SoundsHelper.getDropdownBox());
        style.put("EntrySounds", SoundsHelper.getButtonsLight());
        
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply HeaderSearch style and properties based on @HeaderSearch from Common.ui (line 662-682).
     * Note: This is a Group containing CompactTextField, implementation may need adjustment.
     */
    public static void applyHeaderSearch(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 200);
        anchor.put("Right", 0);
        builder.setProperty("Anchor", anchor);
        
        // This is a Group, so LayoutMode not needed
        // The CompactTextField child should be added separately
    }
    
    // ========== Public Apply Methods for Checkboxes ==========
    
    /**
     * Apply CheckBox style and properties based on @CheckBox from Common.ui (line 390-395).
     */
    public static void applyCheckBox(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 22);
        anchor.put("Height", 22);
        builder.setProperty("Anchor", anchor);
        
        // Set Background
        builder.setProperty("Background", "PatchStyle(TexturePath: \"Common/CheckBoxFrame.png\", Border: 7)");
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Full", 4);
        builder.setProperty("Padding", padding);
        
        // Set Style
        Map<String, Object> style = new HashMap<>();
        
        Map<String, Object> unchecked = new HashMap<>();
        Map<String, Object> uncheckedDefault = new HashMap<>();
        uncheckedDefault.put("Color", Color.of("#00000000"));
        unchecked.put("DefaultBackground", uncheckedDefault);
        unchecked.put("HoveredBackground", uncheckedDefault);
        unchecked.put("PressedBackground", uncheckedDefault);
        Map<String, Object> uncheckedDisabled = new HashMap<>();
        uncheckedDisabled.put("Color", Color.of("#424242"));
        unchecked.put("DisabledBackground", uncheckedDisabled);
        Map<String, Object> untickSound = new HashMap<>();
        untickSound.put("SoundPath", LiteralValue.of(SoundsHelper.getUntick()));
        untickSound.put("Volume", 6);
        unchecked.put("ChangedSound", untickSound);
        style.put("Unchecked", unchecked);
        
        Map<String, Object> checked = new HashMap<>();
        Map<String, Object> checkedBackground = new HashMap<>();
        checkedBackground.put("TexturePath", LiteralValue.of("Common/Checkmark.png"));
        checked.put("DefaultBackground", checkedBackground);
        checked.put("HoveredBackground", checkedBackground);
        checked.put("PressedBackground", checkedBackground);
        Map<String, Object> tickSound = new HashMap<>();
        tickSound.put("SoundPath", LiteralValue.of(SoundsHelper.getTick()));
        tickSound.put("Volume", 6);
        checked.put("ChangedSound", tickSound);
        style.put("Checked", checked);
        
        builder.setProperty("Style", style);
        
        // Set Value if provided
        if (attributes != null && attributes.containsKey("value")) {
            boolean value = Boolean.parseBoolean(attributes.get("value"));
            builder.setProperty("Value", value);
        }
    }
    
    /**
     * Apply CheckBoxWithLabel style and properties based on @CheckBoxWithLabel from Common.ui (line 397-414).
     * Note: This is a Group containing CheckBox and Label, implementation may need adjustment.
     */
    public static void applyCheckBoxWithLabel(ComponentBuilder builder, Map<String, String> attributes) {
        // Set LayoutMode
        builder.setProperty("LayoutMode", "Left");
        
        // CheckBox and Label children should be added separately
        // This helper just sets up the Group structure
    }
    
    // ========== Public Apply Methods for Containers ==========
    
    /**
     * Apply Panel style and properties based on @Panel from Common.ui (line 4-6).
     */
    public static void applyPanel(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Background
        builder.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerFullPatch.png\", Border: 20)");
    }
    
    /**
     * Apply Container style and properties based on @Container from Common.ui (line 750-777).
     * Note: This is a Group with Title and Content children, implementation may need adjustment.
     */
    public static void applyContainer(ComponentBuilder builder, Map<String, String> attributes) {
        // Set ContentPadding (default)
        int fullPadding = 9 + 8;
        Map<String, Object> contentPadding = new HashMap<>();
        contentPadding.put("Full", fullPadding);
        
        // This is a Group structure with Title and Content children
        // Children should be added separately
    }
    
    /**
     * Apply DecoratedContainer style and properties based on @DecoratedContainer from Common.ui (line 779-816).
     * Note: This is a Group with Title, Content, and decorations, implementation may need adjustment.
     */
    public static void applyDecoratedContainer(ComponentBuilder builder, Map<String, String> attributes) {
        // Similar to Container but with decorations
        // Children should be added separately
    }
    
    /**
     * Apply PageOverlay style and properties based on @PageOverlay from Common.ui (line 818-820).
     */
    public static void applyPageOverlay(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Background
        builder.setProperty("Background", Color.of("#000000", 0.45));
    }
    
    // ========== Public Apply Methods for Labels ==========
    
    /**
     * Apply TitleLabel style and properties based on @TitleLabel from Common.ui (line 8-10).
     */
    public static void applyTitleLabel(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Style
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 40);
        style.put("Alignment", Alignment.CENTER);
        builder.setProperty("Style", style);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply Title style and properties based on @Title from Common.ui (line 594-604).
     */
    public static void applyTitle(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Style
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 15);
        style.put("VerticalAlignment", Alignment.CENTER);
        style.put("RenderUppercase", true);
        style.put("TextColor", Color.of("#b4c8c9"));
        style.put("FontName", "Secondary");
        style.put("RenderBold", true);
        style.put("LetterSpacing", 0);

        Alignment alignment = Alignment.CENTER;
        if (attributes != null && attributes.containsKey("alignment")) {
            alignment = Alignment.of(attributes.get("alignment"));
        }

        style.put("HorizontalAlignment", alignment);
        builder.setProperty("Style", style);
        
        // Set Padding
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 19);
        builder.setProperty("Padding", padding);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply Subtitle style and properties based on @Subtitle from Common.ui (line 578-582).
     */
    public static void applySubtitle(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Style
        Map<String, Object> style = new HashMap<>();
        style.put("FontSize", 15);
        style.put("RenderUppercase", true);
        style.put("TextColor", "#96a9be");
        builder.setProperty("Style", style);
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Bottom", 10);
        builder.setProperty("Anchor", anchor);
        
        // Set Text if provided
        if (attributes != null && attributes.containsKey("text")) {
            builder.setProperty("Text", attributes.get("text"));
        }
    }
    
    /**
     * Apply PanelTitle style and properties based on @PanelTitle from Common.ui (line 702-718).
     * Note: This is a Group with Label and separator, implementation may need adjustment.
     */
    public static void applyPanelTitle(ComponentBuilder builder, Map<String, String> attributes) {
        // Set LayoutMode
        builder.setProperty("LayoutMode", "Top");
        
        // This is a Group structure with Label and separator children
        // Children should be added separately
    }
    
    // ========== Public Apply Methods for Other Components ==========
    
    /**
     * Apply ColorPicker style and properties based on @DefaultColorPickerStyle from Common.ui (line 314-325).
     */
    public static void applyColorPicker(ComponentBuilder builder, Map<String, String> attributes) {
        // Build ColorPickerStyle
        Map<String, Object> style = new HashMap<>();
        style.put("OpacitySelectorBackground", "Common/ColorPickerOpacitySelectorBackground.png");
        style.put("ButtonBackground", "Common/ColorPickerButton.png");
        style.put("ButtonFill", "Common/ColorPickerFill.png");
        
        Map<String, Object> textFieldDecoration = new HashMap<>();
        Map<String, Object> textFieldDefault = new HashMap<>();
        textFieldDefault.put("Background", "#000000(0.5)");
        textFieldDecoration.put("Default", textFieldDefault);
        style.put("TextFieldDecoration", textFieldDecoration);
        
        Map<String, Object> textFieldPadding = new HashMap<>();
        textFieldPadding.put("Left", 7);
        style.put("TextFieldPadding", textFieldPadding);
        style.put("TextFieldHeight", 32);
        
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply Scrollbar style and properties based on @DefaultScrollbarStyle from Common.ui (line 348-355).
     */
    public static void applyScrollbar(ComponentBuilder builder, Map<String, String> attributes) {
        // Build ScrollbarStyle
        Map<String, Object> style = new HashMap<>();
        style.put("Spacing", 6);
        style.put("Size", 6);
        style.put("Background", "PatchStyle(TexturePath: \"Common/Scrollbar.png\", Border: 3)");
        style.put("Handle", "PatchStyle(TexturePath: \"Common/ScrollbarHandle.png\", Border: 3)");
        style.put("HoveredHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleHovered.png\", Border: 3)");
        style.put("DraggedHandle", "PatchStyle(TexturePath: \"Common/ScrollbarHandleDragged.png\", Border: 3)");
        
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply Spinner style and properties based on @DefaultSpinner from Common.ui (line 555-561).
     */
    public static void applySpinner(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 32);
        anchor.put("Height", 32);
        builder.setProperty("Anchor", anchor);
        
        // Set TexturePath
        builder.setProperty("TexturePath", "Common/Spinner.png");
        
        // Set Frame
        Map<String, Object> frame = new HashMap<>();
        frame.put("Width", 32);
        frame.put("Height", 32);
        frame.put("PerRow", 8);
        frame.put("Count", 72);
        builder.setProperty("Frame", frame);
        
        // Set FramesPerSecond
        builder.setProperty("FramesPerSecond", 30);
    }
    
    /**
     * Apply Slider style and properties based on @DefaultSliderStyle from Common.ui (line 822-830).
     */
    public static void applySlider(ComponentBuilder builder, Map<String, String> attributes) {
        // Build SliderStyle
        Map<String, Object> style = new HashMap<>();
        style.put("Background", "PatchStyle(TexturePath: \"Common/SliderBackground.png\", Border: 2)");
        style.put("Handle", "Common/SliderHandle.png");
        style.put("HandleWidth", 16);
        style.put("HandleHeight", 16);
        
        Map<String, Object> sounds = new HashMap<>();
        Map<String, Object> hoverSound = new HashMap<>();
        hoverSound.put("SoundPath", SoundsHelper.getButtonsLightHover());
        hoverSound.put("Volume", 6);
        sounds.put("MouseHover", hoverSound);
        style.put("Sounds", sounds);
        
        builder.setProperty("Style", style);
    }
    
    /**
     * Apply ContentSeparator style and properties based on @ContentSeparator from Common.ui (line 548-553).
     */
    public static void applyContentSeparator(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 1);
        builder.setProperty("Anchor", anchor);
        
        // Set Background
        builder.setProperty("Background", "#2b3542");
    }
    
    /**
     * Apply VerticalSeparator style and properties based on @VerticalSeparator from Common.ui (line 720-723).
     */
    public static void applyVerticalSeparator(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Background
        builder.setProperty("Background", "PatchStyle(TexturePath: \"Common/ContainerVerticalSeparator.png\")");
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 6);
        anchor.put("Top", -2);
        builder.setProperty("Anchor", anchor);
    }
    
    /**
     * Apply PanelSeparatorFancy style and properties based on @PanelSeparatorFancy from Common.ui (line 725-745).
     * Note: This is a Group with three child Groups, implementation may need adjustment.
     */
    public static void applyPanelSeparatorFancy(ComponentBuilder builder, Map<String, String> attributes) {
        // Set LayoutMode
        builder.setProperty("LayoutMode", "Left");
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Height", 8);
        builder.setProperty("Anchor", anchor);
        
        // This is a Group structure with three children
        // Children should be added separately
    }
    
    // ========== Public Apply Methods for Special Components ==========
    
    /**
     * Apply ActionButtonContainer style and properties based on @ActionButtonContainer from Common.ui (line 563-566).
     */
    public static void applyActionButtonContainer(ComponentBuilder builder, Map<String, String> attributes) {
        // Set LayoutMode
        builder.setProperty("LayoutMode", "Right");
        
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Right", 50);
        anchor.put("Bottom", 50);
        anchor.put("Height", 27);
        builder.setProperty("Anchor", anchor);
    }
    
    /**
     * Apply ActionButtonSeparator style and properties based on @ActionButtonSeparator from Common.ui (line 568-570).
     */
    public static void applyActionButtonSeparator(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 35);
        builder.setProperty("Anchor", anchor);
    }
    
    /**
     * Apply HeaderSeparator style and properties based on @HeaderSeparator from Common.ui (line 697-700).
     */
    public static void applyHeaderSeparator(ComponentBuilder builder, Map<String, String> attributes) {
        // Set Anchor
        Map<String, Object> anchor = new HashMap<>();
        anchor.put("Width", 5);
        anchor.put("Height", 34);
        builder.setProperty("Anchor", anchor);
        
        // Set Background
        builder.setProperty("Background", "Common/HeaderTabSeparator.png");
    }
}
