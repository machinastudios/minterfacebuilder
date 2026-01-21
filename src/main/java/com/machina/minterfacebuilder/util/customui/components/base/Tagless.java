package com.machina.minterfacebuilder.util.customui.components.base;

import java.util.Map;

import com.machina.minterfacebuilder.util.customui.ComponentBuilder;

public class Tagless extends ComponentBuilder {
    public Tagless(String id) {
        super("#" + id);
    }

    public Tagless(String id, Map<String, Object> attributes) {
        super("#" + id);
        setProperties(attributes);
    }
}
