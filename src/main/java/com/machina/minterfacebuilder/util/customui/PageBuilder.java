package com.machina.minterfacebuilder.util.customui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;
import com.machina.minterfacebuilder.model.HTMLCustomUITemplate.DynamicEventData;
import com.machina.shared.util.PlayerUtil;

public class PageBuilder extends ComponentBuilder {
    /**
     * The player reference.
     */
    protected Ref<EntityStore> playerRef = null;

    /**
     * The event builder.
     */
    protected UIEventBuilder eventBuilder = null;

    /**
     * The store.
     */
    protected Store<EntityStore> store = null;

    /**
     * The event listeners.
     */
    protected List<EventListener> eventListeners = new ArrayList<>();

    /**
     * The page lifetime.
     * Defaults to CustomPageLifetime.CanDismiss if not specified.
     */
    @Nonnull
    protected final CustomPageLifetime lifetime;

    /**
     * The custom page.
     */
    private InteractiveCustomUIPage<DynamicEventData> customPage;

    /**
     * Create a new PageBuilder with default lifetime (CanDismiss).
     * This constructor creates a dummy codec that will be replaced when build() is called.
     * 
     * @param playerRef The player reference.
     */
    @SuppressWarnings("null")
    public PageBuilder(@Nonnull PlayerRef playerRef) {
        this("Group", playerRef.getReference(), CustomPageLifetime.CanDismiss);
    }

    /**
     * Create a new PageBuilder with default lifetime (CanDismiss).
     * This constructor creates a dummy codec that will be replaced when build() is called.
     * 
     * @param playerRef The player reference.
     */
    public PageBuilder(@Nonnull Ref<EntityStore> playerRef) {
        this("Group", playerRef, CustomPageLifetime.CanDismiss);
    }

    /**
     * Create a new PageBuilder.
     * 
     * @param playerRef The player reference.
     * @param lifetime The page lifetime (defaults to CanDismiss if null).
     * @param eventDataCodec The event data codec.
     */
    public PageBuilder(String baseTag, @Nonnull Ref<EntityStore> playerRef, @Nullable CustomPageLifetime lifetime) {
        super(baseTag);

        this.playerRef = playerRef;
        this.lifetime = lifetime != null ? lifetime : CustomPageLifetime.CanDismiss;
    }

    /**
     * Set the player reference.
     * @param playerRef The player reference.
     * @return The page builder.
     */
    public PageBuilder setPlayerRef(@Nonnull Ref<EntityStore> playerRef) {
        this.playerRef = playerRef;
        this.store = playerRef.getStore();
        return this;
    }

    /**
     * Add an event listener.
     * @param selector The selector.
     * @param eventType The event type.
     * @param action The action.
     * @return The page builder.
     */
    public PageBuilder addEventListener(String selector, EventType eventType, Consumer<PageEvent<?>> action) {
        var eventListener = new EventListener(selector, eventType, action);
        this.eventListeners.add(eventListener);
        return this;
    }

    /**
     * Send the page to the player.
     * @return The page builder.
     */
    public PageBuilder send() {
        // If there's no player reference, commandBuilder or eventBuilder, throw an exception
        if (this.playerRef == null) {
            throw new IllegalStateException("Player reference is not set");
        }

        if (this.store == null) {
            throw new IllegalStateException("Entity store reference is not set");
        }

        // Join the world thread
        store.getExternalData().getWorld().execute(() -> {
            // Build the page contents
            String uiString = super.build();

            // Limit UI string to 4MB to prevent server overload
            final long MAX_UI_SIZE = 4L * 1024 * 1024; // 4MB in bytes
            long uiSizeBytes = uiString.getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            
            if (uiSizeBytes > MAX_UI_SIZE) {
                throw new IllegalStateException(String.format(
                    "Generated UI string is too large: %d bytes (max: %d bytes / 4MB). " +
                    "The UI template is generating too much content. Please simplify your HTML template.",
                    uiSizeBytes, MAX_UI_SIZE
                ));
            }

            // Get the player component
            PlayerRef playerRef = store.getComponent(this.playerRef, PlayerRef.getComponentType());
            if (playerRef == null) {
                throw new IllegalStateException("Could not get player reference from entity store");
            }

            // Get the player component
            Player player = PlayerUtil.getPlayer(playerRef);

            // Allow subclasses to add custom bindings or modifications
            this.construct();

            // Build the page
            this.customPage = this.buildPage();

            // Send the page to the player
            player.getPageManager().openCustomPage(null, store, this.customPage);
        });

        return this;
    }

    /**
     * Send the page to the player.
     * @param playerRef The player reference.
     * @return The page builder.
     */
    public PageBuilder send(@Nonnull PlayerRef playerRef) {
        this.setPlayerRef(playerRef.getReference());
        return this.send();
    }

    /**
     * Send the page to the player.
     * @param playerRef The player reference.
     * @return The page builder.
     */
    public PageBuilder send(@Nonnull Ref<EntityStore> playerRef) {
        this.setPlayerRef(playerRef);
        return this.send();
    }

    /**
     * Get the custom page.
     * @return The custom page.
     */
    public InteractiveCustomUIPage<DynamicEventData> getCustomPage() {
        return this.customPage;
    }

    /**
     * Build a dynamic BuilderCodec based on the variables in the parsed template.
     * This method parses the HTML first if not already parsed, then builds a codec
     * from the template's variables.
     * 
     * @return A BuilderCodec for DynamicEventData that can handle all variables in the template.
     */
    @Nonnull
    public BuilderCodec<HTMLCustomUITemplate.DynamicEventData> buildEventDataCodec() {
        // Build the event data codec
        return BuilderCodec.<HTMLCustomUITemplate.DynamicEventData>builder(
            HTMLCustomUITemplate.DynamicEventData.class,
            HTMLCustomUITemplate.DynamicEventData::new
        )
            .addField(
                new KeyedCodec<>("CHANGE", Codec.STRING),
                    (entry, s) -> entry.setValue("CHANGE", s),
                    (entry) -> entry.getString("CHANGE")
            )
            .addField(
                new KeyedCodec<>("CLICK", Codec.STRING),
                    (entry, s) -> entry.setValue("CLICK", s),
                    (entry) -> entry.getString("CLICK")
            )
            .build();
    }

    /**
     * Close the page.
     */
    public void close() {
        var playerComponent = store.getComponent(playerRef, Player.getComponentType());
        playerComponent.getPageManager().setPage(playerRef, store, Page.None);
    }

    /**
     * Construct the page.
     */
    protected void construct() {
        // Do nothing
    }

    /**
     * Build the page.
     * @return The page.
     */
    private InteractiveCustomUIPage<HTMLCustomUITemplate.DynamicEventData> buildPage() {
        // Save self as an accessible variable
        PageBuilder self = this;

        // Get the playerRef from the playerRef
        PlayerRef playerRef = this.store.getComponent(this.playerRef, PlayerRef.getComponentType());
        if (playerRef == null) {
            throw new IllegalStateException("Player reference is not set");
        }

        return new InteractiveCustomUIPage<HTMLCustomUITemplate.DynamicEventData>(
            playerRef,
            this.lifetime,
            this.buildEventDataCodec()
        ) {
            @Override
            public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store) {
                // Create empty Group #MIBRoot first (required for PageBuilder to display)
                commandBuilder.append("Pages/MInterfaceBuilder_Dummy.ui");

                // Build the page
                var pageContents = self.build();

                // Save to a test file
                try {
                    Files.writeString(Path.of("page.ui"), pageContents);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Append the UI inline to the #MIBRoot group
                commandBuilder.appendInline("#MIBRoot", pageContents);

                // Register the events
                for (EventListener eventListener : self.eventListeners) {
                    EventData eventData = null;
                    CustomUIEventBindingType eventType = null;

                    switch (eventListener.eventType) {
                        // If it's a click event
                        case CLICK:
                            eventData = EventData.of("CLICK", eventListener.selector);
                            eventType = CustomUIEventBindingType.Activating;
                            break;

                        // If it's a change event
                        case CHANGE:
                            eventData = EventData.of("CHANGE", eventListener.selector);
                            eventType = CustomUIEventBindingType.ValueChanged;
                            break;
                    }

                    // Add the event binding
                    eventBuilder.addEventBinding(
                        eventType,
                        eventListener.selector,
                        eventData,
                        false
                    );
                }
            }

            @Override
            public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull HTMLCustomUITemplate.DynamicEventData data) {
                var changeEvent = data.getString("CHANGE");
                var clickEvent = data.getString("CLICK");

                // Create the page event
                PageEvent<String> pageEvent = new PageEvent<>(changeEvent != null ? changeEvent : clickEvent, self);

                // If it's a change event
                if (changeEvent != null) {
                    // Get the event listener
                    self.eventListeners.stream()
                        .filter(e -> e.eventType == EventType.CHANGE && e.selector.equals(changeEvent))
                        .forEach(e -> e.action.accept(pageEvent));
                } else
                // If it's a click event
                if (clickEvent != null) {
                    // Get the event listener
                    self.eventListeners.stream()
                        .filter(e -> e.eventType == EventType.CLICK && e.selector.equals(clickEvent))
                        .forEach(e -> e.action.accept(pageEvent));
                }
            }
        };
    }

    public static class EventListener {
        /**
         * The selector of the element.
         */
        @Nonnull
        public final String selector;

        /**
         * The type of event.
         */
        @Nonnull
        public final EventType eventType;

        /**
         * The action to perform when the event is fired.
         */
        @Nonnull
        public final Consumer<PageEvent<?>> action;

        public EventListener(@Nonnull String selector, @Nonnull EventType eventType, @Nonnull Consumer<PageEvent<?>> action) {
            this.selector = selector;
            this.eventType = eventType;
            this.action = action;
        }
    }

    public enum EventType {
        /**
         * Fired when the user clicks on the element.
         */
        CLICK,

        /**
         * Fired when the user changes the value of the element.
         */
        CHANGE
    }

    /**
     * The event data.
     */
    public static class PageEvent<T> {
        /**
         * The value of the event data.
         */
        private final T value;

        /**
         * The component builder.
         */
        private final PageBuilder componentBuilder;

        public PageEvent(T value, PageBuilder componentBuilder) {
            this.value = value;
            this.componentBuilder = componentBuilder;
        }

        /**
         * Get the value of the event data.
         * @return The value of the event data.
         */
        public T getValue() {
            return this.value;
        }

        /**
         * Get the component builder.
         * @return The component builder.
         */
        public PageBuilder getPageBuilder() {
            return this.componentBuilder;
        }

        /**
         * Get the custom page.
         * @return The custom page.
         */
        public InteractiveCustomUIPage<DynamicEventData> getCustomPage() {
            return this.componentBuilder.getCustomPage();
        }
    }
}
