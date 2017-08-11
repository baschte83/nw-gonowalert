/*
 * Created by
 * Justina Kluone,
 * Paul Campean,
 * Sebastian Baumann
 * 24.11.2016
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GoNowAlert {

    // IP-Adressen der einzelnen Bridges ------------------------------------------
    private static final String IP_ADRESS_LAMPS_BRIDGE1 = "10.28.9.120";
    private static final String IP_ADRESS_LAMPS_BRIDGE2 = "10.28.9.121";
    private static final String IP_ADRESS_LAMPS_BRIDGE3 = "10.28.9.122";
    private static final String IP_ADRESS_LAMPS_BRIDGE4 = "10.28.9.123";


    // Authorized-User-IDs --------------------------------------------------------
    private static final String AUTH_USER_BRIDGE1 = "197ea42c25303cef1a68c4042ed56887";
    private static final String AUTH_USER_BRIDGE2 = "3dc1d8f23e55321f3c049c03ac88dff";
    private static final String AUTH_USER_BRIDGE3 = "2217334838210e7f244460f83b42026f";
    private static final String AUTH_USER_BRIDGE4 = "2b2d3ff23d63751f10c1d8c0332d50ff";


    // Fest programmierte Kommandos im JSON-Format --------------------------------
    private static final String SET_ON = "{\"on\":true}";
    private static final String SET_OFF = "{\"on\":false,\"ct\":250,\"transitiontime\":1}";
    private static final String SET_ALARM_ON = "{\"hue\":0,\"sat\":254,\"alert\":\"lselect\"}";
    private static final String SET_ALARM_OFF = "{\"sat\":0,\"ct\":250,\"alert\":\"none\"}";


    // Deklaration benötigter Adress-Variablen ------------------------------------
    private int numberOfGroup;
    private int numberOfLamp;
    private int actionStatus;


    // URl-String jeder Bridge ----------------------------------------------------
    private String urlBridge1;
    private String urlBridge2;
    private String urlBridge3;
    private String urlBridge4;


    // Links zu den Ressourcen jeder Lampe oder der ganzen Gruppe -----------------
    private String ressourceLamp1;
    private String ressourceLamp2;
    private String ressourceLamp3;
    private String ressourceGroup;


    // Konstruktor ----------------------------------------------------------------
    GoNowAlert() {
        try {
            InetAddress addrBridge1 = InetAddress.getByName(IP_ADRESS_LAMPS_BRIDGE1);
            InetAddress addrBridge2 = InetAddress.getByName(IP_ADRESS_LAMPS_BRIDGE2);
            InetAddress addrBridge3 = InetAddress.getByName(IP_ADRESS_LAMPS_BRIDGE3);
            InetAddress addrBridge4 = InetAddress.getByName(IP_ADRESS_LAMPS_BRIDGE4);

            // Hier wird der URl-String jeder Bridge aus einzelteilen zusammengesetzt
            urlBridge1 = "http:/" + addrBridge1 + "/api/" + AUTH_USER_BRIDGE1;
            urlBridge2 = "http:/" + addrBridge2 + "/api/" + AUTH_USER_BRIDGE2;
            urlBridge3 = "http:/" + addrBridge3 + "/api/" + AUTH_USER_BRIDGE3;
            urlBridge4 = "http:/" + addrBridge4 + "/api/" + AUTH_USER_BRIDGE4;

            ressourceLamp1 = "/lights/1/state";
            ressourceLamp2 = "/lights/2/state";
            ressourceLamp3 = "/lights/3/state";
            ressourceGroup = "/groups/0/action";
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }


    /*
     * Steuerung der Lampen:
     * Grundsätzlich werden die Lampen durch die Methode setControll() von außen angesprochen.
     * Dieser Methode müssen 3 Integer-Werte übergeben werden:
     * setControll(int numberGroup, int numberLamp, int action)
     * 1.)  numberGroup:
     *      Gibt die Nummer der Gruppe an, die angesteuert werden soll.
     *      Ist der Wert 0, so sollen alle Gruppen gleichzeitig angesprochen werden und
     *      der Wert von numberLamp spielt keine Rolle mehr.
     *      Zulässige Werte: numberGroup e [0,4]
     *
     * 2.)  numberLamp:
     *      Gibt die Nummer der Lampe an, die Angesprochen werden soll.
     *      Ist der Wert 0 und der Wert von numberGroup != 0, so soll eine bestimmte ganze
     *      Lampengruppe gleichzeitig angesprochen werden.
     *      Zulässige Werte: numberLamp e [0,3]
     *
     * 3.)  action:
     *      Gibt die Aktion an, die die Lampe(n) ausführen sollen:
     *      action = 0 -> die Lampe soll ausgeschalten werden
     *      action = 1 -> die Lampe soll angeschalten werden
     *      action = 2 -> die Lampe soll in den Alarm-Modus
     *      action = 3 -> der Alarm-Modus der Lampe soll beendet werden
     *      Zulässige Werte: action e [0,3]
     *
     * Ein zulässiges Integer-Kommando als Parameter für setControl() sieht beispielsweise so aus:
     *      (0,0,0) ->  Alle Lampen in allen Gruppen ausschalten
     *      (0,0,1) ->  Alle Lampen in allen Gruppen anschalten
     *      (1,0,2) ->  Alle Lampen in Gruppe 1 in den Alarm-Modus schalten
     *      (1,0,3) ->  Den Alarm-Modus für alle Lampen in Gruppe 1 beenden
     */


    // Methode entscheidet, welche Lampensteuermethode ausgewählt wird ------------
    void setControll(int numberGroup, int numberLamp, int action) {
        numberOfGroup = numberGroup;
        numberOfLamp = numberLamp;
        actionStatus = action;

        if (numberGroup == 0) {
            setAllLampsInAllGroups();
        }
        else if (numberOfLamp == 0) {
            setOneGroupOfLamps();
        }
        else {
            setOnlyOneLamp();
        }
    }


    // Methode findet, addressiert und steuert genau 1 Lampe in einer Gruppe ------
    private void setOnlyOneLamp() {
        String url;
        String action;
        switch(numberOfGroup) {
            case 1:
                url = urlBridge1;
                if (numberOfLamp == 1) {
                    url += ressourceLamp1;
                }
                else if (numberOfLamp == 2) {
                    url += ressourceLamp2;
                }
                else {
                    url += ressourceLamp3;
                }
                break;
            case 2:
                url = urlBridge2;
                if (numberOfLamp == 1) {
                    url += ressourceLamp1;
                }
                else if (numberOfLamp == 2) {
                    url += ressourceLamp2;
                }
                else {
                    url += ressourceLamp3;
                }
                break;
            case 3:
                url = urlBridge3;
                if (numberOfLamp == 1) {
                    url += ressourceLamp1;
                }
                else if (numberOfLamp == 2) {
                    url += ressourceLamp2;
                }
                else {
                    url += ressourceLamp3;
                }
                break;
            default:
                url = urlBridge4;
                if (numberOfLamp == 1) {
                    url += ressourceLamp1;
                }
                else if (numberOfLamp == 2) {
                    url += ressourceLamp2;
                }
                else {
                    url += ressourceLamp3;
                }
        }
        if (actionStatus == 0) {
            action = SET_OFF;
        }
        else if (actionStatus == 1) {
            action = SET_ON;
        }
        else if (actionStatus == 2) {
            action = SET_ALARM_ON;
        }
        else {
            action = SET_ALARM_OFF;
        }
        sendCommandos(url, action);
    }


    // Methode findet, addressiert und steuert genau 1 ganze Gruppe ---------------
    private void setOneGroupOfLamps() {
        String url;
        String action;
        switch (numberOfGroup) {
            case 1:
                url = urlBridge1;
                break;
            case 2:
                url = urlBridge2;
                break;
            case 3:
                url = urlBridge3;
                break;
            default:
                url = urlBridge4;
        }
        url += ressourceGroup;
        System.out.println(url);
        if (actionStatus == 0) {
            action = SET_OFF;
        }
        else if (actionStatus == 1) {
            action = SET_ON;
        }
        else if (actionStatus == 2) {
            action = SET_ALARM_ON;
        }
        else {
            action = SET_ALARM_OFF;
        }
        sendCommandos(url, action);
    }


    // Methode addressiert und steuert alle Gruppen auf einmal --------------------
    private void setAllLampsInAllGroups() {
        String url1 = urlBridge1 + ressourceGroup;
        String url2 = urlBridge2 + ressourceGroup;
        String url3 = urlBridge3 + ressourceGroup;
        String url4 = urlBridge4 + ressourceGroup;
        String action;
        if (actionStatus == 0) {
            action = SET_OFF;
        }
        else if (actionStatus == 1) {
            action = SET_ON;
        }
        else if (actionStatus == 2) {
            action = SET_ALARM_ON;
        }
        else {
            action = SET_ALARM_OFF;
        }
        sendCommandos(url1, action);
        sendCommandos(url2, action);
        sendCommandos(url3, action);
        sendCommandos(url4, action);
    }


    // Methode stellt PUT-Verbindung zu den Lampen her ----------------------------
    private void sendCommandos(String urlString, String commando) {
        try {
            // Hier wird aus dem URL-String ein URL-Objekt erzeugt
            URL url = new URL(urlString);
            // Öffnet die Verbindung zur angegebenen URL
            HttpURLConnection hueLampsBridge = (HttpURLConnection) url.openConnection();
            // Wird benötigt, um PUT-Requests per URL zu senden
            hueLampsBridge.setDoOutput(true);
            // Einstellen der Methode (kann auch DELETE oder GET sein)
            hueLampsBridge.setRequestMethod("PUT");
            // Schreibt die Daten auf den Output-Stream
            OutputStreamWriter out = new OutputStreamWriter(hueLampsBridge.getOutputStream());

            // Hier muss der HUE-Befehl gesetzt werden
            out.write(commando);

            // Schließen aller offenen Streams
            out.close();

            // Lesen des Server-Inputs mit Scanner
            hueLampsBridge.getInputStream();
            Scanner in = new Scanner(hueLampsBridge.getInputStream());

            // Ausgabe des Input-Streams der Hue-Bridge
            System.out.println("Server says " + in.nextLine());
        }
        // Exception-Handling
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
