package com.bllk.Apka.customComponents;

import javax.swing.*;
import java.awt.*;

public class BetterJLabel extends JLabel {
    public BetterJLabel(String text, Color color, Font font) {
        setText(text);
        setForeground(color);
        setFont(font);
    }
}
