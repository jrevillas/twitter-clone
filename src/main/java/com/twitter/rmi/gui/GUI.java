package com.twitter.rmi.gui;

import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;
import com.twitter.rmi.gui.resources.GetImage;
import com.twitter.rmi.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.*;
import java.util.*;

public class GUI {
    private JFrame frame;
    private JPanel panelGeneral;
    private JPanel panelTweets;
    private JPanel panelUsers;
    private JPanel panelProfile;
    private PanelHeader panelHeader;
    private JPanel panelNewPeople;
    private JSplitPane panelBody;
    private JScrollPane scrollPaneTweets;
    private JScrollPane scrollPaneUsers;
    private JTable tableTweets;
    private JTable tableUsers;
    private JButton buttonNewPeople;
    private Component glassPane;

    private GenericDomainTableModel<Status> modelTweets;
    private GenericDomainTableModel<User> modelUsers;

    Twitter twitter;
    User activeUser;
    private JComboBox<String> comboNewPeople;

    /**
     * <B>FUNCTION:</B> main constructor of the interface
     */
    private GUI() {
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
//        System.setProperty("java.security.policy", "security.policy");
//        if (System.getSecurityManager() == null)
//            System.setSecurityManager(new SecurityManager());
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);
            twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ------------------------------------------------------------------
        // FRAME CONFIGURATION

        this.frame = new JFrame();
        frame.getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        });
        glassPane = frame.getGlassPane();
        this.frame.setTitle("twitter-rmi");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        login();
    }

    private void login() {
        JPanel panelLogin = new PanelLogin().addFrameAction(e -> exit(), e -> frame.setState(Frame.ICONIFIED)).
                addLogginButton(this);

        this.frame.setContentPane(panelLogin);

        frame.setUndecorated(true);
        this.frame.setVisible(true);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
    }

    void start() {
        this.panelGeneral = new JPanel();
        this.panelGeneral.setLayout(new BorderLayout());

        panelHeader = new PanelHeader().setType(false).addFrameAction(e -> exit(), e -> frame.setState(Frame.ICONIFIED))
                .addAction(null,null, e -> newTweet());
        panelGeneral.add(panelHeader, BorderLayout.NORTH);

        this.initializeBody();

        try {
            modelTweets.addRows(activeUser.getTimeline());
            modelUsers.addRows(activeUser.getFollowing(activeUser.getHandle()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        this.frame.setContentPane(panelGeneral);
        this.frame.setSize(new Dimension(800, 600));
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        panelBody.setResizeWeight(1);
        panelBody.setDividerLocation(0.68);
    }

    /**
     * <B>FUNCTION:</B> initialize all the elements in the Body Panel
     */
    private void initializeBody() {
        // -----------------------------------------------------
        // PANEL TWEETS
        panelTweets = new JPanel();
        modelTweets = new GenericDomainTableModel<Status>(Arrays.asList(new String[]{"avatar", "textPane"})) {
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
                Status status = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
//                        return GetImage.getImage(status.getAvatar(), 72, 72); // TODO
                        return GetImage.getImage("avatar1.png", 72, 72);
                    case 1:
                        try {
                            return "<html>" +
                                    //                              "<font color=#53535353 face=\"Roboto Bold\" size=5>" + status.getName() + " </font>" +
                                    "<font color=#A9CEFF face=\"Roboto Medium\" size=4>" + status.getUserHandle() + " </font>" +
                                    //                              "<font color=\"lightgrey\" face=\"Roboto Light\" size=2>" + status.getDate() + "</font><br>" +
                                    "<<font color=#23232323 face=\"Roboto Light\" size=3>" + status.getBody() + "</font>";
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
                                    //                                "<font color=#53535353 face=\"Roboto Light\" size=4>" + user.getName() + "</font><br>" +
                                    "<font color=#A9CEFF face=\"Roboto Medium\" size=3 align=right>" + user.getHandle() +
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
//        this.addStuff();

        panelProfile = new JPanel(new BorderLayout());
        panelProfile.setBackground(Color.decode("#232323"));
//        JLabel labelProfileAvatar = new JLabel(GetImage.getImage(activeUser.getAvatar(), 72, 72)); TODO
        JLabel labelProfileAvatar = new JLabel(GetImage.getImage("avatar1.png", 72, 72));
        labelProfileAvatar.setBorder(new EmptyBorder(10, 10, 30, 0));
        String profileData = null;
        try {
            profileData = "<html>" +
                    //                    "<font color=#FFFFFF face =\"Roboto Light\" size=5>" + activeUser.getName() + "</font><br>" + TODO
                    "<font color=#c9e0ff face =\"Roboto Light\" size=4>@" + activeUser.getHandle() + "</font>";
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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
        comboNewPeople.setFont(new Font("Roboto Light", 0, 14));
        comboNewPeople.setPreferredSize(new Dimension(240, 45));
        comboNewPeople.setMinimumSize(new Dimension(240, 45));
        comboNewPeople.setVisible(false);
        comboNewPeople.setEditable(true);
        try {
            for (User user : activeUser.getUsers())
//            comboNewPeople.addItem(user.getHandler() + " " + user.getName()); TODO
                comboNewPeople.addItem(user.getHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        JTextField textNewPeople = ((JTextField) comboNewPeople.getEditor().getEditorComponent());
        textNewPeople.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // TODO
                    comboNewPeople.setVisible(false);
                    buttonNewPeople.setVisible(true);
                } else {
                    try {
                        String string = textNewPeople.getText();
                        comboNewPeople.removeAllItems();
                        for (User user : activeUser.getUsers())
                            if (user.getHandle().contains(string)) // || user.getName().contains(string)) TODO
                                comboNewPeople.addItem(user.getHandle()); // + " " + user.getName());
                        textNewPeople.setText(string);
                        comboNewPeople.showPopup();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

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

    private void newTweet() {
        glassPane.setVisible(true);
        frame.setEnabled(false);
        DialogTweet dialogTweet = new DialogTweet();
        dialogTweet.setUndecorated(true);
        dialogTweet.pack();
        dialogTweet.setLocationRelativeTo(panelGeneral);
        dialogTweet.setResizable(false);
        dialogTweet.setVisible(true);
        dialogTweet.setGUI(this);
//        dialogTweet.getResult(); TODO
//        glassPane.setVisible(false);
//        frame.setEnabled(true);
//        System.out.println(result);
    }

    void continueNewTweet (String result){
        glassPane.setVisible(false); // TODO
        frame.setEnabled(true);
        System.out.println(result);
    }

    private void exit() {
        System.exit(0);
    }


    private static String timeSince(String date) {
        try {
            DateFormat format = new SimpleDateFormat("HH:mm dd/MM/yy");
            long dateNow = new Date().getTime();
            long dateStatus;
            dateStatus = format.parse(date).getTime();
            long diff = (dateNow - dateStatus) / 1000;
            if (diff < 60)
                return "just now";
            if (diff < 60 + 60)
                return "a minute ago";
            if (diff < 60 * 60)
                return (int) diff / 60 + " minutes ago";
            if (diff < 60 * 60 * 2)
                return "an hour ago";
            if (diff < 60 * 60 * 24)
                return (int) diff / 3600 + " hours ago";
            if (diff < 86400 * 365)
                return date.substring(6, 11);
            return date.substring(6);
        } catch (Exception e) {
            return "before the big bang";
        }
    }

    /**
     * <B>FUNCTION:</B> hard-code data into the interface
     * <p>
     * private void addStuff() {
     * StatusGUI status1 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
     * modelTweets.addRow(status1);
     * StatusGUI status2 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar2.png");
     * modelTweets.addRow(status2);
     * StatusGUI status3 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar3.png");
     * modelTweets.addRow(status3);
     * StatusGUI status4 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar4.png");
     * modelTweets.addRow(status4);
     * StatusGUI status5 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
     * modelTweets.addRow(status5);
     * StatusGUI status6 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar2.png");
     * modelTweets.addRow(status6);
     * StatusGUI status7 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar3.png");
     * modelTweets.addRow(status7);
     * StatusGUI status8 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar4.png");
     * modelTweets.addRow(status8);
     * StatusGUI status9 = new StatusGUI("Miguel Núñez", "@mnunezdm", "Hola este es un mensaje", "avatar1.png");
     * modelTweets.addRow(status9);
     * <p>
     * UserGUI hum1 = new UserGUI("avatar4.png", "Clara", "@clara");
     * users.add(hum1);
     * activeUser.addFriend(hum1);
     * UserGUI hum3 = new UserGUI("avatar3.png", "Javier Revillas", "@jrevillas");
     * users.add(hum3);
     * activeUser.addFriend(hum3);
     * UserGUI hum4 = new UserGUI("avatar4.png", "Javier Ruiz", "@jruiz");
     * users.add(hum4);
     * activeUser.addFriend(hum4);
     * UserGUI hum5 = new UserGUI("avatar1.png", "Daniel Melero", "@dmelero");
     * users.add(hum5);
     * UserGUI hum6 = new UserGUI("avatar2.png", "Victor Blazquez", "@vbgalache");
     * users.add(hum6);
     * UserGUI hum7 = new UserGUI("avatar3.png", "Marcos Núñez", "@marcosnunez");
     * users.add(hum7);
     * UserGUI hum8 = new UserGUI("avatar4.png", "Jorge Pelos", "@pelosjorge");
     * users.add(hum8);
     * this.modelUsers.addRows(activeUser.getFriends());
     * }
     */
    public static void main(String[] args) {
        new GUI();
    }
}
