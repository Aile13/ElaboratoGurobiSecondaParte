package it.unibs.eliapitozzi.ro.defproblema;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Legge file txt fornito e costruisce il modello in formato lp.
 *
 * @author Elia
 */
public class DatiProblema {

    private static final String DATA_FILE_PATH = "RisorseElaborato/singolo55.txt";
    private static final String MODEL_FILE_PATH = "RisorseElaborato/problema.lp";

    /**
     * m, numero di filiali;
     * n, numero di configurazioni processo per filiale;
     * <p>
     * k, numero processori totali disponibili in grid;
     * g, numero GB totali di memoria disponibili in grid;
     * <p>
     * Poi modello riassunti conf di processi.
     */

    private int m;
    private int n;
    private int k;
    private int g;
    private GridConfModel grid = new GridConfModel();

    /**
     * Costruisce l'istanza di modello dalla lettura del file fornito.
     * E elabora il corrispondente problema in formato lp.
     */
    public DatiProblema() {
        leggiEEstraiDati();
        // elaboraFileModelloLP();
    }

/*
    private void elaboraFileModelloLP() {

        PrintWriter w;
        try {
            w = new PrintWriter(MODEL_FILE_PATH);

            // Stampa descrizione file
            w.println("\\ LP file per definizione problema ottimizzazione di elaborato");
            w.println("\\ Variabili tutte continue");


            // Stampa def problema in formato lp

            // Stampa funzione obiettivo
            w.println("\nMinimize w\n");

            // Aggiungo vincoli
            w.println("Subject To");

            // Vincolo w >= xi
            for (int i = 0; i < n; i++) {
                String label = String.format("  c_di_w_e_x%d: ", i + 1);
                w.printf(label + "w - x%d >= 0\n", i + 1, i + 1);
            }


            // Salto riga tra un tipo di vincolo e un altro
            w.println();


            // Vincolo estremi percentuali forniti per ogni xi, entro omega e teta
            for (int i = 0; i < n; i++) {
                String labelEstrMin = String.format("  c_di_x%d_e_omega: ", i + 1);
                String labelEstrMax = String.format("  c_di_x%d_e_teta: ", i + 1);

                w.printf(labelEstrMin + "x%d >= %.02f\n", i + 1, omega);
                w.printf(labelEstrMax + "x%d <= %.02f\n", i + 1, teta);
            }


            // Salto riga tra un tipo di vincolo e un altro
            w.println();


            // Vincolo per percentuale totale, tutte le xi insieme danno 100%
            w.print("  c_delle_xi_somma_delle_percentuali:");
            for (int i = 0; i < n - 1; i++) {
                w.printf(" x%d +", i + 1);
            }
            w.printf(" x%d = 100,0\n", n);


            // Salto riga tra un tipo di vincolo e un altro
            w.println();


            // Vincolo temporale, tempo di esecuzione massimo ammesso
            double totDatiInGB = h * g * 1000;

            // Calcolo coefficiente per ogni xi
            // (xi * 100 * totDati / alfa)  +  (xi * 100 * totDati / beta)
            // Ho raccolto 100 e totDati, poi calcolato la somma dei reciproci di alfa e beta
            for (int i = 0; i < n; i++) {

                double coef = totDatiInGB / 100 *
                        (1. / reteComputer.get(i).getAlfa() + 1. / reteComputer.get(i).getBeta());

                String label = String.format("  c_di_x%d_tempo_max_esec:  ", i + 1);

                w.printf(label + "%.02f x%d <= %d\n", coef, i + 1, tau);
            }


            // Salto riga tra un tipo di vincolo e un altro
            w.println();


            // Aggiungo intervallo di validita variabili xi, tra 0 e 100, perchè percentuale
            w.println("Bounds");
            for (int i = 0; i < n; i++) {
                w.printf("  0.0 <= x%d <= 100.0\n", i + 1);
            }

            // Salto riga
            w.println();

            // Concludo file LP
            w.println("End");

            w.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
*/

    private void leggiEEstraiDati() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(DATA_FILE_PATH));

            // Legge e estrae parametri "singoli"
            int[] intsFirstLine = parseInts(in.readLine());
            m = intsFirstLine[0];
            n = intsFirstLine[1];

            int[] intsSecondLine = parseInts(in.readLine());
            k = intsSecondLine[0];
            g = intsSecondLine[1];
            
            // Legge e estrae dati configurazioni
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    int[] valoriConf = parseInts(in.readLine());

                    grid.addGridModelItemInFilialeNum(i,
                            new GridConfModelItem(
                                    valoriConf[0],
                                    valoriConf[1],
                                    valoriConf[2]
                            ));
                }
            }

            in.close();

        } catch (FileNotFoundException e) {
            System.err.println("File non trovato in percorso: " + DATA_FILE_PATH);
        } catch (IOException e) {
            System.err.println("File non leggibile in percorso: " + DATA_FILE_PATH);
        }
    }

    private int[] parseInts(String raw) {
        String[] interiString = raw.split("-");
        int[] interi = new int[3];
        int i = 0;
        for (String s : interiString) {
            interi[i] = Integer.parseInt(s);
            i++;
        }
        return interi;
    }

}
