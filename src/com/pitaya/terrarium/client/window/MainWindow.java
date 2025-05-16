package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.client.Player;
import com.pitaya.terrarium.game.World;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private OptionWindow optionWindow;
    private ConnectWindow connectWindow;
    private SingleplayerWindow singleplayerWindow;

    public MainWindow() {
        super("Terrarium");
        JPanel mainPanel = new JPanel();
        JMenuBar menuBar = getBar();
        setLocation(20, 20);
        setSize(450, 300);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
            if (connectWindow == null || !connectWindow.isVisible()) {
                connectWindow = new ConnectWindow(this);
                connectWindow.setVisible(true);
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

