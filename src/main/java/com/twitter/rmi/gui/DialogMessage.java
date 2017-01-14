package com.twitter.rmi.gui;

import com.twitter.rmi.common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

/**
 * Created by migui on 6/01/17.
 */
class DialogMessage extends JDialog {
    private JTextPane textArea;
    private JComboBox<String> comboUsers;
    private java.util.List<User> users;
    private String content;
    private String receiver;
    private JTextField textNewPeople;

    DialogMessage setActiveUser(User activeUser) {
        JPanel panelGeneral = new JPanel(new GridBagLayout());

        JLabel labelReceiver = new JLabel("Select the receiver");
        labelReceiver.setFont(new Font("Roboto", 0, 16));
        labelReceiver.setForeground(Color.decode("#232323"));

        comboUsers = comboUsers(activeUser);

        JLabel labelInstructions = new JLabel("Write your Message");
        labelInstructions.setFont(new Font("Roboto", 0, 16));
        labelInstructions.setForeground(Color.decode("#232323"));

        JLabel labelCounter = new JLabel("140");
        labelCounter.setFont(labelInstructions.getFont());
        labelCounter.setForeground(labelInstructions.getForeground());
        labelCounter.setHorizontalAlignment(JLabel.RIGHT);

        textArea = new JTextPane();
        textArea.setFont(new Font("Roboto Light", 0, 14));
        textArea.setPreferredSize(new Dimension(300, 150));

        JButton buttonOK = new JButton("Send Message");
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
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panelGeneral.add(comboUsers, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        panelGeneral.add(labelInstructions, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        panelGeneral.add(labelCounter, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridwidth = 4;
        gbc.gridheight = 3;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1;
        panelGeneral.add(textArea, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 5;
        panelGeneral.add(buttonCancel, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 5;
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

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setUndecorated(true);
        this.pack();
        this.setModal(true);
        return this;
    }

    DialogMessage setDestinatary(String handle) {
        textNewPeople.setText(handle);
        return this;
    }

    private JComboBox<String> comboUsers(User activeUser) {
        JComboBox<String> comboNewPeople = new JComboBox<>();
        comboNewPeople.setFont(new Font("Roboto Light", 0, 12));
        comboNewPeople.setPreferredSize(new Dimension(190, 30));
        comboNewPeople.setMinimumSize(new Dimension(190, 30));
        comboNewPeople.setEditable(true);
        try {
            users = activeUser.getUsers();
            for (User user : users)
                comboNewPeople.addItem(user.getHandle());
        } catch (RemoteException ignored) {
        }
        textNewPeople = ((JTextField) comboNewPeople.getEditor().getEditorComponent());

        textNewPeople.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    String string = textNewPeople.getText();
                    comboNewPeople.removeAllItems();
                    for (User user : users)
                        if (user.getHandle().contains(string))
                            comboNewPeople.addItem(user.getHandle());

                    textNewPeople.setText(string);
                    comboNewPeople.showPopup();
                } catch (RemoteException ignored) {
                }
            }
        });
        textNewPeople.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (textNewPeople.getText().equals("Select Destinatary"))
                    textNewPeople.setText("");
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                if (textNewPeople.getText().length() == 0)
                    textNewPeople.setText("Select Destinatary");
            }
        });
        textNewPeople.setText("Select Destinatary");
        return comboNewPeople;
    }

    private void onOK() {
        if (textArea.getText().length() == 0 || textArea.getText().equals("Select Destinatary"))
            JOptionPane.showMessageDialog(this, "You must write something", "Empty Message",
                    JOptionPane.WARNING_MESSAGE);
        else {
            receiver = (String) comboUsers.getSelectedItem();
            content = textArea.getText();
            this.setVisible(false);
            this.dispose();
        }
    }

    private void onCancel() {
        this.setVisible(false);
        this.dispose();
    }

    String getReceiver() {
        String res = receiver;
        receiver = null;
        return res;
    }

    String getContent() {
        String res = content;
        content = null;
        return res;
    }
}