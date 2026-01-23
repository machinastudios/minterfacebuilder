package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.components.base.Button;
import com.machina.minterfacebuilder.util.customui.components.base.Group;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Button component with inline expanded styles.
 * Based on @Button from Common.ui (line 182-194).
 */
public class HButton extends Button {
    public static final String TAG_NAME = "HButton";
    
    /**
     * The icon to display on the button
     */
    private String icon = null;

    /**
     * The height of the icon
     */
    private int iconHeight = 24;

    /**
     * The width of the icon
     */
    private int iconWidth = 24;

    public HButton() {
        this(null);
    }

    public HButton(Map<String, String> attributes) {
        super();
        
        // Configure Anchor (square button)
        Map<String, Object> anchor = new HashMap<>();
        int height = 44; // @PrimaryButtonHeight expanded
        if (attributes != null && attributes.containsKey("height")) {
            try {
                height = Integer.parseInt(attributes.get("height"));
            } catch (NumberFormatException e) {
                // Use default
            }
        }
        anchor.put("Height", height);
        anchor.put("Width", height);

        this.setProperty("Anchor", anchor);

        this.setProperty("Style", FnCall.of(
            "ButtonStyle", Map.of(
                "Background", LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Primary_Square.png\", Border: 12)"),
                "Hovered", LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Primary_Square_Hovered.png\", Border: 12)"),
                "Pressed", LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Primary_Square_Pressed.png\", Border: 12)"),
                "Disabled", LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", Border: 12)"),
                "Sounds", SoundsHelper.getButtonsLight()
            )
        ));

        // Padding
        this.setProperty("Padding", Map.of("Horizontal", 24));
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }

    /**
     * Set the icon to display on the button
     * @param icon The icon to display on the button
     * @return The button
     */
    @Nonnull
    public HButton setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Set the icon to display on the button
     * @param icon The icon to display on the button
     * @param iconHeight The height of the icon
     * @param iconWidth The width of the icon
     * @return The button
     */
    @Nonnull
    public HButton setIcon(String icon, int iconHeight, int iconWidth) {
        this.icon = icon;
        this.iconHeight = iconHeight;
        this.iconWidth = iconWidth;
        return this;
    }

    /**
     * Set the height of the icon
     * @param iconHeight The height of the icon
     * @return The button
     */
    @Nonnull
    public HButton setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
        return this;
    }

    /**
     * Set the width of the icon
     * @param iconWidth The width of the icon
     * @return The button
     */
    @Nonnull
    public HButton setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
        return this;
    }

    @Override
    public String build() {
        // If an icon is set, add it to the button
        if (icon != null) {
            var iconComponent = ComponentFactory.create(Group.class)
                .setProperty("Background", icon);

            // Get or create the anchor property
            var anchor = getPropertyIgnoreCase("anchor", Map.of());
            
            // Set the height of the icon
            if (iconHeight > 0) {
                anchor.put("Height", iconHeight);
            }

            // Set the width of the icon
            if (iconWidth > 0) {
                anchor.put("Width", iconWidth);
            }

            // Set the anchor property
            iconComponent.setProperty("Anchor", anchor);

            appendChild(iconComponent);
        }

        return super.build();
    }
}
