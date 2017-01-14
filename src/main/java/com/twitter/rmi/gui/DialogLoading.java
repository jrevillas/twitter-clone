package com.twitter.rmi.gui;

import javax.swing.*;

class DialogLoading extends JDialog{
    DialogLoading() {
        ImageIcon icon = new ImageIcon(Thread.currentThread().
                getContextClassLoader().getResource("loading.gif"));
        JLabel label = new JLabel(icon);
        JPanel contentPane = new JPanel();
        contentPane.add(label);
        this.setUndecorated(true);
        this.add(contentPane);
        this.pack();
        this.setAlwaysOnTop(true);
        this.setResizable(false);
    }
}
