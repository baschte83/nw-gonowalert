/*
 * Created by
 * Justina Kluone,
 * Paul Campean,
 * Sebastian Baumann
 * 24.11.2016
 */

public class GoNowAlertMain {

    // Kommandos
    //private static final String SET_ON = ""

    public static void main(String[] args) {
        GoNowAlert steuerung = new GoNowAlert();

        // Anzahl an anzusteuernden Gruppen
        int numberGroup = Integer.parseInt(args[0]);
        // Anzahl an anzusteuernden Lampen
        int numberLamp = Integer.parseInt(args[1]);
        // Wert gibt an, ob die Lampen an (1) oder aus (0) sein sollen
        int energy = Integer.parseInt(args[2]);

        steuerung.setControll(numberGroup, numberLamp, energy);
    }
}