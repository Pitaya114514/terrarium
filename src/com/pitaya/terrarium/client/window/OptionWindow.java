package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OptionWindow extends JDialog {

    public OptionWindow(Frame mainWindow) {
        super(mainWindow, "设置", true);
        setSize(900, 600);
        setResizable(false);
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new FlowLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        HashMap<String, JTextField> tempProperties = new HashMap<>();
        for (Map.Entry<Object, Object> entry : Main.getClient().properties.entrySet()) {
            JTextField f = new JTextField((String) entry.getValue(), 7);
            tempProperties.put((String) entry.getKey(), f);
        }
        for (Map.Entry<String, JTextField> entry : tempProperties.entrySet()) {
            optionPanel.add(new JLabel(entry.getKey()));
            optionPanel.add(entry.getValue());
        }
        JButton button1 = new JButton("应用");
        button1.addActionListener(event -> {
            for (Map.Entry<String, JTextField> entry : tempProperties.entrySet()) {
                Main.getClient().properties.setProperty(entry.getKey(), entry.getValue().getText());
            }
            Main.getClient().outputProperties("client.properties");
        });
        JButton button2 = new JButton("取消");
        button2.addActionListener(event -> {
            this.setVisible(false);
        });
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        add(optionPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
