package it.unibs.eliapitozzi.ro.fileoutput;

import gurobi.*;
import it.unibs.eliapitozzi.ro.defproblema.DatiProblema;
import it.unibs.eliapitozzi.ro.defproblema.GridConfModelItem;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Scrive il file txt con le risposte ai quesiti
 *
 * @author Elia Pitozzi
 */
public class RisposteQuesiti {
    private static final String PATH_ANSWER_FILE = "risposte_gruppo55.txt";
    private final GRBModel model;
    private final DatiProblema datiPb;

    public RisposteQuesiti(GRBModel model, DatiProblema datiProblema) {
        this.model = model;
        this.datiPb = datiProblema;
    }

    public void stampaFileRisposte() {
        try {
            PrintWriter writer = new PrintWriter(PATH_ANSWER_FILE);

            // Stampa intestazione
            writer.println(""); // Salto una riga
            writer.println("GRUPPO 55");
            writer.println("Componenti: Pitozzi");
            writer.println("\n");


            // Stampa risposta quesito 1
            writer.println("QUESITO I:");
            writer.printf("funzione obiettivo = %d\n", Math.round(model.get(GRB.DoubleAttr.ObjVal)));

            // Ciclo sulle prime m * n variabili, ovvero le mie var bin associate
            // all'assegnamento di una conf per filiale. Quindi operata una approssimazione all'intero,
            // verifico quale sia quella a 1, e stampa relativo profitto associato.
            for (int i = 0; i < datiPb.getM() * datiPb.getN(); i++) {
                GRBVar var = model.getVar(i);
                long valoreVar = Math.round(var.get(GRB.DoubleAttr.X));
                if (valoreVar == 1) {
                    int numFiliale = i / datiPb.getN();
                    List<GridConfModelItem> listConf = datiPb.getGrid().getConfItemsListByFilialeNum(numFiliale);
                    int numConf = i % datiPb.getN();
                    int profittoAssociato = listConf.get(numConf).getQ();
                    writer.printf("filiale p_%d: %d\n", numFiliale + 1, profittoAssociato);
                }
            }

            // Stampa numero processori inutilizzati e GB inutilizzati
            // Ciclo sulle varie conf scelte e poi conto i processori usati e GB usati
            // Per sottrazione con quelli in grid ottengo quelli non utilizzati
            int numProcUsati=0;
            int numGBMemUsati=0;

            for (int i = 0; i < datiPb.getM() * datiPb.getN(); i++) {
                GRBVar var = model.getVar(i);
                long valoreVar = Math.round(var.get(GRB.DoubleAttr.X));
                if (valoreVar == 1) {
                    int numFiliale = i / datiPb.getN();
                    List<GridConfModelItem> listConf = datiPb.getGrid().getConfItemsListByFilialeNum(numFiliale);
                    int numConf = i % datiPb.getN();

                    numProcUsati += listConf.get(numConf).getA();
                    numGBMemUsati += listConf.get(numConf).getB();
                }
            }

            int numProcInutilizzati = datiPb.getK() - numProcUsati;
            int numGBMemInutilizzati = datiPb.getG() - numGBMemUsati;

            writer.printf("processori inutilizzati = %d\n", numProcInutilizzati);
            writer.printf("GB inutilizzati = %d\n", numGBMemInutilizzati);



            // Salto riga tra risposta ad un quesito e l'altro.
            writer.println();

            // Stampa risposta quesito 2
            writer.println("QUESITO II:");

            // Verifico se ci sono eventualmente altre soluzioni disponibili oltre a quella trovata
            // Mediante l'attributo di modello SolCount.
            // Se è maggiore di 1 allora ci sono altre soluzioni che posso analizzare,
            // Altrimenti non esistono soluzioni alternative e quindi non faccio niente.

            if (model.get(GRB.IntAttr.SolCount) > 1) {
                // Eventuale analisi di altre soluzioni
                // Verifico il loro valore di funz ob.
                // e guardo se corrispondono ad altra soluzione avendo
                // coefficienti diversi per le var rispetto alla soluzione trovata.
            } else {
                // Caso alternativo di soluzione non trovata,
                // stampo dicitura: "NON ESISTE".

               writer.println("NON ESISTE");
            }




            // Chiudo writer
            writer.close();

        } catch (FileNotFoundException | GRBException e) {
            e.printStackTrace();
        }
    }

}
