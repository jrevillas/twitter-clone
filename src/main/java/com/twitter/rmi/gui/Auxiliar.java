package com.twitter.rmi.gui;

import com.twitter.rmi.common.PrivateMessage;
import com.twitter.rmi.common.Status;

import javax.swing.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

class Auxiliar {

    static ImageIcon getImage(String name, int height, int width) {
        ImageIcon icon = new ImageIcon(Thread.currentThread().
                getContextClassLoader().getResource(name));
        return new ImageIcon(icon.getImage()
                .getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
    }

    static String formatDate(String date) {
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

    static List<LocalStatus> getLocalStatus(List<Status> timeline) throws RemoteException {
        List<LocalStatus> localStatus = new ArrayList<>();
        for (Status st : timeline) {
            localStatus.add(new LocalStatus().setBody(st.getBody()).
                    setDate(st.getDate()).setUserHandle(st.getUserHandle()));
        }
        return localStatus;
    }

    static List<LocalMessages> getLocalMessages(List<PrivateMessage> listPM) throws RemoteException {
        List<LocalMessages> localPM = new ArrayList<>();
        for (PrivateMessage pm : listPM)
            localPM.add(new LocalMessages().setDate(pm.getDate()).setReceiver(pm.getReceiver())
                    .setBody(pm.getBody()).setSender(pm.getSender()));
        return localPM;
    }

//    public List<Integer> mergeList(List<Integer> list1, List<Integer> list2) {
//        List<Integer> resultado = new ArrayList<>();
//        if (list1.isEmpty() && list2.isEmpty())
//            return resultado;
//        if (list1.isEmpty())
//            finiquita(resultado, null, list2.iterator());
//        else if (list2.isEmpty())
//            finiquita(resultado, null, list1.iterator());
//        else
//            inserta(resultado, null, list1.iterator(), null, list2.iterator());
//        return resultado;
//    }
//
//    private static void inserta(List<Integer> resultado, Integer valor1, Iterator<Integer> cursor1,
//                                Integer valor2, Iterator<Integer> cursor2) {
//        if (cursor1 != null || cursor2 != null)
//            if (!cursor1.hasNext())
//                finiquita(resultado, , cursor2);
//            else if (!cursor2.hasNext())
//                finiquita(resultado, cursor1);
//            else if (compare(cursor1.element(), cursor2.element()) <= 0) {
//                resultado.add(cursor1.element());
//                inserta(resultado, cursor1.next(), cursor2);
//            } else {
//                resultado.add(cursor2.element());
//                inserta(resultado, lista1, cursor1, lista2, lista2.next(cursor2));
//            }
//    }
//
//    private static void finiquita(List<Integer> resultado, Integer valor, Iterator<Integer> cursor) {
//        Integer valorAux;
//        if (valor == null) {
//            if (cursor.hasNext()) {
//                resultado.add(cursor.next());
//                finiquita(resultado, null, cursor);
//            }
//        } else {
//            if (cursor.hasNext()) {
//                valorAux = cursor.next();
//                if (valorAux < valor) {
//                    resultado.add(valorAux);
//                    finiquita(resultado, valor, cursor);
//                } else {
//                    resultado.add(valor);
//                    resultado.add(valorAux);
//                    finiquita(resultado, valor, cursor);
//                }
//            }
//            else
//                resultado.add(valor);
//        }
//    }
//    static List<LocalMessages> getLocalMessages(List<LocalMessages> localPM, Iterator<PrivateMessage> sentIterator,
//                                                Iterator<PrivateMessage> receivedIterator, PrivateMessage sentPM,
//                                                PrivateMessage recvPM) throws RemoteException {
//        if (localPM == null)
//            localPM = new ArrayList<>();
//        if (!sentIterator.hasNext() && !receivedIterator.hasNext())
//            return localPM;
//        else if (!sentIterator.hasNext())
//            auxMessages(localPM, receivedIterator, sentPM);
//        else if (!receivedIterator.hasNext())
//            auxMessages(localPM, sentIterator, recvPM);
//        else {
//            sentPM = sentIterator.next();
//            recvPM = receivedIterator.next();
//            do {
//                if (Long.valueOf(sentPM.getDate()) < Long.valueOf(recvPM.getDate())) {
//                    localPM.add(new LocalMessages().setReceiver(sentPM.getReceiver()).setBody(sentPM.getBody()).
//                            setSender(sentPM.getSender()).setDate(sentPM.getDate()));
//                    sentPM = sentIterator.next();
//                } else {
//                    localPM.add(new LocalMessages().setReceiver(recvPM.getReceiver()).setBody(recvPM.getBody()).
//                            setSender(recvPM.getSender()).setDate(recvPM.getDate()));
//                    recvPM = receivedIterator.next();
//                }
//            } while (sentIterator.hasNext() && receivedIterator.hasNext());
//            getLocalMessages(localPM, sentIterator, receivedIterator, sentPM, recvPM);
//        }
//        System.out.println(localPM.size());
//        return localPM;
//    }
//
//    private static List<LocalMessages> auxMessages(List<LocalMessages> localPM, Iterator<PrivateMessage> iterator,
//                                                   PrivateMessage pmAux)
//            throws RemoteException {
//        System.out.println(localPM.size());
//        PrivateMessage pm = iterator.next();
//        if (pmAux != null) {
//            if (Long.valueOf(pmAux.getDate()) < Long.valueOf(pm.getDate())) {
//                localPM.add(new LocalMessages().setReceiver(pmAux.getReceiver()).setBody(pmAux.getBody()).
//                        setSender(pmAux.getSender()).setDate(pmAux.getDate()));
//                pmAux = null;
//            }
//        }
//        localPM.add(new LocalMessages().setReceiver(pm.getReceiver()).setBody(pm.getBody()).
//                setSender(pm.getSender()).setDate(pm.getDate()));
//        if (iterator.hasNext())
//            auxMessages(localPM, iterator, pmAux);
//        return localPM;
//    }
}