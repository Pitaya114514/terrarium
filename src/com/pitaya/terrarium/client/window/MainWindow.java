package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;

import javax.swing.*;

public class MainWindow extends JFrame {
    private OptionWindow optionWindow;
    private ConnectWindow connectWindow;

    public MainWindow() {
        super("Terrarium");
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("File");
        JMenu menu2 = new JMenu("Connect");
        JMenuItem menuItem1 = new JMenuItem("New world");
        menuItem1.addActionListener(e -> {
            Main.getClient().runTerrarium();
            Main.getClient().loadRenderer();
        });
        JMenuItem menuItem2 = new JMenuItem("Load world");
        JMenuItem menuItem3 = new JMenuItem("Option");
        menuItem3.addActionListener(e -> {
            if (optionWindow == null) {
                optionWindow = new OptionWindow(this);
            }
            if (!optionWindow.isVisible()) {
                optionWindow.setVisible(true);
            }
        });
        JMenuItem menuItem4 = new JMenuItem("Connect");
        menuItem4.addActionListener(e -> {
            if (connectWindow == null) {
                connectWindow = new ConnectWindow(this);
            }
            if (!connectWindow.isVisible()) {
                connectWindow.setVisible(true);
            }
        });
        menu1.add(menuItem1);
        menu1.add(menuItem2);
        menu1.add(menuItem3);
        menu2.add(menuItem4);
        menuBar.add(menu1);
        menuBar.add(menu2);
        setLocation(20, 20);
        setSize(450, 300);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        setJMenuBar(menuBar);
        add(mainPanel);
    }
}

