package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.components.base.Button;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Button component with inline expanded styles.
 * Based on @Button from Common.ui (line 182-194).
 */
public class HButton extends Button {
    public static final String TAG_NAME = "HButton";

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
        Map<String, Object> padding = new HashMap<>();
        padding.put("Horizontal", 24); // @ButtonPadding expanded
        this.setProperty("Padding", padding);
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
