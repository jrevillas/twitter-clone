package com.twitter.rmi.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by migui on 24/12/2016.
 */
public class DialogTweet extends JDialog {
    private JTextPane textArea;
    private String result;
    GUI gui;

    DialogTweet() {
        JPanel panelGeneral = new JPanel(new GridBagLayout());

        JLabel labelInstructions = new JLabel("Write your tweet");
        labelInstructions.setFont(new Font("Roboto", 0, 16));
        labelInstructions.setForeground(Color.decode("#232323"));

        JLabel labelCounter = new JLabel("140");
        labelCounter.setFont(labelInstructions.getFont());
        labelCounter.setForeground(labelInstructions.getForeground());
        labelCounter.setHorizontalAlignment(JLabel.RIGHT);

        textArea = new JTextPane();
        textArea.setFont(new Font("Roboto Light", 0, 14));
        textArea.setPreferredSize(new Dimension(300, 150));

        JButton buttonOK = new JButton("Send Tweet");
        buttonOK.setFont(labelInstructions.getFont());
        buttonOK.setForeground(labelInstructions.getForeground());
        buttonOK.setContentAreaFilled(false);
        buttonOK.addActionListener(e -> onOK());

        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.setFont(labelInstructions.getFont());
        buttonCancel.setForeground(labelInstructions.getForeground());
        buttonCancel.setContentAreaFilled(false);
        buttonCancel.addActionListener(e -> onCancel());

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                labelCounter.setText(String.valueOf(140 - textArea.getText().length()));
                if (labelCounter.getText().charAt(0) == '-') {
                    labelCounter.setForeground(Color.RED);
                    buttonOK.setEnabled(false);
                } else {
                    labelCounter.setForeground(Color.decode("#232323"));
                    buttonOK.setEnabled(true);
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panelGeneral.add(labelInstructions, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panelGeneral.add(labelCounter, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridwidth = 4;
        gbc.gridheight = 3;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        panelGeneral.add(textArea, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1;
        panelGeneral.add(buttonCancel, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        panelGeneral.add(buttonOK, gbc);
        this.setContentPane(panelGeneral);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        panelGeneral.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        if (textArea.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "You must write something", "Empty Tweet",
                    JOptionPane.WARNING_MESSAGE);
        else if (JOptionPane.showConfirmDialog(this, "Are you sure you want to write this?",
                "Confirm?", JOptionPane.YES_NO_OPTION) == 0) {
            result = textArea.getText();
            this.setVisible(false);
            this.dispose();
            gui.continueNewTweet(textArea.getText());
        }
    }

    private void onCancel() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to exit",
                "Confirm?", JOptionPane.YES_NO_OPTION) == 0) {
            this.setVisible(false);
            this.dispose();
            gui.continueNewTweet("");
        }
    }

    String getResult(){
        return result;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
    }
}
