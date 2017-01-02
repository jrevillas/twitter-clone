package com.twitter.rmi.gui.resources;

import javax.swing.*;

public class GetImage {

    public static ImageIcon getImage(String name, int height, int width) {
//        ImageIcon icon = new ImageIcon(GetImage.class.getResource(name));

        ImageIcon icon = new ImageIcon("/home/migui/IdeaProjects/twitter-rmi/src/main/java/com/twitter/rmi/gui/resources/" + name);

        return new ImageIcon(icon.getImage()
                .getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH));
    }

    public static ImageIcon getImage(String name) {
        return new ImageIcon(GetImage.class.getResource(name));
    }
}
