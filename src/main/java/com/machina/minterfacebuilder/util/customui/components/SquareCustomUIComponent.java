package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

public class SquareCustomUIComponent extends ComponentBuilder {
    public SquareCustomUIComponent(String color) {
        super("Group");

        this.setProperty("Background", color == "white" ? "#ffffff" : "#000000");
        this.addComment("SquareUIComponent");
    }

    public SquareCustomUIComponent(String color, int width, int height) {
        super("Group");

        this.setProperty("Background", color == "white" ? "#ffffff" : "#000000");
        this.setProperty("Anchor", Map.of("Width", width, "Height", height));
    }
}
