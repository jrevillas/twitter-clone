package com.twitter.rmi.gui;

import com.twitter.rmi.common.Status;
import com.twitter.rmi.common.Twitter;
import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;
import com.twitter.rmi.gui.resources.GetImage;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Date;

public class GUI {
    JFrame frame;
    private JPanel panelGeneral;
    private PanelHeader panelHeader;
    private JSplitPane panelBody;
    PanelUsers panelUsers;
    private JScrollPane scrollPaneTweets;
    private Component glassPane;

    private GenericDomainTableModel<Status> modelTweets;

    Twitter twitter;
    User activeUser;

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
            JOptionPane.showMessageDialog(null, "No connection with the server",
                    "Connectivity Issues", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
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
        UIManager.put("OptionPane.messageFont", new Font("Roboto Light", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Roboto Light", Font.PLAIN, 16));

        login();
    }

    private void login() {

        JPanel panelLogin = new PanelLogin().setGUI(this);

        this.frame.setContentPane(panelLogin);
        frame.setUndecorated(true);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    void start() {
        this.panelGeneral = new JPanel();
        this.panelGeneral.setLayout(new BorderLayout());

        panelHeader = new PanelHeader().setType(false).setGUI(this);
        panelGeneral.add(panelHeader, BorderLayout.NORTH);

        this.initializeTweets("");

        try {
            panelUsers = new PanelUsers().setActiveUser(this, activeUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        panelBody = new JSplitPane();
        panelBody.setLeftComponent(scrollPaneTweets);
        panelBody.setRightComponent(panelUsers);
        panelBody.setDividerSize(0);
        panelGeneral.add(panelBody, BorderLayout.CENTER);

        this.frame.setContentPane(panelGeneral);
        this.frame.pack();
        panelBody.setResizeWeight(1);
        panelBody.setDividerLocation(0.72);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    void initializeTweets(String handle) {
        modelTweets = new GenericDomainTableModel<Status>(Arrays.asList(new String[]{"avatar", "content"})) {
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
                                    "<font color=#A9CEFF face=\"Roboto Medium\" size=5>" + status.getUserHandle() + " </font>" +
                                    "<font color=#737373 face=\"Roboto Light\" size=4>" + formatDate(status.getDate()) + "</font><br>" +
                                    "<<font color=#23232323 face=\"Roboto Light\" size=4>" + status.getBody() + "</font>";
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
        JTable tableTweets = new JTable(modelTweets);
        tableTweets.getColumn("avatar").setMaxWidth(100);
        tableTweets.getColumn("avatar").setMinWidth(100);
        tableTweets.setRowHeight(100);
        tableTweets.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableTweets.getTableHeader().setUI(null);
        tableTweets.setShowVerticalLines(false);
        scrollPaneTweets = new JScrollPane(tableTweets);
        int dim = 300;
        scrollPaneTweets.setMinimumSize(new Dimension(dim,450));
        scrollPaneTweets.setPreferredSize(new Dimension(dim,450));
        scrollPaneTweets.setSize(new Dimension(dim,450));
        try {
            modelTweets.addRows(activeUser.getTimeline());
        } catch (RemoteException ignored) {}
    }

    void writeNewTweet() {
        changeUser("");
        glassPane.setVisible(true);
        frame.setEnabled(false);
        DialogTweet dialogTweet = new DialogTweet().setGUI(this);

        dialogTweet.setLocationRelativeTo(panelGeneral);

        dialogTweet.setVisible(true);
        glassPane.setVisible(false);
        frame.setEnabled(true);
        glassPane.setVisible(false);
        frame.setEnabled(true);
        try {
            String res = dialogTweet.getResult();
            if (res != null) {
                activeUser.submitStatus(res);
                modelTweets.clearTableModelData();
                modelTweets.addRows(activeUser.getTimeline());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void openSettings() {
        // TODO
        JOptionPane.showMessageDialog(panelGeneral, "UNDER CONSTRUCTION", "TODO", JOptionPane.ERROR_MESSAGE);
    }

    void changeUser(String handle) {
        try {
            panelHeader.setLabel((handle.equals("")? "Timeline" : ""));
            modelTweets.clearTableModelData();
            if (handle.equals(""))
                modelTweets.addRows(activeUser.getTimeline());
            else
                modelTweets.addRows(activeUser.getStatuses(handle));
            panelUsers.changeUser(handle);
        } catch (RemoteException ignored) {}
    }

    void exit() {
        System.exit(0);
    }

    public static String formatDate(String date){
        try {
            long dateNow = new Date().getTime();
            long dateStatus = Long.parseLong(date);
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
            e.printStackTrace();
            return "before the big bang";
        }
    }

    public static void main(String[] args) {
        new GUI();
    }
}
