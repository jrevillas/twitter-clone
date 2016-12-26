package com.twitter.rmi.gui.auxiliar;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * Created by migui on 08/12/2016.
 */
public enum StyleSelector {
    HANDLERtweet, NAMEtweet, TEXT, MENTION, HASHTAG, HANDLERuser, NAMEuser;

    public static Style getStyle(StyleSelector type, JTextPane text) {
        Style style = text.addStyle(null, null);
        switch (type) {
            case NAMEtweet:
                StyleConstants.setFontFamily(style, "Roboto Bold");
                StyleConstants.setFontSize(style, 24);
                StyleConstants.setForeground(style, Color.decode("#232323"));
                StyleConstants.setBold(style, true);
                break;
            case HANDLERtweet:
                StyleConstants.setFontSize(style, 18);
                StyleConstants.setFontFamily(style, "Roboto Medium");
                StyleConstants.setForeground(style, Color.decode("#A9CEFF"));
                StyleConstants.setBold(style, true);
                break;
            case TEXT:
                StyleConstants.setFontSize(style, 14);
                StyleConstants.setFontFamily(style, "Roboto Light");
                StyleConstants.setForeground(style, Color.decode("#232323"));
                break;
            case MENTION:
                StyleConstants.setFontSize(style, 14);
                StyleConstants.setFontFamily(style, "Roboto Light");
                StyleConstants.setForeground(style, Color.decode("#a9ceff"));
                break;
            case HASHTAG:
                StyleConstants.setFontSize(style, 14);
                StyleConstants.setFontFamily(style, "Roboto Light");
                StyleConstants.setForeground(style, Color.decode("#a9ceff"));
                break;
            case NAMEuser:
                StyleConstants.setFontSize(style, 24);
                StyleConstants.setFontFamily(style, "Roboto Light");
                StyleConstants.setForeground(style, Color.decode("#232323"));
                break;
            case HANDLERuser:
                StyleConstants.setFontSize(style, 18);
                StyleConstants.setFontFamily(style, "Roboto Medium");
                StyleConstants.setForeground(style, Color.decode("#a9ceff"));
                StyleConstants.setAlignment(style,StyleConstants.ALIGN_RIGHT);
                break;
        }
        return style;
    }
}