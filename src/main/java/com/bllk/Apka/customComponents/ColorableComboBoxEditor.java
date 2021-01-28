package com.bllk.Apka.customComponents;

import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;

public class ColorableComboBoxEditor extends BasicComboBoxEditor {
    public ColorableComboBoxEditor (Color background) {
        super();
        super.editor.setBackground(background);
    }
}