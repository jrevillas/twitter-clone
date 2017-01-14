package com.twitter.rmi.gui;

import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.GUI.VIEW;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class PanelLogin extends JPanel {
    private JButton buttonLogIn;
    private JButton buttonSignIn;
    private JTextField textHandle;
    private JPasswordField textPassword;
    private PanelHeader panelHeader;
    private JPanel panelCenter;

    PanelLogin() {
        this.setLayout(new BorderLayout());

        // HEADER!
        panelHeader = new PanelHeader().setType(VIEW.LOGIN);

        // CENTER
        panelCenter = new JPanel(new GridLayout(4, 1));
        panelCenter.setBorder(new EmptyBorder(5, 20, 15, 20));

        JLabel labelHandle = new JLabel("Handle");
        Font light = new Font("Roboto Light", 0, 18);
        labelHandle.setFont(light);
        labelHandle.setForeground(Color.decode("#232323"));

        textHandle = new JTextField();
        textHandle.setPreferredSize(new Dimension(240, 30));
        textHandle.setFont(light);

        JLabel labelPassword = new JLabel("Password");
        labelPassword.setFont(light);
        labelPassword.setForeground(Color.decode("#232323"));

        textPassword = new JPasswordField();
        textPassword.setFont(light);

        panelCenter.add(labelHandle, 0);
        panelCenter.add(textHandle, 1);
        panelCenter.add(labelPassword, 2);
        panelCenter.add(textPassword, 3);

        // FOOTER
        JPanel panelFooter = new JPanel(new GridLayout(1, 2));

        buttonSignIn = new JButton("Sign In");
        buttonSignIn.setContentAreaFilled(false);
        buttonSignIn.setFocusPainted(false);
        Font normal = new Font("Roboto", 0, 18);
        buttonSignIn.setFont(normal);
        buttonSignIn.setMargin(new Insets(5, 0, 5, 0));


        buttonLogIn = new JButton("Log In");
        buttonLogIn.setContentAreaFilled(false);
        buttonLogIn.setFocusPainted(false);
        buttonLogIn.setFont(normal);


        panelFooter.add(buttonSignIn, 0);
        panelFooter.add(buttonLogIn, 1);

        // BUILDER

        this.add(panelHeader, BorderLayout.NORTH);
        this.add(panelCenter, BorderLayout.CENTER);
        this.add(panelFooter, BorderLayout.SOUTH);
    }

    PanelLogin setGUI(GUI gui) {
        panelHeader.setGUI(gui);
        buttonSignIn.addActionListener(e -> {
            gui.showDialogLoading(true);
            User activeUser;
            String handle = textHandle.getText();
            String password = String.copyValueOf(textPassword.getPassword());
            if (handle.length() == 0 || password.length() == 0)
                JOptionPane.showMessageDialog(panelCenter, "You must enter a handle and a password",
                        "Error at Sign in!", JOptionPane.ERROR_MESSAGE);
            else
                try {
                    activeUser = gui.getTwitter().register(handle, password);
                    if (activeUser == null) {
                        gui.showDialogLoading(false);
                        JOptionPane.showMessageDialog(panelCenter, "Incorrect register, are you registered?",
                                "Error at sign in!", JOptionPane.ERROR_MESSAGE);
                    } else
                        gui.start(activeUser);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    gui.showDialogLoading(false);
                    JOptionPane.showMessageDialog(panelCenter, "No connection detected",
                            "Connection is down!", JOptionPane.ERROR_MESSAGE);
                }
        });
        buttonLogIn.addActionListener(e -> {
            gui.showDialogLoading(true);
            User activeUser;
            String handle = textHandle.getText();
            String password = String.copyValueOf(textPassword.getPassword());
            if (handle.length() == 0 || password.length() == 0)
                JOptionPane.showMessageDialog(panelCenter, "You must enter a handle and a password",
                        "Error at Logging in!", JOptionPane.ERROR_MESSAGE);
            else
                try {
                    activeUser = gui.getTwitter().login(handle, password);
                    if (activeUser == null) {
                        gui.showDialogLoading(false);
                        JOptionPane.showMessageDialog(panelCenter, "Incorrect register, are you already " +
                                "registered?", "Error at sign in!", JOptionPane.ERROR_MESSAGE);
                    } else
                        gui.start(activeUser);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    gui.showDialogLoading(false);
                    JOptionPane.showMessageDialog(panelCenter, "No connection detected",
                            "Connection is down!", JOptionPane.ERROR_MESSAGE);
                }
        });
        return this;
    }
}