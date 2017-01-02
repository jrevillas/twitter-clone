package com.twitter.rmi.gui;

import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;
import com.twitter.rmi.gui.resources.GetImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

class PanelUsers extends JPanel {

    private JPanel panelProfile;
    private JPanel panelNewPeople;
    private JButton buttonFindUsers;
    private JButton buttonUser;
    private JButton buttonQuitFind;

    private JComboBox<String> comboUsers;

    private GUI gui;
    private User activeUser;
    private GenericDomainTableModel<User> modelUsers;
    private List<User> users;
    private JTextPane textBio;
    private JLabel labelAvatar;
    private JLabel labelHandle;

    PanelUsers setActiveUser(GUI gui, User activeUser) throws RemoteException {
        this.gui = gui;
        this.activeUser = activeUser;
        this.setLayout(new BorderLayout());

        modelUsers = new GenericDomainTableModel<User>(Arrays.asList(new String[]{"avatar", "textPane"})) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return ImageIcon.class;
                else if (columnIndex == 1)
                    return JTextPane.class;
                else
                    return null;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                User user = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
//                      return GetImage.getImage(user.getAvatar(), 50, 50); TODO
                        return GetImage.getImage("avatar1.png", 50, 50);
                    case 1:
                        try {
                            return "<html>" +
//                                  "<font color=#53535353 face=\"Roboto Light\" size=4>" + user.getName() + "</font><br>" +
                                    "<font color=#535353 face=\"Roboto Light\" size=5>@" + user.getHandle() +
                                    "</font>";
                        } catch (RemoteException e) {
                            return "ERROR";
                        }
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        JTable tableUsers = new JTable(modelUsers);
        tableUsers.getColumn("avatar").setMaxWidth(65);
        tableUsers.getColumn("avatar").setMinWidth(65);
        tableUsers.setRowHeight(65);
        tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableUsers.getTableHeader().setUI(null);
        tableUsers.setShowVerticalLines(false);
        tableUsers.setFocusable(false);
        tableUsers.setSelectionBackground(tableUsers.getBackground());
        tableUsers.setSelectionForeground(tableUsers.getForeground());
        JScrollPane scrollPaneUsers = new JScrollPane(tableUsers);

        tableUsers.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                try {
                    String handle = modelUsers.getDomainObject(tableUsers.rowAtPoint(mouseEvent.getPoint())).getHandle();
                    gui.changeUser(handle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        initializeProfile();

        comboUsers = comboUsers();

        // -----------------------------------------------------
        // PANEL NEW PEOPLE

        JPanel panelNewPeople = new JPanel();
        panelNewPeople.setBackground(Color.decode("#C9E0FF"));
        buttonFindUsers = new JButton("Find New People");
        buttonFindUsers.setBackground(null);
        buttonFindUsers.setFont(new Font("Roboto Light", 0, 20));
        buttonFindUsers.setForeground(Color.decode("#535353"));
        buttonFindUsers.setBorder(new EmptyBorder(10, 5, 10, 5));
        buttonFindUsers.addActionListener(e -> {
            buttonFindUsers.setVisible(false);
            buttonQuitFind.setVisible(true);
            comboUsers.setVisible(true);
        });
        buttonQuitFind = new JButton("X");
        buttonQuitFind.setVisible(false);
        buttonQuitFind.setFont(new Font("Roboto Light", 0, 30));
        buttonQuitFind.setMargin(new Insets(1, 0, 0, 0));
        buttonQuitFind.setForeground(Color.decode("#535353"));
        buttonQuitFind.addActionListener(e -> {
            comboUsers.setVisible(false);
            buttonFindUsers.setVisible(true);
            buttonQuitFind.setVisible(false);
        });

        panelNewPeople.add(buttonFindUsers);
        panelNewPeople.add(comboUsers);
        panelNewPeople.add(buttonQuitFind);

        // -----------------------------------------------------
        // BUILD PANEL

        this.add(scrollPaneUsers, BorderLayout.CENTER);
        this.add(panelProfile, BorderLayout.NORTH);
        this.add(panelNewPeople, BorderLayout.SOUTH);
        return this;
    }

    private JComboBox<String> comboUsers() {

        JComboBox<String> comboNewPeople = new JComboBox<>();
        comboNewPeople.setFont(new Font("Roboto Light", 0, 14));
        comboNewPeople.setPreferredSize(new Dimension(200, 45));
        comboNewPeople.setMinimumSize(new Dimension(200, 45));
        comboNewPeople.setVisible(false);
        comboNewPeople.setEditable(true);
        try {
            users = activeUser.getUsers();
            for (User user : users)
                if (!user.getHandle().equals(activeUser.getHandle()))
                    comboNewPeople.addItem(user.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        JTextField textNewPeople = ((JTextField) comboNewPeople.getEditor().getEditorComponent());

        textNewPeople.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String selected = comboNewPeople.getItemAt(0);
                        if (selected == null)
                            JOptionPane.showMessageDialog(null, "Sorry could not find that user",
                                    "No user found", JOptionPane.ERROR_MESSAGE);
                        else {
                            gui.changeUser(selected);
                            comboNewPeople.setVisible(false);
                            buttonQuitFind.setVisible(false);
                            buttonFindUsers.setVisible(true);
                        }
                    } else {
                        String string = textNewPeople.getText();
                        comboNewPeople.removeAllItems();
                        for (User user : users) {
                            if (!user.getHandle().equals(activeUser.getHandle()) &&
                                    user.getHandle().contains(string)) // || user.getName().contains(string)) TODO
                                comboNewPeople.addItem(user.getHandle()); // + " " + user.getName());
                        }
                        textNewPeople.setText(string);
                        comboNewPeople.showPopup();

                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        textNewPeople.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                textNewPeople.setText("");
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                textNewPeople.setText("Press enter when finish");
            }
        });
        textNewPeople.setText("Press enter when finish");
        return comboNewPeople;
    }

    private void initializeProfile() {
        try {
            modelUsers.addRows(activeUser.getUserFollowing());
            panelProfile = new JPanel(new GridBagLayout());
            panelProfile.setBackground(Color.decode("#232323"));

            labelAvatar = new JLabel(GetImage.getImage("avatar1.png", 60, 60));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0;
            gbc.gridheight = 2;
            panelProfile.add(labelAvatar, gbc);

            buttonUser = new JButton();
            buttonUser.setText("Settings");
            buttonUser.setForeground(Color.white);
            buttonUser.setBackground(Color.decode("#c9e0ff"));
            buttonUser.setMaximumSize(new Dimension(85, 30));
            buttonUser.setMinimumSize(new Dimension(85, 30));
            buttonUser.addActionListener(e -> gui.openSettings());
            buttonUser.setMargin(new Insets(3, 5, 3, 5));
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.weightx = 0;
            panelProfile.add(buttonUser, gbc);

            labelHandle = new JLabel("@"+activeUser.getHandle());
            labelHandle.setFont(new Font("Roboto", 0, 16));
            labelHandle.setForeground(Color.decode("#c9e0ff"));
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            panelProfile.add(labelHandle, gbc);

            textBio = new JTextPane();
            textBio.setOpaque(false);
            textBio.setForeground(Color.white);
            textBio.setFont(new Font("Roboto", 0, 12));
            textBio.setText(activeUser.getBio());
            gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.gridheight = 2;
            panelProfile.add(textBio, gbc);
        } catch (RemoteException ignored) {
        }
    }

    void changeUser(String handle)  {
        modelUsers.clearTableModelData();
        try {
            if (handle.length()==0) {
                labelHandle.setText("@" + activeUser.getHandle());
                textBio.setText(activeUser.getBio());
                buttonUser.setText("Settings");
                buttonUser.removeActionListener(buttonUser.getActionListeners()[0]);
                buttonUser.addActionListener(e -> gui.openSettings());
                modelUsers.addRows(activeUser.getUserFollowing());
            } else {
                labelHandle.setText("@" + handle);
                for (User user : activeUser.getUsers())
                    if (user.getHandle().equals(handle))
                        textBio.setText(user.getBio());
                buttonUser.setText((activeUser.isFollowing(handle)) ? "UnFollow" : "Follow");
                buttonUser.removeActionListener(buttonUser.getActionListeners()[0]);
                buttonUser.addActionListener(e -> follow(handle));
                modelUsers.addRows(activeUser.getFollowing(handle));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void follow(String handle) {
        try {
            if (activeUser.isFollowing(handle)) {
                activeUser.unfollow(handle);
                buttonUser.setText("Follow");
            } else {
                activeUser.follow(handle);
                buttonUser.setText("UnFollow");
            }
        } catch (RemoteException ignored) {}
    }

}
