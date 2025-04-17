package com.pitaya.terrarium.client;

import com.pitaya.terrarium.Main;

import javax.swing.*;

public class MainWindow extends JFrame {
    private OptionWindow optionWindow;

    public MainWindow() {
        super("Terrarium");
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("File");
        JMenuItem menuItem1 = new JMenuItem("New world");
        menuItem1.addActionListener(e -> {
            Main.getClient().runTerrarium();
            Main.getClient().loadRenderer();
        });
        menu1.add(menuItem1);
        JMenuItem menuItem2 = new JMenuItem("Load world");
        menu1.add(menuItem2);
        JMenuItem menuItem3 = new JMenuItem("Option");
        menuItem3.addActionListener(e -> {
            if (optionWindow == null) {
                optionWindow = new OptionWindow(this);
            }
            if (!optionWindow.isVisible()) {
                optionWindow.setVisible(true);
            }
        });
        menu1.add(menuItem3);
        menuBar.add(menu1);
        setLocation(20, 20);
        setSize(450, 300);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        setJMenuBar(menuBar);
        add(mainPanel);
    }
}

