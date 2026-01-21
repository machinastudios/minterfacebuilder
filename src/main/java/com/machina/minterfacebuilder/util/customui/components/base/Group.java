package com.machina.minterfacebuilder.util.customui.components.base;

import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

public class Group extends ComponentBuilder {
    public Group() {
        super("Group");
    }

    public Group(Map<String, Object> attributes) {
        super("Group");
        setProperties(attributes);
    }
}
