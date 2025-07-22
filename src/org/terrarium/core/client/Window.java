//package org.terrarium.core.client;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.terrarium.Main;
//import org.terrarium.core.client.network.Server;
//import org.terrarium.core.config.ConfigGroup;
//import org.terrarium.core.config.ConfigManager;
//import org.terrarium.core.config.ConfigObject;
//import org.terrarium.core.game.World;
//import org.terrarium.core.game.entity.player.PlayerDifficulty;
//import org.terrarium.core.game.network.CommunicationException;
//import org.terrarium.core.game.world.WorldDifficulty;
//import org.terrarium.core.game.world.WorldGenerator;
//import org.terrarium.core.game.world.WorldInfo;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//public class Window extends JFrame {
//    private static final Logger LOGGER = LogManager.getLogger(Window.class);
//
//    private static class ConfigComponent extends JPanel {
//
//        public ConfigComponent(ConfigObject configObject) {
//            this.setLayout(new FlowLayout());
//            this.add(new JLabel(configObject.getName()));
//
//            final String value = configObject.getValue();
//            switch (configObject.valueType) {
//                case INTEGER -> this.add(new JTextField(String.valueOf(value)));
//                case BOOLEAN -> this.add(new JCheckBox("", Boolean.parseBoolean(value)));
//                case STRING -> this.add(new JTextField(value));
//            }
//        }
//    }
//
//    private JDialog optionWindow;
//    private JDialog singleplayerWindow;
//    private JDialog multiplayerWindow;
//
//    public Window() {
//        super("Terrarium");
//        JPanel mainPanel = new JPanel();
//        JMenuBar menuBar = getBar();
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocation(20, 20);
//        setSize(450, 300);
//        setResizable(false);
//        setJMenuBar(menuBar);
//        add(mainPanel);
//    }
//
//    private JMenuBar getBar() {
//        JMenuBar menuBar = new JMenuBar();
//        JMenu menu1 = new JMenu("游戏");
//        JMenu menu2 = new JMenu("设置");
//        JMenuItem menuItem1 = new JMenuItem("单人游戏");
//        menuItem1.addActionListener(e -> {
//            if (singleplayerWindow == null || !singleplayerWindow.isVisible()) {
//                singleplayerWindow = singleplayerWindow(this);
//                singleplayerWindow.setVisible(true);
//            }
//        });
//        JMenuItem menuItem2 = new JMenuItem("多人游戏");
//        menuItem2.addActionListener(e -> {
//            if (multiplayerWindow == null || !multiplayerWindow.isVisible()) {
//                multiplayerWindow = multiplayerWindow(this);
//                multiplayerWindow.setVisible(true);
//            }
//        });
//        JMenuItem menuItem3 = new JMenuItem("设置");
//        menuItem3.addActionListener(e -> {
//            if (optionWindow == null || !optionWindow.isVisible()) {
//                optionWindow = optionWindow(this, Main.getClient().configManager);
//                optionWindow.setVisible(true);
//            }
//        });
//        menu1.add(menuItem1);
//        menu1.add(menuItem2);
//        menu2.add(menuItem3);
//        menuBar.add(menu1);
//        menuBar.add(menu2);
//        return menuBar;
//    }
//
//    private static JDialog optionWindow(JFrame parent, ConfigManager configManager) {
//        JDialog settingWindow = new JDialog(parent);
//        JPanel mainPanel = new JPanel();
//        JPanel settingPanel = new JPanel();
//        JPanel settingMainPanel = new JPanel();
//        JPanel settingConfirmPanel = new JPanel();
//        ConfigGroup[] configGroups = configManager.getConfigGroups();
//        Map<ConfigGroup, JScrollPane> configMap = new HashMap<>();
//        JList<ConfigGroup> groupList = new JList<>(configGroups);
//        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane groupListScrollPane = new JScrollPane(groupList);
//
//        mainPanel.setLayout(new GridLayout(0, 2, 5, 0));
//        mainPanel.add(groupListScrollPane);
//        settingPanel.setLayout(new BorderLayout());
//        settingMainPanel.setLayout(new OverlayLayout(settingMainPanel));
//        for (ConfigGroup configGroup : configGroups) {
//            JPanel configPanel = new JPanel();
//            configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS));
//            for (ConfigObject configObject : configGroup.getConfigObjects()) {
//                if (configObject.getValue() != null) {
//                    configPanel.add(new ConfigComponent(configObject));
//                }
//            }
//            JScrollPane configScroll = new JScrollPane(configPanel);
//            configScroll.setVisible(false);
//            settingMainPanel.add(configScroll);
//            configMap.put(configGroup, configScroll);
//        }
//        groupList.addListSelectionListener(listSelectionEvent -> {
//            for (Map.Entry<ConfigGroup, JScrollPane> entry : configMap.entrySet()) {
//                entry.getValue().setVisible(false);
//            }
//            for (Map.Entry<ConfigGroup, JScrollPane> entry : configMap.entrySet()) {
//                ConfigGroup configGroup = entry.getKey();
//                JScrollPane configScroll = entry.getValue();
//                if (groupList.getSelectedValue() == configGroup) {
//                    configScroll.setVisible(true);
//                }
//            }
//        });
//        settingPanel.add(settingMainPanel);
//        settingConfirmPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        Button apply = new Button();
//        apply.setLabel("应用");
//        settingConfirmPanel.add(apply);
//        Button cancel = new Button();
//        cancel.setLabel("取消");
//        settingConfirmPanel.add(cancel);
//        Button confirm = new Button();
//        confirm.setLabel("确定");
//        settingConfirmPanel.add(confirm);
//        settingPanel.add(settingConfirmPanel, BorderLayout.SOUTH);
//        mainPanel.add(settingPanel);
//        settingWindow.setTitle(configManager.getDescription());
//        settingWindow.setSize(1000, 800);
//        settingWindow.setResizable(false);
//        settingWindow.add(mainPanel);
//
//        return settingWindow;
//    }
//
//    private static JDialog singleplayerWindow(JFrame parent) {
//        JDialog singleplayerWindow = new JDialog(parent, "单人游戏", true);
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new GridLayout(0, 3, 0, 5));
//
//        JPanel worldGenerationPanel = new JPanel();
//        worldGenerationPanel.setLayout(new BoxLayout(worldGenerationPanel, BoxLayout.Y_AXIS));
//        worldGenerationPanel.add(new JLabel("新世界"));
//        JTextField wName = new JTextField("Name");
//        worldGenerationPanel.add(wName);
//        ButtonGroup wDifficultyButtons = new ButtonGroup();
//        WorldDifficulty[] worldDifficulties = WorldDifficulty.values();
//        for (int i = 0; i < worldDifficulties.length; i++) {
//            JCheckBox wDifficulty = i == 0 ? new JCheckBox(worldDifficulties[i].name(), true) : new JCheckBox(worldDifficulties[i].name());
//            wDifficultyButtons.add(wDifficulty);
//            worldGenerationPanel.add(wDifficulty);
//        }
//        ButtonGroup wGeneratorButtons = new ButtonGroup();
//        WorldGenerator[] worldGenerators = Main.getClient().gameInitializer.getWorldGenerators();
//        for (int i = 0; i < worldGenerators.length; i++) {
//            JCheckBox wGenerator = i == 0 ? new JCheckBox(worldGenerators[i].toString(), true) : new JCheckBox(worldGenerators[i].toString());
//            wGenerator.add(wGenerator);
//            worldGenerationPanel.add(wGenerator);
//        }
//        JPanel wgButtonPanel = new JPanel(new FlowLayout());
//        JButton wrbutton = new JButton("返回");
//        wrbutton.addActionListener(e -> worldGenerationPanel.setVisible(false));
//        wgButtonPanel.add(wrbutton);
//        JButton wcButton = new JButton("创建");
//        wcButton.addActionListener(e -> {
//            try {
//                Enumeration<AbstractButton> buttons = wDifficultyButtons.getElements();
//                WorldDifficulty difficulty = null;
//                while (buttons.hasMoreElements()) {
//                    AbstractButton btn = buttons.nextElement();
//                    if (btn.isSelected()) {
//                        difficulty = WorldDifficulty.valueOf(btn.getText());
//                    }
//                }
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//        wgButtonPanel.add(wcButton);
//        worldGenerationPanel.add(wgButtonPanel);
//
//        JPanel playerGenerationPanel = new JPanel();
//        playerGenerationPanel.setLayout(new BoxLayout(playerGenerationPanel, BoxLayout.Y_AXIS));
//        playerGenerationPanel.add(new JLabel("新玩家"));
//        JTextField pName = new JTextField("Name");
//        playerGenerationPanel.add(pName);
//        ButtonGroup pDifficultyButtons = new ButtonGroup();
//        for (int i = 0; i < PlayerDifficulty.values().length; i++) {
//            JCheckBox pDifficulty = i == 0 ? new JCheckBox(PlayerDifficulty.values()[i].name(), true) : new JCheckBox(PlayerDifficulty.values()[i].name());
//            pDifficultyButtons.add(pDifficulty);
//            playerGenerationPanel.add(pDifficulty);
//        }
//        JPanel pgButtonPanel = new JPanel(new FlowLayout());
//        JButton prButton = new JButton("返回");
//        prButton.addActionListener(e -> playerGenerationPanel.setVisible(false));
//        pgButtonPanel.add(prButton);
//        JButton pcButton = new JButton("创建");
//        pcButton.addActionListener(e -> {
//            try {
//                Enumeration<AbstractButton> buttons = pDifficultyButtons.getElements();
//                PlayerDifficulty difficulty = null;
//                while (buttons.hasMoreElements()) {
//                    AbstractButton btn = buttons.nextElement();
//                    if (btn.isSelected()) {
//                        difficulty = PlayerDifficulty.valueOf(btn.getText());
//                    }
//                }
//                GameLoader.savePlayer(GameLoader.exportPlayer(new Player(pName.getText(), difficulty)));
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//        pgButtonPanel.add(pcButton);
//        playerGenerationPanel.add(pgButtonPanel);
//
//        JPanel generationPanel = new JPanel();
//        generationPanel.setLayout(new OverlayLayout(generationPanel));
//        playerGenerationPanel.setVisible(false);
//        worldGenerationPanel.setVisible(false);
//        generationPanel.add(playerGenerationPanel);
//        generationPanel.add(worldGenerationPanel);
//
//        JPanel playerPanel = new JPanel();
//        playerPanel.setLayout(new BorderLayout());
//        JList<Player> playerJList = new JList<>();
//        try {
//            playerJList = new JList<>(GameLoader.scanPlayers());
//        } catch (Exception e) {
//            LOGGER.error("Unable to read player data", e);
//        }
//        playerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane playerScrollPane = new JScrollPane(playerJList);
//        playerPanel.add(playerScrollPane);
//        JPanel playerButtonPanel = new JPanel();
//        playerButtonPanel.setLayout(new FlowLayout());
//        JButton pButton = new JButton("创建新玩家");
//        pButton.addActionListener(e -> {
//            playerGenerationPanel.setVisible(!playerGenerationPanel.isVisible());
//            if (worldGenerationPanel.isVisible()) {
//                worldGenerationPanel.setVisible(false);
//            }
//        });
//        playerButtonPanel.add(pButton);
//        JButton refreshPlayerButton = new JButton("刷新");
//        playerButtonPanel.add(refreshPlayerButton);
//        JButton delPlayerButton = new JButton("删除该玩家");
//        playerButtonPanel.add(delPlayerButton);
//        playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);
//
//        JPanel worldPanel = new JPanel();
//        worldPanel.setLayout(new BorderLayout());
//        JList<WorldInfo> worldJList = new JList<>();
//        try {
//            worldJList = new JList<>(GameLoader.scanWorlds());
//        } catch (IOException e) {
//            LOGGER.error("Unable to read world data", e);
//        }
//        worldJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane worldScrollPane = new JScrollPane(worldJList);
//        worldPanel.add(worldScrollPane);
//        JPanel worldButtonPanel = new JPanel();
//        worldButtonPanel.setLayout(new FlowLayout());
//        JButton wButton = new JButton("创建新世界");
//        wButton.addActionListener(e -> {
//            worldGenerationPanel.setVisible(!worldGenerationPanel.isVisible());
//            if (playerGenerationPanel.isVisible()) {
//                playerGenerationPanel.setVisible(false);
//            }
//        });
//        worldButtonPanel.add(wButton);
//        worldButtonPanel.add(new JButton("刷新"));
//        JButton wjbutton = new JButton("加入");
//        JList<Player> finalPlayerJList = playerJList;
//        JList<WorldInfo> finalWorldJList = worldJList;
//        wjbutton.addActionListener(e -> {
//            singleplayerWindow.setVisible(false);
//            CompletableFuture.runAsync(() -> Main.getClient().runLocalTerrarium(finalPlayerJList.getSelectedValue(), finalWorldJList.getSelectedValue(), Main.getClient().gameInitializer))
//                    .exceptionally(exception -> {
//                        String message = String.format("Unable to load world: %s", exception) ;
//                        LOGGER.error(message);
//                        JOptionPane.showMessageDialog(singleplayerWindow, message, "无法加载世界", JOptionPane.ERROR_MESSAGE);
//                        return null;
//                    });
//        });
//        worldButtonPanel.add(wjbutton);
//        worldPanel.add(worldButtonPanel, BorderLayout.SOUTH);
//
//        singleplayerWindow.add(mainPanel);
//        singleplayerWindow.setSize(1000, 700);
//        singleplayerWindow.setResizable(false);
//        playerJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList, finalWorldJList)));
//        worldJList.addListSelectionListener(e -> wjbutton.setEnabled(shouldJoinWorld(finalPlayerJList, finalWorldJList)));
//        wjbutton.setEnabled(shouldJoinWorld(playerJList, worldJList));
//
//        mainPanel.add(playerPanel);
//        mainPanel.add(generationPanel);
//        mainPanel.add(worldPanel);
//
//        return singleplayerWindow;
//    }
//
//    private static JDialog multiplayerWindow(JFrame parent) {
//        JDialog multiplayerWindow = new JDialog(parent, "多人游戏", true);
//        JPanel mainPanel = new JPanel();
//        mainPanel.setLayout(new GridLayout(0, 3, 0, 5));
//
//        JPanel serverGenerationPanel = new JPanel();
//        serverGenerationPanel.setLayout(new BoxLayout(serverGenerationPanel, BoxLayout.Y_AXIS));
//        serverGenerationPanel.add(new JLabel("新服务器"));
//        JTextField sName = new JTextField("Name");
//        serverGenerationPanel.add(sName);
//        JTextField sIP = new JTextField("IP");
//        serverGenerationPanel.add(sIP);
//        JTextField sPort = new JTextField("Port");
//        serverGenerationPanel.add(sPort);
//        JPanel sgButtonPanel = new JPanel(new FlowLayout());
//        JButton srButton = new JButton("返回");
//        srButton.addActionListener(e -> serverGenerationPanel.setVisible(false));
//        sgButtonPanel.add(srButton);
//        JButton scButton = new JButton("创建");
//        scButton.addActionListener(e -> {
//            try {
//                GameLoader.saveServer(GameLoader.exportServer(new Server(sName.getText(), InetAddress.getByName(sIP.getText()), Integer.parseInt(sPort.getText()))));
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(multiplayerWindow, ex, "无法创建服务器", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        sgButtonPanel.add(scButton);
//        serverGenerationPanel.add(sgButtonPanel);
//
//        JPanel playerGenerationPanel = new JPanel();
//        playerGenerationPanel.setLayout(new BoxLayout(playerGenerationPanel, BoxLayout.Y_AXIS));
//        playerGenerationPanel.add(new JLabel("新玩家"));
//        JTextField pName = new JTextField("Name");
//        playerGenerationPanel.add(pName);
//        ButtonGroup pDifficultyButtons = new ButtonGroup();
//        for (int i = 0; i < PlayerDifficulty.values().length; i++) {
//            JCheckBox pDifficulty = i == 0 ? new JCheckBox(PlayerDifficulty.values()[i].name(), true) : new JCheckBox(PlayerDifficulty.values()[i].name());
//            pDifficultyButtons.add(pDifficulty);
//            playerGenerationPanel.add(pDifficulty);
//        }
//        JPanel pgButtonPanel = new JPanel(new FlowLayout());
//        JButton prButton = new JButton("返回");
//        prButton.addActionListener(e -> playerGenerationPanel.setVisible(false));
//        pgButtonPanel.add(prButton);
//        JButton pcButton = new JButton("创建");
//        pcButton.addActionListener(e -> {
//            try {
//                Enumeration<AbstractButton> buttons = pDifficultyButtons.getElements();
//                PlayerDifficulty difficulty = null;
//                while (buttons.hasMoreElements()) {
//                    AbstractButton btn = buttons.nextElement();
//                    if (btn.isSelected()) {
//                        difficulty = PlayerDifficulty.valueOf(btn.getText());
//                    }
//                }
//                GameLoader.savePlayer(GameLoader.exportPlayer(new Player(pName.getText(), difficulty)));
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//        pgButtonPanel.add(pcButton);
//        playerGenerationPanel.add(pgButtonPanel);
//
//        JPanel generationPanel = new JPanel();
//        generationPanel.setLayout(new OverlayLayout(generationPanel));
//        playerGenerationPanel.setVisible(false);
//        serverGenerationPanel.setVisible(false);
//        generationPanel.add(playerGenerationPanel);
//        generationPanel.add(serverGenerationPanel);
//
//        JPanel playerPanel = new JPanel();
//        playerPanel.setLayout(new BorderLayout());
//        JList<Player> playerJList = new JList<>();
//        try {
//            playerJList = new JList<>(GameLoader.scanPlayers());
//        } catch (Exception e) {
//            LOGGER.error("Unable to read player data", e);
//        }
//        playerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane playerScrollPane = new JScrollPane(playerJList);
//        playerPanel.add(playerScrollPane);
//        JPanel playerButtonPanel = new JPanel();
//        playerButtonPanel.setLayout(new FlowLayout());
//        JButton pButton = new JButton("创建新玩家");
//        pButton.addActionListener(e -> {
//            playerGenerationPanel.setVisible(!playerGenerationPanel.isVisible());
//            if (serverGenerationPanel.isVisible()) {
//                serverGenerationPanel.setVisible(false);
//            }
//        });
//        playerButtonPanel.add(pButton);
//        JButton refreshPlayerButton = new JButton("刷新");
//        playerButtonPanel.add(refreshPlayerButton);
//        playerButtonPanel.add(new JButton("删除该玩家"));
//        playerPanel.add(playerButtonPanel, BorderLayout.SOUTH);
//
//        JPanel serverPanel = new JPanel();
//        serverPanel.setLayout(new BorderLayout());
//        JList<Server> serverJList = new JList<>();
//        try {
//            serverJList = new JList<>(GameLoader.scanServers());
//        } catch (Exception e) {
//            LOGGER.error("Unable to read server data", e);
//        }
//        serverJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane serverScrollPane = new JScrollPane(serverJList);
//        serverPanel.add(serverScrollPane);
//        JPanel serverButtonPanel = new JPanel();
//        serverButtonPanel.setLayout(new FlowLayout());
//        JButton sButton = new JButton("创建新服务器");
//        sButton.addActionListener(e -> {
//            serverGenerationPanel.setVisible(!serverGenerationPanel.isVisible());
//            if (playerGenerationPanel.isVisible()) {
//                playerGenerationPanel.setVisible(false);
//            }
//        });
//        serverButtonPanel.add(sButton);
//        serverButtonPanel.add(new JButton("刷新"));
//        JButton sjbutton = new JButton("连接");
//        JList<Server> finalServerJList1 = serverJList;
//        JList<Player> finalPlayerJList1 = playerJList;
//        sjbutton.addActionListener(e -> {
//            multiplayerWindow.setVisible(false);
//            CompletableFuture.runAsync(() -> {
//                try {
//                    Main.getClient().runRemoteTerrarium(finalPlayerJList1.getSelectedValue(), finalServerJList1.getSelectedValue());
//                } catch (CommunicationException ex) {
//                    String message = String.format("Unable to connect to the server: %s", ex) ;
//                    LOGGER.error(message);
//                    JOptionPane.showMessageDialog(multiplayerWindow, message, "无法连接至服务器", JOptionPane.ERROR_MESSAGE);
//                }
//            }).completeOnTimeout(null, 10, TimeUnit.SECONDS);
//        });
//        serverButtonPanel.add(sjbutton);
//        serverPanel.add(serverButtonPanel, BorderLayout.SOUTH);
//
//        multiplayerWindow.add(mainPanel);
//        multiplayerWindow.setSize(1000, 700);
//        multiplayerWindow.setResizable(false);
//        JList<Player> finalPlayerJList = playerJList;
//        JList<Server> finalServerJList = serverJList;
//        playerJList.addListSelectionListener(e -> sjbutton.setEnabled(shouldConnect(finalPlayerJList, finalServerJList)));
//        serverJList.addListSelectionListener(e -> sjbutton.setEnabled(shouldConnect(finalPlayerJList, finalServerJList)));
//        sjbutton.setEnabled(shouldConnect(playerJList, serverJList));
//
//        mainPanel.add(playerPanel);
//        mainPanel.add(generationPanel);
//        mainPanel.add(serverPanel);
//
//        return multiplayerWindow;
//    }
//
//    private static boolean shouldJoinWorld(JList<Player> playerJList, JList<WorldInfo> worldJList) {
//        return playerJList.getSelectedValue() != null && worldJList.getSelectedValue() != null;
//    }
//
//    private static boolean shouldConnect(JList<Player> playerJList, JList<Server> serverJList) {
//        return playerJList.getSelectedValue() != null && serverJList.getSelectedValue() != null;
//    }
//}
