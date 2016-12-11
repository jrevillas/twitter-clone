package com.twitter.rmi.gui;

import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;
import com.twitter.rmi.gui.resources.GetImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GUI {
    private JFrame frame;
    private JPanel panelGeneral;
    private JPanel panelTweets;
    private JPanel panelUsers;
    private JPanel panelProfile;
    private JPanel panelHeader;
    private JPanel panelNewPeople;
    private JSplitPane panelBody;
    private JLabel labelHeader;
    private JLabel labelHeaderIcon;
    private JScrollPane scrollPaneTweets;
    private JScrollPane scrollPaneUsers;
    private JTable tableTweets;
    private JTable tableUsers;
    private JButton buttonNewPeople;
    //    private JButton buttonExit; // TODO

    private GenericDomainTableModel<StatusGUI> modelTweets;
    private GenericDomainTableModel<UserGUI> modelUsers;

    private UserGUI activeUser = new UserGUI("avatar1.png", "Miguel Núñez", "@mnunezdm");
    private JComboBox<String> comboNewPeople;
    private java.util.List<UserGUI> users;

    /**
     * <B>FUNCTION:</B> main constructor of the interface
     */
    private GUI() {
        this.users = new ArrayList<>();
        this.panelGeneral = new JPanel();
        this.panelGeneral.setLayout(new BorderLayout());

        this.initializeHeader();

        this.initializeBody();

        // ------------------------------------------------------------------
        // FRAME CONFIGURATION

        this.frame = new JFrame();
        this.frame.setContentPane(panelGeneral);
        this.frame.setSize(new Dimension(800, 600));
        this.frame.setLocationRelativeTo(null);
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setTitle("twitter-rmi");
        // TODO
//        this.frame.setIconImage(GetImage.getImage("icon.png").getImage());
//        this.frame.setUndecorated(true);
        this.frame.setVisible(true);
        panelBody.setResizeWeight(1);
        panelBody.setDividerLocation(0.68);
    }

    /**
     * <B>FUNCTION:</B> initialize all the elements in the Header Panel
     */
    private void initializeHeader() {
        this.panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        this.panelHeader.setBackground(Color.decode("#515151"));

        this.labelHeaderIcon = new JLabel(GetImage.getImage("ButtonMenu.png"));
        this.labelHeaderIcon.setBorder(new EmptyBorder(5, 15, 2, 0));
        this.panelHeader.add(labelHeaderIcon);

        this.labelHeader = new JLabel("Timeline");
        this.labelHeader.setFont(new Font("Roboto", 0, 24));
        this.labelHeader.setBorder(new EmptyBorder(5, 15, 2, 0));
        this.labelHeader.setForeground(Color.decode("#c9e0ff"));
        panelHeader.add(labelHeader);
        // TODO
//        this.buttonExit = new JButton("X");
//        this.buttonExit.addActionListener(e -> System.exit(1));
//        this.buttonExit.setForeground(Color.decode("#c9e0ff"));
//        panelHeader.add(buttonExit);
        panelGeneral.add(this.panelHeader, BorderLayout.NORTH);
    }

    /**
     * <B>FUNCTION:</B> initialize all the elements in the Body Panel
     */
    private void initializeBody() {
        // -----------------------------------------------------
        // PANEL TWEETS
        panelTweets = new JPanel();
        modelTweets = new GenericDomainTableModel<StatusGUI>(Arrays.asList(new String[]{"avatar", "textPane"})) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return ImageIcon.class;
                    case 1:
                        return JTextPane.class;
                    default:
                        return null;
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                StatusGUI status = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return GetImage.getImage(status.getAvatar(), 72, 72);
                    case 1:
                        return "<html><font color=#53535353 face=\"Roboto Bold\" size=5>" + status.getName() + " </font>" +
                                "<font color=#A9CEFF face=\"Roboto Medium\" size=4>" + status.getHandler() + " </font>" +
                                "<font color=\"lightgrey\" face=\"Roboto Light\" size=2>" + status.getDate() + "</font><br>" +
                                "<<font color=#23232323 face=\"Roboto Light\" size=3>" + status.getText() + "</font>";
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        tableTweets = new JTable(modelTweets);
        tableTweets.getColumn("avatar").setMaxWidth(100);
        tableTweets.getColumn("avatar").setMinWidth(100);
        tableTweets.setRowHeight(100);
        tableTweets.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableTweets.getTableHeader().setUI(null);
        tableTweets.setShowVerticalLines(false);
        tableTweets.getColumn("textPane").setCellRenderer(new DefaultTableCellRenderer());
        scrollPaneTweets = new JScrollPane(tableTweets);

        // -----------------------------------------------------
        // PANEL USERS

        panelUsers = new JPanel(new BorderLayout());
        modelUsers = new GenericDomainTableModel<UserGUI>(Arrays.asList(new String[]{"avatar", "textPane"})) {
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
                UserGUI user = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return GetImage.getImage(user.getAvatar(), 50, 50);
                    case 1:
                        return "<html><font color=#53535353 face=\"Roboto Light\" size=4>" + user.getName() + "</font><br>" +
                                "<font color=#A9CEFF face=\"Roboto Medium\" size=3 align=right>" + user.getHandler() +
                                "</font>";
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        tableUsers = new JTable(modelUsers);
        tableUsers.getColumn("avatar").setMaxWidth(65);
        tableUsers.getColumn("avatar").setMinWidth(65);
        tableUsers.setRowHeight(65);
        tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableUsers.getTableHeader().setUI(null);
        tableUsers.setShowVerticalLines(false);
        tableUsers.setFocusable(false);
        scrollPaneUsers = new JScrollPane(tableUsers);

        // -----------------------------------------------------
        // PANEL PROFILE

        // TODO
        this.addStuff();

        panelProfile = new JPanel(new BorderLayout());
        panelProfile.setBackground(Color.decode("#232323"));
        JLabel labelProfileAvatar = new JLabel(GetImage.getImage(activeUser.getAvatar(), 72, 72));
        labelProfileAvatar.setBorder(new EmptyBorder(10, 10, 30, 0));
        String profileData = "<html><font color=#FFFFFF face =\"Roboto Light\" size=5>" + activeUser.getName() +
                "</font><br><font color=#c9e0ff face =\"Roboto Light\" size=4>" + activeUser.getHandler() + "</font>";
        JLabel labelProfileData = new JLabel(profileData);
        labelProfileData.setBorder(new EmptyBorder(0, 10, 10, 10));
        labelProfileData.setHorizontalAlignment(SwingConstants.RIGHT);
        labelProfileData.setVerticalAlignment(SwingConstants.BOTTOM);
        panelProfile.add(labelProfileAvatar, BorderLayout.WEST);
        panelProfile.add(labelProfileData, BorderLayout.EAST);

        // -----------------------------------------------------
        // PANEL NEW PEOPLE

        panelNewPeople = new JPanel();
        panelNewPeople.setBackground(Color.decode("#C9E0FF"));
        buttonNewPeople = new JButton("Find New People");
        buttonNewPeople.setBackground(null);
        buttonNewPeople.setFont(new Font("Roboto Light", 0, 20));
        buttonNewPeople.setForeground(Color.decode("#535353"));
        buttonNewPeople.setBorder(new EmptyBorder(10, 30, 10, 30));
        buttonNewPeople.addActionListener(e -> {
            this.buttonNewPeople.setVisible(false);
            this.comboNewPeople.setVisible(true);
        });

        comboNewPeople = new JComboBox<>();
        comboNewPeople.setPreferredSize(new Dimension(240, 45));
        comboNewPeople.setMinimumSize(new Dimension(240, 45));
        comboNewPeople.setVisible(false);
        comboNewPeople.setEditable(true);

        for (UserGUI user : users)
            comboNewPeople.addItem(user.getHandler() + " " + user.getName());
        JTextField textNewPeople = ((JTextField) comboNewPeople.getEditor().getEditorComponent());
        textNewPeople.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased (KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // TODO
                    comboNewPeople.setVisible(false);
                    buttonNewPeople.setVisible(true);
                }
                else {
                    String string = textNewPeople.getText();
                    comboNewPeople.removeAllItems();
                    for (UserGUI user : users)
                        if (user.getHandler().contains(string) || user.getName().contains(string))
                            comboNewPeople.addItem(user.getHandler() + " " + user.getName());
                    textNewPeople.setText(string);
                    comboNewPeople.showPopup();
                }
            }
        });

        textNewPeople.setText("");
        textNewPeople.setPreferredSize(new Dimension(240, 45));
        textNewPeople.setMinimumSize(new Dimension(240, 45));
        textNewPeople.setMaximumSize(new Dimension(240, 45));

        panelNewPeople.add(buttonNewPeople);
        panelNewPeople.add(comboNewPeople);

        // -----------------------------------------------------
        // BUILD PANEL

        panelUsers.add(scrollPaneUsers, BorderLayout.CENTER);
        panelUsers.add(panelProfile, BorderLayout.NORTH);
        panelUsers.add(panelNewPeople, BorderLayout.SOUTH);
        panelBody = new JSplitPane();
        panelBody.setLeftComponent(scrollPaneTweets);
        panelBody.setRightComponent(panelUsers);
        panelBody.setDividerSize(0);
        panelGeneral.add(panelBody, BorderLayout.CENTER);
    }

    /**
     * <B>FUNCTION:</B> hard-code data into the interface
     */
    private void addStuff() {
        StatusGUI status1 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
        modelTweets.addRow(status1);
        StatusGUI status2 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar2.png");
        modelTweets.addRow(status2);
        StatusGUI status3 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar3.png");
        modelTweets.addRow(status3);
        StatusGUI status4 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar4.png");
        modelTweets.addRow(status4);
        StatusGUI status5 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
        modelTweets.addRow(status5);
        StatusGUI status6 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar2.png");
        modelTweets.addRow(status6);
        StatusGUI status7 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar3.png");
        modelTweets.addRow(status7);
        StatusGUI status8 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar4.png");
        modelTweets.addRow(status8);
        StatusGUI status9 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
        modelTweets.addRow(status9);

        UserGUI hum1 = new UserGUI("avatar4.png", "Clara", "@clara");
        users.add(hum1);
        activeUser.addFriend(hum1);
        UserGUI hum3 = new UserGUI("avatar3.png", "Javier Revillas", "@jrevillas");
        users.add(hum3);
        activeUser.addFriend(hum3);
        UserGUI hum4 = new UserGUI("avatar4.png", "Javier Ruiz", "@jruiz");
        users.add(hum4);
        activeUser.addFriend(hum4);
        UserGUI hum5 = new UserGUI("avatar1.png", "Daniel Melero", "@dmelero");
        users.add(hum5);
        UserGUI hum6 = new UserGUI("avatar2.png", "Victor Blazquez", "@vbgalache");
        users.add(hum6);
        UserGUI hum7 = new UserGUI("avatar3.png", "Marcos Núñez", "@marcosnunez");
        users.add(hum7);
        UserGUI hum8 = new UserGUI("avatar4.png", "Jorge Pelos", "@pelosjorge");
        users.add(hum8);
        this.modelUsers.addRows(activeUser.getFriends());

    }

    public static void main(String[] args) {
        new GUI();
    }
}
