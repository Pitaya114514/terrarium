package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OptionWindow extends JDialog {

    public OptionWindow(JFrame mainWindow) {
        super(mainWindow, "Setting", true);
        setSize(900, 600);
        setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));
        HashMap<String, JTextField> tempProperties = new HashMap<>();
        for (Map.Entry<Object, Object> entry : Main.getClient().properties.entrySet()) {
            JTextField f = new JTextField((String) entry.getValue(), 7);
            tempProperties.put((String) entry.getKey(), f);
        }
        for (Map.Entry<String, JTextField> entry : tempProperties.entrySet()) {
            panel.add(new JLabel(entry.getKey()));
            panel.add(entry.getValue());
        }
        JButton button1 = new JButton("Apply");
        button1.addActionListener(event -> {
            for (Map.Entry<String, JTextField> entry : tempProperties.entrySet()) {
                Main.getClient().properties.setProperty(entry.getKey(), entry.getValue().getText());
            }
        });
        JButton button2 = new JButton("Cancel");
        panel.add(button1);
        panel.add(button2);
        add(panel);
    }
}
