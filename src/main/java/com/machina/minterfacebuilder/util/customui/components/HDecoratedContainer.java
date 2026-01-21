package com.machina.minterfacebuilder.util.customui.components;

import java.util.Map;

import com.machina.minterfacebuilder.factory.ComponentFactory;
import com.machina.minterfacebuilder.helpers.LayoutMode;
import com.machina.minterfacebuilder.util.customui.ComponentBuilder;
import com.machina.minterfacebuilder.util.customui.HytaleCustomUIComponent;
import com.machina.minterfacebuilder.util.customui.components.base.Button;
import com.machina.minterfacebuilder.util.customui.components.base.Group;
import com.machina.minterfacebuilder.util.customui.helpers.SoundsHelper;

/**
 * H DecoratedContainer component (Group with Title + Content + decorations + CloseButton).
 * Based on @DecoratedContainer from Common.ui (line 779-816).
 */
public class HDecoratedContainer extends HytaleCustomUIComponent {
    public static final String TAG_NAME = "HDecoratedContainer";

    private ComponentBuilder title;
    private ComponentBuilder content;

    public HDecoratedContainer() {
        this(null);
    }

    public HDecoratedContainer(Map<String, String> attributes) {
        super("Group");

        boolean showCloseButton = attributes != null
            && attributes.containsKey("closebutton")
            && Boolean.parseBoolean(attributes.get("closebutton"));

        appendChild(
            ComponentFactory.create(
                Group.class,
                Map.of(
                    "Id", "Title",
                    "Anchor", Map.of(
                        "Height", 38,
                        "Top", 0
                    ),
                    "Background", Map.of(
                        "TexturePath", "Common/ContainerHeader.png",
                        "HorizontalBorder", 50,
                        "VerticalBorder", 0
                    ),
                    "Padding", Map.of("Top", 7)
                ),
                ComponentFactory.create(
                    Group.class,
                    Map.of(
                        "Id", "ContainerDecorationTop",
                        "Anchor", Map.of(
                            "Width", 236,
                            "Height", 11,
                            "Top", -12
                        ),
                        "Background", Map.of("TexturePath", "Common/ContainerDecorationTop.png")
                    )
                )
            )
        );

        appendChild(
            ComponentFactory.create(
                Group.class,
                Map.of(
                    "Id", "Content",
                    "LayoutMode", LayoutMode.TOP,
                    "Anchor", Map.of("Top", 38),
                    "Padding", Map.of(
                        "Full", 17, // 9 + 8 expanded
                        "Top", 8
                    ),
                    "Background", Map.of(
                        "TexturePath", "Common/ContainerPatch.png",
                        "Border", 23
                    )
                )
            )
        );

        appendChild(
            ComponentFactory.create(
                Group.class,
                Map.of(
                    "Id", "ContainerDecorationBottom",
                    "Anchor", Map.of(
                        "Width", 236,
                        "Height", 11,
                        "Bottom", -6
                    ),
                    "Background", Map.of("TexturePath", "Common/ContainerDecorationBottom.png")
                )
            )
        );

        appendChild(
            ComponentFactory.create(
                Button.class,
                Map.of(
                    "Id", "CloseButton",
                    "Anchor", Map.of(
                        "Width", 32,
                        "Height", 32,
                        "Top", -8,
                        "Right", -8
                    ),
                    "Style", Map.of(
                        "Default", Map.of("Background", Map.of("TexturePath", "Common/ContainerCloseButton.png")),
                        "Hovered", Map.of("Background", Map.of("TexturePath", "Common/ContainerCloseButtonHovered.png")),
                        "Pressed", Map.of("Background", Map.of("TexturePath", "Common/ContainerCloseButtonPressed.png")),
                        "Sounds", SoundsHelper.getButtonsCancel()
                    ),
                    "Visible", showCloseButton
                )
            )
        );

        if (attributes != null && attributes.containsKey("id")) {
            this.setId(attributes.get("id"));
        }
    }

    /**
     * Set the title of the container.
     * @param title The title component.
     * @return The container.
     */
    public HDecoratedContainer setTitle(ComponentBuilder title) {
        this.title = title;
        return this;
    }

    /**
     * Set the content of the container.
     * @param content The content component.
     * @return The container.
     */
    public HDecoratedContainer setContent(ComponentBuilder content) {
        this.content = content;
        return this;
    }
}
