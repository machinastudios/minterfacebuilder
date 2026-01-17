package com.machina.minterfacebuilder.util.customui.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for expanding sound definitions from Sounds.ui inline.
 * All sounds are expanded without aliases, using direct values from Sounds.ui.
 */
public class SoundsHelper {
    /**
     * Get ButtonsLight sounds expanded inline.
     * Based on @ButtonsLight from Sounds.ui (line 33-44)
     */
    public static Map<String, Object> getButtonsLight() {
        Map<String, Object> sounds = new HashMap<>();
        
        Map<String, Object> activate = new HashMap<>();
        activate.put("SoundPath", "Sounds/ButtonsLightActivate.ogg");
        activate.put("MinPitch", -0.2);
        activate.put("MaxPitch", 0.2);
        activate.put("Volume", 2);
        sounds.put("Activate", activate);
        
        Map<String, Object> hover = new HashMap<>();
        hover.put("SoundPath", "Sounds/ButtonsLightHover.ogg");
        hover.put("Volume", 6);
        sounds.put("MouseHover", hover);
        
        return sounds;
    }
    
    /**
     * Get ButtonsDestructive sounds expanded inline.
     * Based on @ButtonsDestructive from Sounds.ui (line 59-70)
     */
    public static Map<String, Object> getButtonsDestructive() {
        Map<String, Object> sounds = new HashMap<>();
        
        Map<String, Object> activate = new HashMap<>();
        activate.put("SoundPath", "Sounds/ButtonsCancelActivate.ogg");
        activate.put("MinPitch", -0.4);
        activate.put("MaxPitch", 0.4);
        activate.put("Volume", 6);
        sounds.put("Activate", activate);
        
        Map<String, Object> hover = new HashMap<>();
        hover.put("SoundPath", "Sounds/ButtonsLightHover.ogg");
        hover.put("Volume", 6);
        sounds.put("MouseHover", hover);
        
        return sounds;
    }
    
    /**
     * Get ButtonsCancel sounds (alias for ButtonsDestructive).
     * Based on @ButtonsCancel usage (uses @ButtonsDestructive)
     */
    public static Map<String, Object> getButtonsCancel() {
        return getButtonsDestructive();
    }
    
    /**
     * Get Tick sound path.
     * Based on @Tick from Sounds.ui (line 180)
     */
    public static String getTick() {
        return "Sounds/TickActivate.ogg";
    }
    
    /**
     * Get Untick sound path.
     * Based on @Untick from Sounds.ui (line 182)
     */
    public static String getUntick() {
        return "Sounds/UntickActivate.ogg";
    }
    
    /**
     * Get ButtonsLightHover sound path.
     * Based on @ButtonsLightHover from Sounds.ui (line 31)
     */
    public static String getButtonsLightHover() {
        return "Sounds/ButtonsLightHover.ogg";
    }
    
    /**
     * Get ButtonsLightActivate sound path.
     * Based on @ButtonsLightActivate from Sounds.ui (line 30)
     */
    public static String getButtonsLightActivate() {
        return "Sounds/ButtonsLightActivate.ogg";
    }
    
    /**
     * Get ButtonsMain sounds expanded inline.
     * Based on @ButtonsMain from Sounds.ui (line 17-28)
     */
    public static Map<String, Object> getButtonsMain() {
        Map<String, Object> sounds = new HashMap<>();
        
        Map<String, Object> activate = new HashMap<>();
        activate.put("SoundPath", "Sounds/ButtonsMainActivate.ogg");
        activate.put("Volume", 6);
        sounds.put("Activate", activate);
        
        Map<String, Object> hover = new HashMap<>();
        hover.put("SoundPath", "Sounds/ButtonsMainHover.ogg");
        hover.put("MinPitch", -0.1);
        hover.put("MaxPitch", 0.1);
        hover.put("Volume", 2);
        sounds.put("MouseHover", hover);
        
        return sounds;
    }
    
    /**
     * Get DropdownBox sounds expanded inline.
     * Based on @DropdownBox from Sounds.ui (line 91-104)
     * Note: In the original Sounds.ui this is defined as DropdownBoxSounds(...).
     * The Map structure should work, but may need adjustment depending on ComponentBuilder behavior.
     */
    public static Map<String, Object> getDropdownBox() {
        Map<String, Object> sounds = new HashMap<>();
        
        Map<String, Object> activate = new HashMap<>();
        activate.put("SoundPath", "Sounds/TickActivate.ogg");
        activate.put("Volume", 6);
        sounds.put("Activate", activate);
        
        Map<String, Object> hover = new HashMap<>();
        hover.put("SoundPath", "Sounds/ButtonsLightHover.ogg");
        hover.put("Volume", 6);
        sounds.put("MouseHover", hover);
        
        Map<String, Object> close = new HashMap<>();
        close.put("SoundPath", "Sounds/UntickActivate.ogg");
        close.put("Volume", 6);
        sounds.put("Close", close);
        
        return sounds;
    }
}
