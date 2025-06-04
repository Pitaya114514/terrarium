package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.GameLoader;
import com.pitaya.terrarium.client.Player;
import com.pitaya.terrarium.client.network.Server;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.network.CommunicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MultiplayerWindow extends JDialog {
    private static final Logger LOGGER = LogManager.getLogger(MultiplayerWindow.class);

    public MultiplayerWindow(MainWindow mainWindow) {
        super(mainWindow, "多人游戏", true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel serverGenerationPanel = new JPanel();
        serverGenerationPanel.setLayout(new BoxLayout(serverGenerationPanel, BoxLayout.Y_AXIS));
        serverGenerationPanel.add(new JLabel("新服务器"));
        JTextField sName = new JTextField("Name");
        serverGenerationPanel.add(sName);
        JTextField sIP = new JTextField("IP");
        serverGenerationPanel.add(sIP);
        JTextField sPort = new JTextField("Port");
        serverGenerationPanel.add(sPort);
        JPanel sgButtonPanel = new JPanel(new FlowLayout());
        JButton srButton = new JButton("返回");
        srButton.addActionListener(e -> serverGenerationPanel.setVisible(false));
        sgButtonPanel.add(srButton);
        JButton scButton = new JButton("创建");
        scButton.addActionListener(e -> {
            try {
                GameLoader.saveServer(GameLoader.exportServer(new Server(sName.getText(), InetAddress.getByName(sIP.getText()), Integer.parseInt(sPort.getText()))));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex, "无法创建服务器", JOptionPane.ERROR_MESSAGE);
            }
        });
        sgButtonPanel.add(scButton);
        serverGenerationPanel.add(sgButtonPanel);

        JPanel playerGenerationPanel = new JPanel();
        playerGenerationPanel.setLayout(new BoxLayout(playerGenerationPanel, BoxLayout.Y_AXIS));
        playerGenerationPanel.add(new JLabel("新玩家"));
        JTextField pName = new JTextField("Name");
        playerGenerationPanel.add(pName);
        ButtonGroup pDifficultyButtons = new ButtonGroup();
        for (int i = 0; i < PlayerDifficulty.values().length; i++) {
            JCheckBox pDifficulty = i == 0 ? new JCheckBox(PlayerDifficulty.values()[i].name(), true) : new JCheckBox(PlayerDifficulty.values()[i].name());
            pDifficultyButtons.add(pDifficulty);
            playerGenerationPanel.add(pDifficulty);
        }
        JPanel pgButtonPanel = new JPanel(new FlowLayout());
        JButton prButton = new JButton("返回");
        prButton.addActionListener(e -> playerGenerationPanel.setVisible(false));
        pgButtonPanel.add(prButton);
        JButton pcButton = new JButton("创建");
        pcButton.addActionListener(e -> {
            try {
                Enumeration<AbstractButton> buttons = pDifficultyButtons.getElements();
                PlayerDifficulty difficulty = null;
                while (buttons.hasMoreElements()) {
                    AbstractButton btn = buttons.nextElement();
                    if (btn.isSelected()) {
                        difficulty = PlayerDifficulty.valueOf(btn.getText());
                    }
                }
                GameLoader.savePlayer(GameLoader.exportPlayer(new Player(pName.getText(), difficulty)));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        pgButtonPanel.add(pcButton);
        playerGenerationPanel.add(pgButtonPanel);

        JPanel generationPanel = new JPanel();
        generationPanel.setLayout(new OverlayLayout(generationPanel));
        playerGenerationPanel.setVisible(false);
        serverGenerationPanel.setVisible(false);
        generationPanel.add(playerGenerationPanel);
        generationPanel.add(serverGenerationPanel);
        mainPanel.add(generationPanel, BorderLayout.CENTER);

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        JList<Player> playerJList = new JList<>();
        try {
            playerJList = new JList<>(GameLoader.scanPlayers());
        } catch (Exception e) {
            LOGGER.error("Unable to read player data", e);
        }
        playerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane playerScrollPane = new JScrollPane(playerJList);
        playerPanel.add(playerScrollPane);
        JPanel playerButtonPanel = new JPanel();
        playerButtonPanel.setLayout(new FlowLayout());
        JButton pButton = new JButton("创建新玩家");
        pButton.addActionListener(e -> {
            playerGenerationPanel.setVisible(!playerGenerationPanel.isVisible());
            if (serverGenerationPanel.isVisible()) {
                serverGenerationPanel.setVisible(false);
            }
        });
        playerButtonPanel.add(pButton);
        JButton refreshPlayerButton = new JButton("刷新");
        playerButtonPanel.add(refreshPlayerButton);
        playerButtonPanel.add(new JButton("删除该玩家"));
        playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(playerPanel, BorderLayout.WEST);

        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new BorderLayout());
        JList<Server> serverJList = new JList<>();
        try {
            serverJList = new JList<>(GameLoader.scanServers());
        } catch (Exception e) {
            LOGGER.error("Unable to read server data", e);
        }
        serverJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane serverScrollPane = new JScrollPane(serverJList);
        serverPanel.add(serverScrollPane);
        JPanel serverButtonPanel = new JPanel();
        serverButtonPanel.setLayout(new FlowLayout());
        JButton sButton = new JButton("创建新服务器");
        sButton.addActionListener(e -> {
            serverGenerationPanel.setVisible(!serverGenerationPanel.isVisible());
            if (playerGenerationPanel.isVisible()) {
                playerGenerationPanel.setVisible(false);
            }
        });
        serverButtonPanel.add(sButton);
        serverButtonPanel.add(new JButton("刷新"));
        serverButtonPanel.add(new JButton("删除该服务器"));
        JButton sjbutton = new JButton("连接");
        JList<Server> finalServerJList1 = serverJList;
        JList<Player> finalPlayerJList1 = playerJList;
        sjbutton.addActionListener(e -> {
            this.setVisible(false);
            CompletableFuture.runAsync(() -> {
                try {
                    Main.getClient().runRemoteTerrarium(finalPlayerJList1.getSelectedValue(), finalServerJList1.getSelectedValue());
                } catch (CommunicationException ex) {
                    String message = String.format("Unable to connect to the server: %s", ex) ;
                    LOGGER.error(message);
                    JOptionPane.showMessageDialog(this, message, "无法连接至服务器", JOptionPane.ERROR_MESSAGE);
                }
            }).completeOnTimeout(null, 10, TimeUnit.SECONDS);
        });
        serverButtonPanel.add(sjbutton);
        serverPanel.add(serverButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(serverPanel, BorderLayout.EAST);

        add(mainPanel);
        setSize(1000, 700);
        setResizable(false);
        JList<Player> finalPlayerJList = playerJList;
        JList<Server> finalServerJList = serverJList;
        playerJList.addListSelectionListener(e -> sjbutton.setEnabled(shouldConnect(finalPlayerJList, finalServerJList)));
        serverJList.addListSelectionListener(e -> sjbutton.setEnabled(shouldConnect(finalPlayerJList, finalServerJList)));
        sjbutton.setEnabled(shouldConnect(playerJList, serverJList));
    }

    public boolean shouldConnect(JList<Player> playerJList, JList<Server> serverJList) {
        return playerJList.getSelectedValue() != null && serverJList.getSelectedValue() != null;
    }
}
