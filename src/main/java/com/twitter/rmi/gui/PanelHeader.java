package com.twitter.rmi.gui;

import com.twitter.rmi.gui.GUI.VIEW;
import com.twitter.rmi.gui.GUI.FrameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class PanelHeader extends JPanel {

    private JButton buttonNewMessage;
    private JButton buttonMain;
    private JButton buttonMessages;
    private JButton buttonNewTweet;
    private JButton buttonExit;
    private JButton buttonMinimize;
    private JButton buttonMax;
    private JButton buttonRestore;

    private JPanel panelData;
    private JPanel panelButtons;

    private Point initialClick;
    private GUI gui;

    PanelHeader() {
        this.setLayout(new GridLayout(1, 2));
        this.setBackground(Color.decode("#515151"));

        panelData = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelData.setOpaque(false);

        panelButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        panelButtons.setOpaque(false);

        buttonMain = new JButton();
        Font theFont = new Font("Roboto", 0, 24);
        buttonMain.setFont(theFont);
        Color theColor = Color.decode("#c9e0ff");
        buttonMain.setForeground(theColor);
        buttonMain.setContentAreaFilled(false);
        buttonMain.setBorderPainted(false);
        buttonMain.setFocusPainted(false);
        buttonMain.setBorder(new EmptyBorder(0, 5, 0, 0));
        buttonMain.setIcon(Auxiliar.getImage("home.png", 30, 30));

        buttonMessages = new JButton("");
        buttonMessages.setFont(theFont);
        buttonMessages.setForeground(theColor);
        buttonMessages.setIcon(Auxiliar.getImage("inbox.png", 30, 30));
        buttonMessages.setContentAreaFilled(false);
        buttonMessages.setBorderPainted(false);
        buttonMessages.setFocusPainted(false);
        buttonMessages.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonNewTweet = new JButton("New Tweet");
        buttonNewTweet.setFont(theFont);
        buttonNewTweet.setForeground(theColor);
        buttonNewTweet.setIcon(Auxiliar.getImage("tweet.png", 25, 25));
        buttonNewTweet.setContentAreaFilled(false);
        buttonNewTweet.setBorderPainted(false);
        buttonNewTweet.setFocusPainted(false);
        buttonNewTweet.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonNewMessage = new JButton("New");
        buttonNewMessage.setFont(theFont);
        buttonNewMessage.setForeground(theColor);
        buttonNewMessage.setIcon(Auxiliar.getImage("message.png", 25, 25));
        buttonNewMessage.setContentAreaFilled(false);
        buttonNewMessage.setBorderPainted(false);
        buttonNewMessage.setFocusPainted(false);
        buttonNewMessage.setBorder(new EmptyBorder(0, 5, 0, 0));
        buttonNewMessage.setVisible(false);

        buttonExit = new JButton(Auxiliar.getImage("close.png", 25, 25));
        buttonExit.setFont(theFont);
        buttonExit.setForeground(theColor);
        buttonExit.setContentAreaFilled(false);
        buttonExit.setBorderPainted(false);
        buttonExit.setFocusPainted(false);
        buttonExit.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonMax = new JButton(Auxiliar.getImage("maximize.png", 25, 25));
        buttonMax.setContentAreaFilled(false);
        buttonMax.setBorderPainted(false);
        buttonMax.setFocusPainted(false);
        buttonMax.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonRestore = new JButton(Auxiliar.getImage("restore.png", 25, 25));
        buttonRestore.setContentAreaFilled(false);
        buttonRestore.setBorderPainted(false);
        buttonRestore.setFocusPainted(false);
        buttonRestore.setBorder(new EmptyBorder(0, 5, 0, 0));
        buttonRestore.setVisible(false);

        buttonMinimize = new JButton("_");
        buttonMinimize.setFont(theFont);
        buttonMinimize.setForeground(theColor);
        buttonMinimize.setContentAreaFilled(false);
        buttonMinimize.setFocusPainted(false);
        buttonMinimize.setBorder(new EmptyBorder(0, 5, 0, 0));

        panelData.add(buttonMain);
        panelData.add(buttonMessages);
        panelData.add(buttonNewTweet);
        panelData.add(buttonNewMessage);


        panelButtons.add(buttonMinimize);
//        panelButtons.add(buttonMax);
//        panelButtons.add(buttonRestore);
        panelButtons.add(buttonExit);

        this.add(panelData, 0);
        this.add(panelButtons, 1);
    }

    PanelHeader setGUI(GUI gui) {
        this.gui = gui;
        setActionButton();

        MouseAdapter mouseDragged = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                // get location of Window
                int thisX = gui.frame.getLocation().x;
                int thisY = gui.frame.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                gui.frame.setLocation(X, Y);
            }
        };


        MouseAdapter mousePressed = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        };

        panelButtons.addMouseMotionListener(mouseDragged);
        panelButtons.addMouseListener(mousePressed);

        panelData.addMouseMotionListener(mouseDragged);
        panelData.addMouseListener(mousePressed);

        return this;
    }

    private void setActionButton() {
        buttonExit.addActionListener(e -> gui.exit());
        buttonMinimize.addActionListener(e -> gui.setFrameState(FrameState.MINIMIZE));
        buttonMax.addActionListener(e -> {
            buttonMax.setVisible(false);
            buttonRestore.setVisible(true);
            gui.setFrameState(FrameState.MAXIMIZE);
        });
        buttonRestore.addActionListener(e -> {
            buttonRestore.setVisible(false);
            buttonMax.setVisible(true);
            gui.setFrameState(FrameState.RESTORE);
        });

        buttonMain.addActionListener(e -> gui.changePanel(VIEW.TWEETS));
        buttonMessages.addActionListener(e -> gui.changePanel(VIEW.MESSAGES));
        buttonNewTweet.addActionListener(e -> gui.writeNewTweet());
        buttonNewMessage.addActionListener(e -> gui.writeNewMessage());
    }

    PanelHeader setType(VIEW view) {
        switch (view) {
            case LOGIN:
                System.out.println("login");
                buttonMain.setText("Login");
                buttonMain.setIcon(null);
                buttonMax.setVisible(false);
                buttonMessages.setVisible(false);
                buttonNewTweet.setVisible(false);
                break;
            case TWEETS:
                buttonMain.setText("Timeline");
                buttonMessages.setText("");
                buttonNewMessage.setVisible(false);
                buttonNewTweet.setVisible(true);
                break;
            case MESSAGES:
                buttonMain.setText("");
                buttonNewMessage.setVisible(true);
                buttonNewTweet.setVisible(false);

                buttonMessages.setText("Messages");

                break;
        }
        return this;
    }

    PanelHeader setLabel(String label) {
        buttonMain.setText(label);
        return this;
    }
}
