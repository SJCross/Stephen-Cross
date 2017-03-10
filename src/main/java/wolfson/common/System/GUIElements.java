package wolfson.common.System;

import java.awt.*;

import javax.swing.*;

//Component to report back should always be added first, so it can be accessed via getComponent(0)
public class GUIElements {
    public static JPanel ComboList(String text_in, String[] list_in) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JTextField text = new JTextField(text_in);
        text.setEditable(false);
        text.setBorder(null);
        JComboBox<String> list = new JComboBox<String>(list_in);
        text.setPreferredSize(list.getPreferredSize());

        c.gridx = 1;
        panel.add(list,c);
        c.gridx = 0;
        panel.add(text,c);

        return panel;
    }

    public static JPanel EditField(String text_in, String edit_in, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JTextField text = new JTextField(text_in);
        text.setEditable(false);
        text.setBorder(null);
        JTextField edit = new JTextField(edit_in,w);

        c.gridx = 1;
        panel.add(edit,c);
        c.gridx = 0;
        panel.add(text,c);

        return panel;
    }

    public static JPanel CheckBox(String text_in, String edit_in, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JCheckBox cb = new JCheckBox(text_in);

        c.gridx = 0;
        panel.add(cb,c);

        return panel;
    }

    public static JPanel Button(String text_in) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JButton button = new JButton(text_in);

        c.gridx = 0;
        panel.add(button,c);

        return panel;

    }

    public static JPanel TextField(String text_in, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JTextField text = new JTextField(text_in,w);
        text.setEditable(false);
        text.setBorder(null);

        c.gridx = 0;
        panel.add(text,c);

        return panel;
    }

    public static JPanel ButtonAndField(String text_in, int w) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);
        c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        JButton button = new JButton(text_in);

        JTextField edit = new JTextField("1",w);
        edit.setPreferredSize(button.getPreferredSize());

        c.gridx = 1;
        panel.add(edit,c);
        c.gridx = 0;
        panel.add(button,c);

        return panel;
    }

    public static JPanel Space() {
        JTextField spaaace = new JTextField("");
        spaaace.setEditable(false);
        spaaace.setBorder(null);
        JPanel panel = new JPanel();
        panel.add(spaaace);

        return panel;
    }
}