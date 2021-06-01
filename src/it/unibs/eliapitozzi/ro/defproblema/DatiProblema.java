package it.unibs.eliapitozzi.ro.defproblema;

import java.io.*;
import java.util.List;

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
     * percUsoGrid, percentuale uso di risorse grid da imporre in vincolo;
     * Poi modello riassunti conf di processi.
     */

    private int m;
    private int n;
    private int k;
    private int g;
    private double percUsoGrid = 0.90;
    private GridConfModel grid = new GridConfModel();

    /**
     * Costruisce l'istanza di modello dalla lettura del file fornito.
     * E elabora il corrispondente problema in formato lp.
     */
    public DatiProblema() {
        leggiEEstraiDati();
        elaboraFileModelloLP();
    }

    public int getG() {
        return g;
    }

    public int getK() {
        return k;
    }

    public GridConfModel getGrid() {
        return grid;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    private void elaboraFileModelloLP() {

        PrintWriter w;
        try {
            w = new PrintWriter(MODEL_FILE_PATH);

            // Stampa descrizione file
            w.println("\\ LP file per definizione problema di elaborato");
            w.println("\\ Variabili tutte intere o binarie");


            // Stampa def problema in formato lp

            // Stampa funzione obiettivo
            w.println("\nMaximize");
            w.print("  "); // per migliore formattazione

            List<GridConfModelItem> listConf;

            for (int i = 0; i < m - 1; i++) {
                listConf = grid.getConfItemsListByFilialeNum(i);
                for (int j = 0; j < n; j++) {
                    w.printf("%d c_%d_%d + ", listConf.get(j).getQ(), i + 1, j + 1);
                }
            }

            listConf = grid.getConfItemsListByFilialeNum(m - 1);
            for (int j = 0; j < n - 1; j++) {
                w.printf("%d c_%d_%d + ", listConf.get(j).getQ(), m, j + 1);
            }
            w.printf("%d c_%d_%d\n", listConf.get(n - 1).getQ(), m, n);


            // Aggiungo vincoli
            w.println("\nSubject To");

            // Vincolo di scelta singola conf per ogni filiale, una riga per ogni filiale
            for (int i = 0; i < m; i++) {
                w.printf("  v_sing_conf_per_filiale_%d: ", i + 1);
                for (int j = 0; j < n - 1; j++) {
                    w.printf("c_%d_%d + ", i + 1, j + 1);
                }
                w.printf("c_%d_%d = 1\n", i + 1, n);
            }


            // Salto riga tra un tipo di vincolo e un altro
            w.println();

            // Vincoli di utilizzo massimo delle risorse di grid,
            // le filiali nel complesso non posso usare più
            // del quantitativo di risorse messe a disposizione dalla grid

            // Per num di cpu
            w.print("  v_max_uso_risorse_grid_cpu: ");
            for (int i = 0; i < m - 1; i++) {
                listConf = grid.getConfItemsListByFilialeNum(i);
                for (int j = 0; j < n; j++) {
                    w.printf("%d c_%d_%d + ", listConf.get(j).getA(), i + 1, j + 1);
                }
            }

            listConf = grid.getConfItemsListByFilialeNum(m - 1);
            for (int j = 0; j < n - 1; j++) {
                w.printf("%d c_%d_%d + ", listConf.get(j).getA(), m, j + 1);
            }
            w.printf("%d c_%d_%d <= %d\n", listConf.get(n - 1).getA(), m, n, k);


            // Per num di GB di mem
            // Salto riga tra un tipo di vincolo e un altro
            w.println();
            w.print("  v_max_uso_risorse_grid_mem: ");

            for (int i = 0; i < m - 1; i++) {
                listConf = grid.getConfItemsListByFilialeNum(i);
                for (int j = 0; j < n; j++) {
                    w.printf("%d c_%d_%d + ", listConf.get(j).getB(), i + 1, j + 1);
                }
            }

            listConf = grid.getConfItemsListByFilialeNum(m - 1);
            for (int j = 0; j < n - 1; j++) {
                w.printf("%d c_%d_%d + ", listConf.get(j).getB(), m, j + 1);
            }
            w.printf("%d c_%d_%d <= %d\n", listConf.get(n - 1).getB(), m, n, g);


            // Salto riga tra un tipo di vincolo e un altro
            w.println();

            // Vincolo di utilizzare almeno una risorsa grid almeno al 90%
            // Uso variabile binaria v_cpu, per attivare o meno anche vincolo su uso memoria
            w.print("  v_uso_90perc_risorse_grid_cpu: ");

            for (int i = 0; i < m - 1; i++) {
                listConf = grid.getConfItemsListByFilialeNum(i);
                for (int j = 0; j < n; j++) {
                    w.printf("%d c_%d_%d + ", listConf.get(j).getA(), i + 1, j + 1);
                }
            }

            listConf = grid.getConfItemsListByFilialeNum(m - 1);
            for (int j = 0; j < n - 1; j++) {
                w.printf("%d c_%d_%d + ", listConf.get(j).getA(), m, j + 1);
            }
            w.printf("%d c_%d_%d - %.02f v_cpu >= 0\n", listConf.get(n - 1).getA(), m, n, k * percUsoGrid);


            // Salto riga tra un tipo di vincolo e un altro
            w.println();

            w.print("  v_uso_90perc_risorse_grid_mem: ");

            for (int i = 0; i < m - 1; i++) {
                listConf = grid.getConfItemsListByFilialeNum(i);
                for (int j = 0; j < n; j++) {
                    w.printf("%d c_%d_%d + ", listConf.get(j).getB(), i + 1, j + 1);
                }
            }

            listConf = grid.getConfItemsListByFilialeNum(m - 1);
            for (int j = 0; j < n - 1; j++) {
                w.printf("%d c_%d_%d + ", listConf.get(j).getB(), m, j + 1);
            }
            w.printf("%d c_%d_%d - %.02f v_mem >= 0\n", listConf.get(n - 1).getB(), m, n, g * percUsoGrid);


            // Salto riga tra un tipo di vincolo e un altro
            w.println();


            // Vincolo per var binaria v_mem, per vincoli disgiuntivi
            w.println("  v_var_bin_disg: v_mem + v_cpu = 1");

            // Salto riga tra un tipo di vincolo e un altro
            w.println();

            // Specifiche di tipo e limitazione intervallo variabili utilizzate
            w.println("Bounds");

            w.println("Binary");
            w.print("  "); // per migliore formattazione

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    w.printf("c_%d_%d ", i + 1, j + 1);
                }
            }
            w.println("v_cpu v_mem");

            // Salto riga
            w.println();

            // Concludo file LP
            w.println("End");

            w.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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

                    grid.addGridModelItemInFilialeNum(
                            i,
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
