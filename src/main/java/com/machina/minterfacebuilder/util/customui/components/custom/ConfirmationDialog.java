package com.machina.minterfacebuilder.util.customui.components.custom;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.LayoutMode;
import com.machina.minterfacebuilder.helpers.TranslationKey;
import com.machina.minterfacebuilder.util.customui.PageBuilder;
import com.machina.minterfacebuilder.util.customui.components.HDecoratedContainer;
import com.machina.minterfacebuilder.util.customui.components.HPageOverlay;
import com.machina.minterfacebuilder.util.customui.components.HSecondaryTextButton;
import com.machina.minterfacebuilder.util.customui.components.HTextButton;
import com.machina.minterfacebuilder.util.customui.components.HTitle;
import com.machina.minterfacebuilder.util.customui.components.base.Group;
import com.machina.minterfacebuilder.util.customui.components.base.Label;

public class ConfirmationDialog extends PageBuilder {
    /**
     * Show a confirmation dialog to the player.
     * @param playerRef The player to show the dialog to.
     * @param options
     */
    public static ConfirmationDialog create(Options options) {
        return new ConfirmationDialog(options);
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
    private final Consumer<PageEvent<String>> onConfirm;

    /**
     * The action to perform when the cancel button is pressed.
     */
    private final Consumer<PageEvent<String>> onCancel;

    /**
     * Whether to close the dialog when the cancel button is pressed.
     */
    private boolean closeOnCancel = true;

    public ConfirmationDialog(Options options) {
        super("Group", null, CustomPageLifetime.CanDismiss);

        this.title = options.title;
        this.description = options.description;
        this.confirmText = options.confirmText;
        this.cancelText = options.cancelText;
        this.closeOnCancel = options.closeOnCancel;

        // If there's a onConfirmOrCancel action, add it to the options
        if (options.onConfirmOrCancel != null) {
            this.onConfirm = (e) -> {
                options.onConfirmOrCancel.accept(true);
            };

            this.onCancel = (e) -> {
                options.onConfirmOrCancel.accept(false);
            };
        } else {
            this.onConfirm = options.onConfirm;
            this.onCancel = options.onCancel;
        }
    }

    public ConfirmationDialog(String title, String description, String confirmText, String cancelText, Consumer<PageEvent<String>> onConfirm, Consumer<PageEvent<String>> onCancel) {
        super("Group", null, CustomPageLifetime.CanDismiss);

        this.title = title;
        this.description = description;
        this.confirmText = confirmText;
        this.cancelText = cancelText;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }
    
    public ConfirmationDialog(TranslationKey title, TranslationKey description, TranslationKey confirmText, TranslationKey cancelText, Consumer<PageEvent<String>> onConfirm, Consumer<PageEvent<String>> onCancel) {
        this(title.getValue(), description.getValue(), confirmText.getValue(), cancelText.getValue(), onConfirm, onCancel);
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

        // When clicking the confirm button, call the onConfirm action
        addEventListener("#ConfirmButton", EventType.CLICK, (e) -> {
            this.onConfirm.accept((PageEvent<String>) e);
        });

        // When clicking the cancel button, call the onCancel action
        addEventListener("#CancelButton", EventType.CLICK, (e) -> {
            this.onCancel.accept((PageEvent<String>) e);

            // If closeOnCancel is true, close the dialog
            if (this.closeOnCancel) {
                this.close();
            }
        });
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

    public static class Options {
        public Object title;
        public Object description;
        public Object confirmText;
        public Object cancelText;
        public boolean closeOnCancel = true;
        public Consumer<PageEvent<String>> onConfirm;
        public Consumer<PageEvent<String>> onCancel;
        public Consumer<Boolean> onConfirmOrCancel;

        public Options(@Nonnull TranslationKey title, @Nonnull TranslationKey description, @Nonnull Consumer<Boolean> onConfirmOrCancel) {
            this.title = title;
            this.description = description;
            this.onConfirmOrCancel = onConfirmOrCancel;
        }

        public Options(@Nonnull String title, @Nonnull String description, @Nonnull Consumer<Boolean> onConfirmOrCancel) {
            this(
                title,
                description,
                null,
                null,
                null,
                null
            );

            this.onConfirmOrCancel = onConfirmOrCancel;
        }

        public Options(
            @Nonnull String title,
            @Nonnull String description,
            @Nullable String confirmText,
            @Nullable String cancelText,
            @Nullable Consumer<PageEvent<String>> onConfirm,
            @Nullable Consumer<PageEvent<String>> onCancel
        ) {
            this.title = title;
            this.description = description;
            this.confirmText = confirmText;
            this.cancelText = cancelText;
            this.onConfirm = onConfirm;
            this.onCancel = onCancel;
        }
        
        /**
         * Set whether to close the dialog when the cancel button is pressed.
         * @param closeOnCancel
         * @return The options.
         */
        public Options setCloseOnCancel(boolean closeOnCancel) {
            this.closeOnCancel = closeOnCancel;
            return this;
        }
    }
}
