package com.twitter.rmi.gui;

import com.twitter.rmi.common.Status;
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
    private GenericDomainTableModel<Status> modelTweets;
    private User activeUser;

    PanelTweets(Component component) {
        super(component);
        modelTweets = new GenericDomainTableModel<Status>(Arrays.asList(new String[]{"avatar", "content"})) {
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
                Status status = this.getDomainObject(rowIndex);
                switch (columnIndex) {
                    case 0:
//                        return Auxiliar.getImage(status.getAvatar(), 72, 72); // TODO
                        return Auxiliar.getImage("avatar1.png", 72, 72);
                    case 1:
                        try {
                            return "<html>" +
                                    "<font color=#A9CEFF face=\"Roboto Medium\" size=5>" + status.getUserHandle() + " </font>" +
                                    "<font color=#737373 face=\"Roboto Light\" size=4>" + Auxiliar.formatDate(status.getDate()) + "</font><br>" +
                                    "<<font color=#23232323 face=\"Roboto Light\" size=4>" + status.getBody() + "</font>";
                        } catch (RemoteException e) {
                            return "ERROR";
                        }
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

    PanelTweets setActiveUser(User activeUser){
        this.activeUser = activeUser;
        return this;
    }

    PanelTweets refreshTimeline() {
        System.out.println("Refreshing tweets");
        modelTweets.clearTableModelData();
        try {
            modelTweets.addRows(activeUser.getTimeline());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return this;
    }

    void getStatusFrom(String handle) {
        modelTweets.clearTableModelData();
        try {
            modelTweets.addRows(activeUser.getStatuses(handle));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
