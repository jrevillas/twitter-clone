package com.twitter.rmi.gui;

import com.twitter.rmi.gui.resources.GetImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class PanelHeader extends JPanel {
    private JButton buttonMain;
    private JButton buttonMessages;
    private JButton buttonTweet;
    private JButton buttonExit;
    private JButton buttonMinimize;

    private JPanel panelData;
    private JPanel panelButtons;

    private Font theFont = new Font("Roboto", 0, 24);
    private Color theColor = Color.decode("#c9e0ff");

    private Point initialClick;

    PanelHeader() {
        this.setLayout(new GridLayout(1, 2));
        this.setBackground(Color.decode("#515151"));

        panelData = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panelData.setOpaque(false);

        panelButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
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
        buttonMessages.setIcon(GetImage.getImage("message.png", 30, 30));
        buttonMessages.setContentAreaFilled(false);
        buttonMessages.setBorderPainted(false);
        buttonMessages.setFocusPainted(false);
        buttonMessages.setBorder(new EmptyBorder(0, 5, 0, 0));

        buttonTweet = new JButton("");
        buttonTweet.setFont(theFont);
        buttonTweet.setForeground(theColor);
        buttonTweet.setIcon(GetImage.getImage("tweet.png", 25, 25));
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

    PanelHeader setGUI(GUI gui) {
        buttonExit.addActionListener(e -> gui.exit());
        buttonMinimize.addActionListener(e -> gui.frame.setState(Frame.ICONIFIED));

        buttonMain.addActionListener(e -> gui.changeUser(""));
        buttonMessages.addActionListener(e -> System.out.println("TODO"));
        buttonTweet.addActionListener(e -> gui.writeNewTweet());

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

    PanelHeader setType(boolean login) {
        if (login) {
            buttonMain.setText("Login");
        } else {
            buttonMain.setText("Timeline");
            buttonMain.setIcon(GetImage.getImage("home.png", 25, 25));
            panelData.add(buttonMain);
            panelData.add(buttonMessages);
            panelData.add(buttonTweet);
        }
        return this;
    }

    PanelHeader setLabel(String label) {
        buttonMain.setText(label);
        return this;
    }
}
