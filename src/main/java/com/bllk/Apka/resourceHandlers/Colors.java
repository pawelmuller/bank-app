package com.bllk.Apka.resourceHandlers;

import java.awt.*;

public class Colors {
    private static Color bright_text_color, orange, blue, dark_grey, bright_grey, grey, light_grey;

    public Colors() {
        bright_text_color = Color.decode("#EEEEEE");
        blue = Color.decode("#1891FF");
        orange = Color.decode("#FF7F00");
        dark_grey = Color.decode("#222222");
        grey = Color.decode("#333333");
        light_grey = Color.decode("#444444");
        bright_grey = Color.decode("#808080");
    }

    public static Color getBrightTextColor() {
        return bright_text_color;
    }
    public static Color getOrange() {
        return orange;
    }
    public static Color getBlue() {
        return blue;
    }
    public static Color getDarkGrey() {
        return dark_grey;
    }
    public static Color getGrey() {
        return grey;
    }
    public static Color getLightGrey() {
        return light_grey;
    }
    public static Color getBrightGrey() {
        return bright_grey;
    }
}
