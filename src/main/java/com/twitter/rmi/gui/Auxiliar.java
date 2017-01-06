package com.twitter.rmi.gui;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class Auxiliar {

    static ImageIcon getImage(String name, int height, int width) {
//        ImageIcon icon = new ImageIcon(Auxiliar.class.getResource(name));

        ImageIcon icon = new ImageIcon("/home/migui/IdeaProjects/twitter-rmi/src/main/java/com/twitter/rmi/gui/resources/" + name);

        return new ImageIcon(icon.getImage()
                .getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH));
    }

    static ImageIcon getImage(String name) {
        return new ImageIcon(Auxiliar.class.getResource(name));
    }

    static String formatDate(String date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            long dateNow = new Date().getTime();
            long dateStatus = Long.parseLong(date);
            date = sdf.format(new Date(dateStatus));
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
                return date.substring(0, 5);
            return date;
        } catch (Exception e) {
            return "before the big bang";
        }
    }
}
