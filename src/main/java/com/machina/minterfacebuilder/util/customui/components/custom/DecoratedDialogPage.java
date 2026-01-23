package com.machina.minterfacebuilder.util.customui.components.custom;

import java.util.Map;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.LayoutMode;
import com.machina.minterfacebuilder.util.customui.PageBuilder;
import com.machina.minterfacebuilder.util.customui.components.HDecoratedContainer;
import com.machina.minterfacebuilder.util.customui.components.HPageOverlay;

public abstract class DecoratedDialogPage extends PageBuilder {
    /**
     * The page overlay.
     */
    private HPageOverlay overlay;

    /**
     * The dialog.
     */
    private HDecoratedContainer dialog;

    public DecoratedDialogPage() {
        super("Group", null, CustomPageLifetime.CanDismiss);
    }

    @Override
    protected void construct() {
        // Create the page overlay
        this.overlay = ComponentFactory.create(
            HPageOverlay.class,
            Map.of("LayoutMode", LayoutMode.MIDDLE)
        );

        // Create the dialog
        this.dialog = ComponentFactory.create(
            HDecoratedContainer.class
        );

        // Allow constructing the dialog
        constructDialog();

        // Add the dialog to the overlay
        overlay.appendChild(dialog);

        // Add the dialog to the current page
        appendChild(overlay);
    }

    /**
     * Construct the dialog.
     */
    protected abstract void constructDialog();

    /**
     * Get the dialog.
     * @return The dialog.
     */
    public HDecoratedContainer getDialog() {
        return dialog;
    }

    /**
     * Get the page overlay.
     * @return The page overlay.
     */
    public HPageOverlay getOverlay() {
        return overlay;
    }
}
