package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import com.machina.minterfacebuilder.helpers.FnCall;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.components.base.Button;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H Tertiary Button component with inline expanded styles.
 * Based on @TertiaryButton from Common.ui (line 299-311).
 */
public class HTertiaryButton extends Button {
    public static final String TAG_NAME = "HTertiaryButton";

    public HTertiaryButton() {
        this(null);
    }

    public HTertiaryButton(Map<String, String> attributes) {
        super();
        Map<String, Object> anchor = new HashMap<>();
        int height = 44;
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

        Map<String, Object> style = new HashMap<>();
        style.put(
            "Default",
            Map.of(
                "Background",
                LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Tertiary.png\", Border: 12)")
            )
        );
        style.put(
            "Hovered",
            Map.of(
                "Background",
                LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Tertiary_Hovered.png\", Border: 12)")
            )
        );
        style.put(
            "Pressed",
            Map.of(
                "Background",
                LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Tertiary_Pressed.png\", Border: 12)")
            )
        );
        style.put(
            "Disabled",
            Map.of(
                "Background",
                LiteralValue.of("PatchStyle(TexturePath: \"Common/Buttons/Disabled.png\", Border: 12)")
            )
        );
        style.put("Sounds", SoundsHelper.getButtonsLight());

        this.setProperty("Style", FnCall.of("ButtonStyle", style));

        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
