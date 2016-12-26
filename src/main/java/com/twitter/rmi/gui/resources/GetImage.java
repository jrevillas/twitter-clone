package com.twitter.rmi.gui.resources;

import javax.swing.*;

/**
 * Created by migui on 10/12/2016.
 */
public class GetImage {

    public static ImageIcon getImage(String name, int height, int width) {
//        ImageIcon icon = new ImageIcon(GetImage.class.getResource(name));
        ImageIcon icon = new ImageIcon(GetImage.class.getResource(name));

        return new ImageIcon(icon.getImage()
                .getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH));
    }

    public static ImageIcon getImage(String name) {
        return new ImageIcon(GetImage.class.getResource(name));
    }
}
