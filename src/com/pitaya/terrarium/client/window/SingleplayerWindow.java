package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.GameLoader;
import com.pitaya.terrarium.client.Player;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.world.WorldDifficulty;
import com.pitaya.terrarium.game.world.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;

public class SingleplayerWindow extends JDialog {

    private static final Logger LOGGER = LogManager.getLogger(SingleplayerWindow.class);

    public SingleplayerWindow(MainWindow mainWindow) {
        super(mainWindow, "单人游戏", true);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel worldGenerationPanel = new JPanel();
        worldGenerationPanel.setLayout(new BoxLayout(worldGenerationPanel, BoxLayout.Y_AXIS));
        worldGenerationPanel.add(new JLabel("新世界"));
        JTextField wName = new JTextField("Name");
        worldGenerationPanel.add(wName);
        ButtonGroup wDifficultyButtons = new ButtonGroup();
        for (int i = 0; i < PlayerDifficulty.values().length; i++) {
            JCheckBox pDifficulty = i == 0 ? new JCheckBox(WorldDifficulty.values()[i].name(), true) : new JCheckBox(WorldDifficulty.values()[i].name());
            wDifficultyButtons.add(pDifficulty);
            worldGenerationPanel.add(pDifficulty);
        }
        JPanel wgButtonPanel = new JPanel(new FlowLayout());
        JButton wrbutton = new JButton("返回");
        wrbutton.addActionListener(e -> worldGenerationPanel.setVisible(false));
        wgButtonPanel.add(wrbutton);
        JButton wcButton = new JButton("创建");
        wcButton.addActionListener(e -> {
            try {
                Enumeration<AbstractButton> buttons = wDifficultyButtons.getElements();
                WorldDifficulty difficulty = null;
                while (buttons.hasMoreElements()) {
                    AbstractButton btn = buttons.nextElement();
                    if (btn.isSelected()) {
                        difficulty = WorldDifficulty.valueOf(btn.getText());
                    }
                }
                GameLoader.saveWorld(GameLoader.exportWorld(new World(wName.getText(), 10, difficulty).getInfo()));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        wgButtonPanel.add(wcButton);
        worldGenerationPanel.add(wgButtonPanel);

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
        worldGenerationPanel.setVisible(false);
        generationPanel.add(playerGenerationPanel);
        generationPanel.add(worldGenerationPanel);
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
            if (worldGenerationPanel.isVisible()) {
                worldGenerationPanel.setVisible(false);
            }
        });
        playerButtonPanel.add(pButton);
        JButton refreshPlayerButton = new JButton("刷新");
        playerButtonPanel.add(refreshPlayerButton);
        JButton delPlayerButton = new JButton("删除该玩家");
        playerButtonPanel.add(delPlayerButton);
        playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(playerPanel, BorderLayout.WEST);

        JPanel worldPanel = new JPanel();
        worldPanel.setLayout(new BorderLayout());
        JList<WorldInfo> worldJList = new JList<>();
        try {
            worldJList = new JList<>(GameLoader.scanWorlds());
        } catch (IOException e) {
            LOGGER.error("Unable to read world data", e);
        }
        worldJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane worldScrollPane = new JScrollPane(worldJList);
        worldPanel.add(worldScrollPane);
        JPanel worldButtonPanel = new JPanel();
        worldButtonPanel.setLayout(new FlowLayout());
        JButton wButton = new JButton("创建新世界");
        wButton.addActionListener(e -> {
            worldGenerationPanel.setVisible(!worldGenerationPanel.isVisible());
            if (playerGenerationPanel.isVisible()) {
                playerGenerationPanel.setVisible(false);
            }
        });
        worldButtonPanel.add(wButton);
        worldButtonPanel.add(new JButton("刷新"));
        worldButtonPanel.add(new JButton("删除该世界"));
        JButton wjbutton = new JButton("加入该世界");
        JList<Player> finalPlayerJList = playerJList;
        JList<WorldInfo> finalWorldJList = worldJList;
        wjbutton.addActionListener(e -> {
            this.setVisible(false);
            CompletableFuture.runAsync(() -> Main.getClient().runLocalTerrarium(finalPlayerJList.getSelectedValue(), finalWorldJList.getSelectedValue()))
                    .exceptionally(exception -> {
                        String message = String.format("Unable to load world: %s", exception) ;
                        LOGGER.error(message);
                        JOptionPane.showMessageDialog(this, message, "无法加载世界", JOptionPane.ERROR_MESSAGE);
                        return null;
                    });
        });
        worldButtonPanel.add(wjbutton);
        worldPanel.add(worldButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(worldPanel, BorderLayout.EAST);

        add(mainPanel);
        setSize(1000, 700);
        setResizable(false);
        playerJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList, finalWorldJList)));
        worldJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList, finalWorldJList)));
        wjbutton.setEnabled(shouldJoinWorld(playerJList, worldJList));
    }

    public boolean shouldJoinWorld(JList<Player> playerJList, JList<WorldInfo> worldJList) {
        return playerJList.getSelectedValue() != null && worldJList.getSelectedValue() != null;
    }
}
