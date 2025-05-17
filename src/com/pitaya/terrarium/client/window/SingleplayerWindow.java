package com.pitaya.terrarium.client.window;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.GameLoader;
import com.pitaya.terrarium.client.Player;
import com.pitaya.terrarium.client.WorldData;
import com.pitaya.terrarium.game.World;
import com.pitaya.terrarium.game.entity.life.player.PlayerDifficulty;
import com.pitaya.terrarium.game.world.WorldDifficulty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Enumeration;

public class SingleplayerWindow extends JDialog {

    private static final Logger LOGGER = LogManager.getLogger(SingleplayerWindow.class);

    public SingleplayerWindow(MainWindow mainWindow) {
        super(mainWindow, "单人游戏", true);
        GameLoader gameLoader = Main.getClient().gameLoader;
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
                gameLoader.saveWorld(gameLoader.exportWorld(new World(wName.getText(), 10, difficulty)));
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
                gameLoader.savePlayer(gameLoader.exportPlayer(new Player(pName.getText(), difficulty)));
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
            playerJList = new JList<>(gameLoader.scanPlayers());
        } catch (IOException e) {
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
        playerButtonPanel.add(new JButton("删除该玩家"));
        playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(playerPanel, BorderLayout.WEST);

        JPanel worldPanel = new JPanel();
        worldPanel.setLayout(new BorderLayout());
        JList<WorldData> worldDataJList;
        try {
            worldDataJList = new JList<>(gameLoader.scanWorldData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        worldDataJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane worldScrollPane = new JScrollPane(worldDataJList);
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
        wjbutton.addActionListener(e -> {
            this.setVisible(false);
            Main.getClient().loadRenderer();
            Main.getClient().terrarium.importWorldData(worldDataJList.getSelectedValue().getData());
            Main.getClient().runTerrarium(finalPlayerJList.getSelectedValue());
        });
        worldButtonPanel.add(wjbutton);
        worldPanel.add(worldButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(worldPanel, BorderLayout.EAST);

        add(mainPanel);
        setSize(1000, 700);
        setResizable(false);
        JList<Player> finalPlayerJList1 = playerJList;
        playerJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList1, worldDataJList)));
        worldDataJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList1, worldDataJList)));
        wjbutton.setEnabled(shouldJoinWorld(playerJList, worldDataJList));
    }

    public boolean shouldJoinWorld(JList<Player> playerJList, JList<WorldData> worldDataJList) {
        return playerJList.getSelectedValue() != null && worldDataJList.getSelectedValue() != null;
    }
}
