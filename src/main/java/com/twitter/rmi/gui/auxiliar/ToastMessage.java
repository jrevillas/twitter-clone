package com.twitter.rmi.gui.auxiliar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

public class ToastMessage extends JDialog {
    private JLabel toastLabel;

    public ToastMessage() {
        setUndecorated(true);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#636363"));
        setContentPane(panel);

        toastLabel = new JLabel();
        toastLabel.setFont(new Font("Roboto Light", 0, 16));
        toastLabel.setForeground(Color.decode("#D3D3D3"));
        panel.add(toastLabel);
        this.setResizable(false);
        this.setOpacity(0.85F);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            }
        });
    }

    public ToastMessage setText(String toastString) {
        toastLabel.setText(toastString);
        this.pack();
        return this;
    }

    public ToastMessage setLocation(Component component){
        super.setLocationRelativeTo(component);
        return this;
    }

    public ToastMessage setVisibleFor(double timeInSeconds) {
        setAlwaysOnTop(true);
        setVisible(true);
        new Thread(() -> {
            try {
                Thread.sleep((int) (timeInSeconds * 1000));
                setVisible(false);
                dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return this;
    }
}