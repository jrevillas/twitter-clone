package com.twitter.rmi.gui;

import com.twitter.rmi.common.Twitter;
import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.ToastMessage;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class GUI {
    enum VIEW {
        LOGIN, MESSAGES, TWEETS, PROFILE
    }

    enum FrameState {MAXIMIZE, MINIMIZE, RESTORE}

    JFrame frame;
    private JPanel panelGeneral;
    private PanelHeader panelHeader;
    private PanelUsers panelUsers;
    private PanelTweets panelTweets;
    private PanelMessages panelMessages;
    private Component glassPane;
    private Twitter twitter;
    private JSplitPane splitBody;
    private VIEW activeView;
    private GUICallbackImpl guiCallback;
    private User remoteUser;
    private LocalUser localUser;
    private DialogLoading dialogLoading;
    private PanelLogin panelLogin;
    private boolean online = true;

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
            Registry registry;
            if (online)
                registry = LocateRegistry.getRegistry("api.twitter-rmi.com",
                        Registry.REGISTRY_PORT);
            else
                registry = LocateRegistry.getRegistry("localhost",
                        Registry.REGISTRY_PORT);
            twitter = (Twitter) registry.lookup("com.twitter.rmi.server.TwitterImpl");
            System.out.println("Twitter version: " + twitter.getVersion());
        } catch (Exception e) {
            // TODO
            JOptionPane.showMessageDialog(null, "No connection with the server",
                    "Connectivity Issues", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
        UIManager.put("OptionPane.messageFont",
                new Font("Roboto Light", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont",
                new Font("Roboto Light", Font.PLAIN, 16));
        login();
    }

    // Initializer

    /**
     * <B>FUNCTION:</B> starts the login process
     */
    private void login() {
        panelLogin = new PanelLogin().setGUI(this);
        activeView = VIEW.LOGIN;
        this.frame.setContentPane(panelLogin);
        frame.setUndecorated(true);
        this.frame.setResizable(false);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    void start(User activeUser) {
        this.remoteUser = activeUser;
        activeView = VIEW.TWEETS;
        panelGeneral = new JPanel();
        panelGeneral.setLayout(new BorderLayout());

        panelHeader = new PanelHeader().setType(VIEW.TWEETS).setGUI(this);
        panelGeneral.add(panelHeader, BorderLayout.NORTH);

        panelTweets = new PanelTweets(new JTable()).setActiveUser(activeUser);
        panelMessages = new PanelMessages(new JTable()).setActiveUser(activeUser);

        try {
            localUser = new LocalUser().setHandle(activeUser.getHandle());
            if (!online)
                guiCallback = new GUICallbackImpl().setThings(this, activeUser).subscribe();
            panelUsers = new PanelUsers().setActiveUser(this, activeUser);
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }

        splitBody = new JSplitPane();
        splitBody.setDividerSize(0);
        splitBody.setRightComponent(panelUsers);
        splitBody.setLeftComponent(panelTweets);
        panelGeneral.add(splitBody, BorderLayout.CENTER);

        showDialogLoading(false);

        frame.setContentPane(panelGeneral);
        frame.pack();
        splitBody.setDividerLocation(0.72);
        splitBody.setResizeWeight(1);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.pack();
        refresh();
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
        try {
            String res = dialogTweet.getResult();
            if (res != null) {
                remoteUser.submitStatus(res);
                this.refresh();
            }
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }
    }

    void writeNewMessage() {
        glassPane.setVisible(true);
        frame.setEnabled(false);
        DialogMessage dialogMessage = new DialogMessage().setActiveUser(remoteUser);
        if (activeView == VIEW.PROFILE) {
            String currentHandle = panelUsers.getCurrentUser();
            if (!currentHandle.equals(localUser.getHandle()))
                dialogMessage.setDestinatary(currentHandle);
        }
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
                remoteUser.submitPm(content, receiver);
            }
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }
    }

    void changePanel(VIEW toView) {
        if (activeView != VIEW.LOGIN) {
            switch (toView) {
                case TWEETS:
                    splitBody.setLeftComponent(panelTweets);
                    panelUsers.changeUser("");
                    break;
                case MESSAGES:
                    splitBody.setLeftComponent(panelMessages);
                    break;
            }
            activeView = toView;
            panelHeader.setType(toView);
            splitBody.setDividerLocation(0.72);
            splitBody.setResizeWeight(1);
            refresh();
        }
    }

    void notifyStatus(String handle) {
        new ToastMessage().setText("New Status from " + handle)
                .setLocation(panelHeader).setVisibleFor(15);
    }

    void openSettings() {
        // TODO
        JOptionPane.showMessageDialog(panelGeneral, "UNDER CONSTRUCTION", "TODO",
                JOptionPane.ERROR_MESSAGE);
    }

    private void refresh() {
        showDialogLoading(true);
        try {
            switch (activeView) {
                case MESSAGES:
                    panelMessages.refreshMessages();
                    break;
                case TWEETS:
                    panelTweets.refreshTimeline();
                    break;
                case PROFILE:
                    panelTweets.getStatusFrom(panelUsers.getCurrentUser());
            }
        } catch (RemoteException e) {
            // TODO
            e.printStackTrace();
        }
        showDialogLoading(false);
    }

    void exit() {
        if (!online && remoteUser != null)
            guiCallback.unsubscribe();
        System.exit(0);
    }

    // More Methods

    void showDialogLoading(boolean visibility) {
        glassPane.setVisible(visibility);
        if (visibility) {
            dialogLoading = new DialogLoading();
            dialogLoading.setVisible(true);
            if (activeView == VIEW.LOGIN)
                dialogLoading.setLocationRelativeTo(panelLogin);
            else
                dialogLoading.setLocationRelativeTo(panelGeneral);
        } else {
            dialogLoading.setVisible(false);
            dialogLoading.dispose();
        }
    }

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
