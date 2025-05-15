package com.pitaya.terrarium.client.window;

import javax.swing.*;
import java.awt.*;

public class MultiplayerWindow extends JDialog {
    public MultiplayerWindow(MainWindow mainWindow) {
        super(mainWindow, "单人游戏", true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
        setSize(1000, 700);
        setResizable(false);
    }
}
