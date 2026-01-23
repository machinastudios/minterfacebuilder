package com.machina.minterfacebuilder.util.customui.components.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.LayoutMode;
import com.machina.minterfacebuilder.util.customui.PageBuilder;
import com.machina.minterfacebuilder.util.customui.components.HPageOverlay;

public class FullscreenPageOverlayPage extends PageBuilder {
    /**
     * The layout mode of the page.
     */
    private LayoutMode layoutMode = LayoutMode.MIDDLE;

    public FullscreenPageOverlayPage() {
        super("Group", null, CustomPageLifetime.CanDismiss);
    }

    @Override
    protected void construct() {
        // Get the children and clone
        List<Object> children = new ArrayList<>(getChildren());

        // Clear the children
        clearChildren();

        // Create the page overlay
        var overlay = ComponentFactory.create(
            HPageOverlay.class,
            Map.of("LayoutMode", layoutMode)
        );

        // Add all inherited the children to the overlay
        overlay.appendChild(children);

        // Add the overlay to the current page
        appendChild(overlay);
    }
}
