package com.machina.minterfacebuilder.util.customui.components;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.machina.minterfacebuilder.helpers.Alignment;
import com.machina.minterfacebuilder.model.LiteralValue;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.components.base.Label;

/**
 * H TitleLabel component with inline expanded styles.
 * Based on @TitleLabel from Common.ui (line 8-10).
 */
public class HTitleLabel extends Label {
    public static final String TAG_NAME = "HTitleLabel";

    public HTitleLabel() {
        this(null);
    }

    public HTitleLabel(@Nullable Map<String, String> attributes) {
        super(attributes);

        // Set Style
        this.setStyle(Map.of(
            "FontSize", LiteralValue.of(40),
            "Alignment", Alignment.CENTER
        ));

        // Text
        if (attributes != null && attributes.containsKey("text")) {
            this.setText(attributes.get("text"));
        }
        
        // ID
        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }
}
