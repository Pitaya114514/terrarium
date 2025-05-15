package com.pitaya.terrarium.client.window;

import com.pitaya.terrarium.Main;
import com.pitaya.terrarium.client.network.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectWindow extends JDialog {
    public ConnectWindow(Frame owner) {
        super(owner, "连接", true);
        setSize(500, 350);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label1 = new JLabel("地址");
        JTextField textField1 = new JFormattedTextField();
        textField1.setText("127.0.0.1");
        JLabel label2 = new JLabel("端口");
        JTextField textField2 = new JFormattedTextField();
        textField2.setText("25565");
        JButton button1 = new JButton("连接");
        button1.addActionListener(event -> {
            try {
                Main.getClient().connectToServer(new Server("fa", Integer.parseInt(textField2.getText()), InetAddress.getByName(textField1.getText())));
            } catch (UnknownHostException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, e, e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            }
        });
        button1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(button1);
        this.add(panel);
    }
}

