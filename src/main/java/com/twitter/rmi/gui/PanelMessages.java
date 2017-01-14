package com.twitter.rmi.gui;

import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Arrays;

class PanelMessages extends JScrollPane {
    private GenericDomainTableModel<LocalMessages> modelMessages;
    private User activeUser;

    PanelMessages(Component component) {
        super(component);
        modelMessages = new GenericDomainTableModel<LocalMessages>(Arrays.asList(new String[]{"avatar", "content"})) {
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
                LocalMessages pm = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return Auxiliar.getImage("avatar1.png", 72, 72);
                    case 1:
                        return getHTML(pm.getSender(), pm.getDate(), pm.getBody());
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        JTable tableTweets = (JTable) component;
        tableTweets.setModel(modelMessages);
        tableTweets.getColumn("avatar").setMaxWidth(100);
        tableTweets.getColumn("avatar").setMinWidth(100);
        tableTweets.setRowHeight(100);
        tableTweets.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableTweets.getTableHeader().setUI(null);
        tableTweets.setShowVerticalLines(false);
        tableTweets.setSelectionBackground(tableTweets.getBackground());
        tableTweets.setSelectionForeground(tableTweets.getForeground());
        this.setMinimumSize(new Dimension(300, 450));
        this.setPreferredSize(new Dimension(300, 450));
        this.setSize(new Dimension(300, 450));
    }

    PanelMessages setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        return this;
    }

    private String getHTML(String sender, String date, String body) {
        return "<html>" +
                "<font color=#737373 face =\"Roboto Light\" size=4>from </font>" +
                "<font color=#A9CEFF face=\"Roboto Medium\" size=5>" + sender + "</font>" +
                "<font color=#737373 face=\"Roboto Light\" size=4> " + Auxiliar.formatDate(date) + "</font><br>" +
                "<font color=#232323 face=\"Roboto Light\" size=4>" + body + "</font>";
    }

    PanelMessages refreshMessages() throws RemoteException {
        modelMessages.clearTableModelData();
        modelMessages.addRows(Auxiliar.getLocalMessages(activeUser.getReceivedPM()));
        return this;
    }
}