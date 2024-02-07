package org.polyfrost.example.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import org.polyfrost.example.ExampleMod;

/**
 * The main Config entrypoint that extends the Config type and init the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */
public class TestConfig extends Config {
    @Switch(
            name = "Enable",
            size = OptionSize.DUAL
    )
    public static boolean enableFilters = false; // Enables or Disables the filters

    @Slider(
            name = "Hue",
            min = -180f, max = 180f // Minimum and maximum values for the hue slider.
    )
    public static float hueValue = 0f; // The default value for the hue Slider.

    @Slider(
            name = "Saturation",
            min = 0f, max = 200f // Minimum and maximum values for the saturation slider.
    )
    public static float saturationValue = 100f; // The default value for the saturation Slider.

    @Slider(
            name = "Lightness",
            min = -100f, max = 100f // Minimum and maximum values for the lightness slider.
    )
    public static float lightnessValue = 0f; // The default value for the lightness Slider.

    public TestConfig() {
        super(new Mod(ExampleMod.NAME, ModType.UTIL_QOL), ExampleMod.MODID + ".json");
        initialize();
    }
}

