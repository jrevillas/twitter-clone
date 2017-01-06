package com.twitter.rmi.gui;

import com.twitter.rmi.common.ClientCallback;
import com.twitter.rmi.common.Twitter;
import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.ToastMessage;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GUI extends UnicastRemoteObject implements ClientCallback {

    enum VIEW {
        LOGIN, MESSAGES, TWEETS, PROFILE
    }

    enum FrameState { MAXIMIZE, MINIMIZE, RESTORE }

    JFrame frame;
    private JPanel panelGeneral;
    private PanelHeader panelHeader;
    private PanelUsers panelUsers;
    private PanelTweets panelTweets;
    private PanelMessages panelMessages;
    private Component glassPane;
    private Twitter twitter;
    private User activeUser;
    private JSplitPane splitBody;


    /**
     * <B>FUNCTION:</B> main constructor of the interface
     */
    private GUI() throws RemoteException {
        // Connection with the RMI Server

        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
        System.setProperty("java.security.policy", "security.policy");
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);
            twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No connection with the server",
                    "Connectivity Issues", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

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

    // Initializer

    /**
     * <B>FUNCTION:</B> starts the login process
     */
    private void login() {
        JPanel panelLogin = new PanelLogin().setGUI(this);
        this.frame.setContentPane(panelLogin);
        frame.setUndecorated(true);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    void start(User activeUser) {
        try {
            activeUser.pushSubscribe(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.activeUser = activeUser;
        this.panelGeneral = new JPanel(); // TODO llevar al metodo change view
        this.panelGeneral.setLayout(new BorderLayout());

        panelHeader = new PanelHeader().setType(VIEW.TWEETS).setGUI(this);
        panelGeneral.add(panelHeader, BorderLayout.NORTH);

        panelTweets = new PanelTweets(new JTable()).setActiveUser(activeUser).refreshTimeline();
        panelMessages = new PanelMessages(new JTable()).setActiveUser(activeUser);

        try {
            panelUsers = new PanelUsers().setActiveUser(this, activeUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        splitBody = new JSplitPane();
        splitBody.setDividerSize(0);
        splitBody.setRightComponent(panelUsers);
        splitBody.setLeftComponent(panelTweets);
        panelGeneral.add(splitBody, BorderLayout.CENTER);

        this.frame.setContentPane(panelGeneral);
        this.frame.pack();
        splitBody.setDividerLocation(0.72);
        splitBody.setResizeWeight(1);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);

        this.writeNewMessage();
    }

    // System Methods

    void writeNewTweet() {
        glassPane.setVisible(true);
        frame.setEnabled(false);

        DialogTweet dialogTweet = new DialogTweet();
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
                panelTweets.refreshTimeline();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void writeNewMessage() {
        glassPane.setVisible(true);
        frame.setEnabled(false);

        DialogMessage dialogMessage = new DialogMessage().setActiveUser(activeUser);
        // TODO if visiting o lo que sea, hacer dialogMessage.setDestinatary();
        dialogMessage.setLocationRelativeTo(panelGeneral);
        dialogMessage.setVisible(true);

        glassPane.setVisible(false);
        frame.setEnabled(true);
        glassPane.setVisible(false);
        frame.setEnabled(true);
        try {
            String receiver = dialogMessage.getReceiver();
            String content = dialogMessage.getContent();
            if (receiver != null && content != null) {
                activeUser.submitPm(content, receiver);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void openSettings() {
        // TODO
        JOptionPane.showMessageDialog(panelGeneral, "UNDER CONSTRUCTION", "TODO", JOptionPane.ERROR_MESSAGE);
    }

    void changePanel(VIEW toView) {
        switch (toView) {
            case TWEETS:
                splitBody.setLeftComponent(panelTweets.refreshTimeline());
                panelUsers.changeUser("");
                break;
            case MESSAGES:
                splitBody.setLeftComponent(panelMessages.getMessages());
                break;
        }
        panelHeader.setType(toView);
        splitBody.setDividerLocation(0.72);
        splitBody.setResizeWeight(1);
    }

    void changeUser(String handle) {
        panelHeader.setLabel((handle.equals("") ? "Timeline" : ""));
        if (handle.equals(""))
            panelTweets.refreshTimeline();
        else
            panelTweets.getStatusFrom(handle);
        panelUsers.changeUser(handle);
    }

    void notifyMessage(String handle) {
        new ToastMessage().setText("New PM from " + handle).setLocation(panelHeader).setVisibleFor(2.5);
    }

    private void notifyStatus(String handle) {
        new ToastMessage().setText("New Status from " + handle).setLocation(panelHeader).setVisibleFor(100);
    }

    @Override
    public String notifyMe(String username, String msg) throws RemoteException {
        notifyStatus(username);
        return null;
    }

    void exit() {
        if (activeUser != null)
            try {
                activeUser.pushUnsubscribe(this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        System.exit(0);
    }

    // More Methods

    void setFrameState(FrameState fs) {
        switch (fs) {
            case MAXIMIZE:

                break;
            case MINIMIZE:
                frame.setState(Frame.ICONIFIED);
                break;
            case RESTORE:
                frame.setState(Frame.NORMAL);
                break;
        }
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public static void main(String[] args) throws RemoteException {
        new GUI();
    }
}
