package com.twitter.rmi.gui;

import com.twitter.rmi.common.User;
import com.twitter.rmi.gui.auxiliar.GenericDomainTableModel;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.Arrays;

/**
 * Created by migui on 3/01/17.
 */
class PanelTweets extends JScrollPane {
    private GenericDomainTableModel<LocalStatus> modelTweets;
    private User activeUser;

    PanelTweets(Component component) {
        super(component);
        modelTweets = new GenericDomainTableModel<LocalStatus>(Arrays.asList(new String[]{"avatar", "content"})) {
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
                LocalStatus status = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return Auxiliar.getImage("avatar1.png", 72, 72);
                    case 1:
                        return "<html>" +
                                "<font color=#A9CEFF face=\"Roboto Medium\" size=5>" + status.getUserHandle() + " </font>" +
                                "<font color=#737373 face=\"Roboto Light\" size=4>" + Auxiliar.formatDate(status.getDate()) + "</font><br>" +
                                "<<font color=#23232323 face=\"Roboto Light\" size=4>" + status.getBody() + "</font>";
                    default:
                        return null;
                }
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            }
        };
        JTable tableTweets = (JTable) component;
        tableTweets.setModel(modelTweets);
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

    PanelTweets setActiveUser(User activeUser) {
        this.activeUser = activeUser;
        return this;
    }

    PanelTweets refreshTimeline() throws RemoteException {
        modelTweets.clearTableModelData();
        modelTweets.addRows(Auxiliar.getLocalStatus(activeUser.getTimeline()));
        return this;
    }

    void getStatusFrom(String handle) throws RemoteException {
        modelTweets.clearTableModelData();
        modelTweets.addRows(Auxiliar.getLocalStatus(activeUser.getStatuses(handle)));
    }
}