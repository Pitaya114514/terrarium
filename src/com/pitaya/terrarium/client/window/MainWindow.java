package com.pitaya.terrarium.client.window;

import javax.swing.*;

public class MainWindow extends JFrame {
    private OptionWindow optionWindow;
    private SingleplayerWindow singleplayerWindow;
    private MultiplayerWindow multiplayerWindow;

    public MainWindow() {
        super("Terrarium");
        JPanel mainPanel = new JPanel();
        JMenuBar menuBar = getBar();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocation(20, 20);
        setSize(450, 300);
        setResizable(false);
        setJMenuBar(menuBar);
        add(mainPanel);
    }

    private JMenuBar getBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("游戏");
        JMenu menu2 = new JMenu("设置");
        JMenuItem menuItem1 = new JMenuItem("单人游戏");
        menuItem1.addActionListener(e -> {
            if (singleplayerWindow == null || !singleplayerWindow.isVisible()) {
                singleplayerWindow = new SingleplayerWindow(this);
                singleplayerWindow.setVisible(true);
            }
        });
        JMenuItem menuItem2 = new JMenuItem("多人游戏");
        menuItem2.addActionListener(e -> {
            if (multiplayerWindow == null || !multiplayerWindow.isVisible()) {
                multiplayerWindow = new MultiplayerWindow(this);
                multiplayerWindow.setVisible(true);
            }
        });
        JMenuItem menuItem3 = new JMenuItem("设置");
        menuItem3.addActionListener(e -> {
            if (optionWindow == null || !optionWindow.isVisible()) {
                optionWindow = new OptionWindow(this);
                optionWindow.setVisible(true);
            }
        });
        menu1.add(menuItem1);
        menu1.add(menuItem2);
        menu2.add(menuItem3);
        menuBar.add(menu1);
        menuBar.add(menu2);
        return menuBar;
    }
}

