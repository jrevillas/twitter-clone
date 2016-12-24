package com.twitter.rmi.gui;

import com.twitter.rmi.gui.resources.GetImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;


class PanelHeader extends JPanel {
    private JButton buttonMain;
    private JButton buttonMessages;
    private JButton buttonTweet;
    private JButton buttonExit;
    private JButton buttonMinimize;

    private JPanel panelData;

    private Font theFont = new Font("Roboto", 0, 24);
    private Color theColor = Color.decode("#c9e0ff");
    private boolean login;

    PanelHeader() {
        this.setLayout(new GridLayout(1, 2));
        this.setBackground(Color.decode("#515151"));

        panelData = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelData.setOpaque(false);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panelButtons.setOpaque(false);

        buttonMain = new JButton();
        buttonMain.setFont(theFont);
        buttonMain.setForeground(theColor);
        buttonMain.setContentAreaFilled(false);
        buttonMain.setBorderPainted(false);
        buttonMain.setFocusPainted(false);
        buttonMain.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonMessages = new JButton("");
        buttonMessages.setFont(theFont);
        buttonMessages.setForeground(theColor);
        buttonMessages.setIcon(GetImage.getImage("message.png", 30,30));
        buttonMessages.setContentAreaFilled(false);
        buttonMessages.setBorderPainted(false);
        buttonMessages.setFocusPainted(false);
        buttonMessages.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonTweet = new JButton("");
        buttonTweet.setFont(theFont);
        buttonTweet.setForeground(theColor);
        buttonTweet.setIcon(buttonMessages.getIcon());
        buttonTweet.setContentAreaFilled(false);
        buttonTweet.setBorderPainted(false);
        buttonTweet.setFocusPainted(false);
        buttonTweet.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonExit = new JButton("X");
        buttonExit.setFont(theFont);
        buttonExit.setForeground(theColor);
        buttonExit.setContentAreaFilled(false);
        buttonExit.setBorderPainted(false);
        buttonExit.setFocusPainted(false);
        buttonExit.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonMinimize = new JButton("_");
        buttonMinimize.setFont(theFont);
        buttonMinimize.setForeground(theColor);
        buttonMinimize.setContentAreaFilled(false);
        buttonMinimize.setFocusPainted(false);
        buttonMinimize.setBorder(new EmptyBorder(0, 5, 0, 0));

        panelData.add(buttonMain);

        panelButtons.add(buttonMinimize);
        panelButtons.add(buttonExit);

        this.add(panelData, 0);
        this.add(panelButtons, 1);
    }

    PanelHeader setType(boolean login) {
        if (login) {
            buttonMain.setText("Login");
            this.login = true;
        } else {
            buttonMain.setText("Timeline");
            buttonMain.setIcon(GetImage.getImage("home.png", 30,30));
            panelData.add(buttonMain);
            panelData.add(buttonMessages);
            panelData.add(buttonTweet);
            this.login = false;
        }
        return this;
    }

    PanelHeader setLabel(String label) {
        buttonMain.setText(label);
        buttonTweet.setText("");
        buttonMessages.setText("");
        return this;
    }

    PanelHeader addFrameAction(ActionListener exit, ActionListener minimize) {
        buttonExit.addActionListener(exit);
        buttonMinimize.addActionListener(minimize);
        return this;
    }

    PanelHeader addAction(ActionListener home, ActionListener message, ActionListener tweet) {
        buttonMain.addActionListener(home);
        buttonMessages.addActionListener(message);
        buttonTweet.addActionListener(tweet);
        return this;
    }
}
