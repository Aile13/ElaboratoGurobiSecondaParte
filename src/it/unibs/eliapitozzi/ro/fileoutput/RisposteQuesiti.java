package it.unibs.eliapitozzi.ro.fileoutput;

import gurobi.*;
import it.unibs.eliapitozzi.ro.defproblema.DatiProblema;
import org.ejml.simple.SimpleMatrix;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
            writer.printf("funzione obiettivo = %.04f\n", model.get(GRB.DoubleAttr.ObjVal));

            // Ciclo sui k elementi del vettore vars, ovvero le mie var originali
            writer.print("soluzione di base ottima: [");
            // Ciclo sulle var originali
            for (GRBVar var : model.getVars()) {
                writer.printf("%.04f, ", Math.abs(var.get(GRB.DoubleAttr.X)));
            }

           /* // Ciclo sulle var di slack
            for (int i = 0; i < datiPb.getM() - 1; i++) {
                writer.printf("%.04f, ", Math.abs(model.getConstr(i).get(GRB.DoubleAttr.Slack)));
            }
            writer.printf("%.04f]\n", Math.abs(model.getConstr(datiPb.getM() - 1).get(GRB.DoubleAttr.Slack)));
*/

            // Stampa risposta quesito 2
            writer.println("\nQUESITO II:");

            // Variabili in bose
            writer.print("varibili in base: [");
            // Variabili originali in base o meno
            for (GRBVar var : model.getVars()) {
                writer.print(var.get(GRB.IntAttr.VBasis) == 0 ? "1, " : "0, ");
            }
            /*// Variabili slack in base o meno
            for (int i = 0; i < datiPb.getM() - 1; i++) {
                writer.print(model.getConstr(i).get(GRB.IntAttr.CBasis) == 0 ? "1, " : "0, ");
            }
            writer.print(model.getConstr(datiPb.getM() - 1)
                    .get(GRB.IntAttr.CBasis) == 0 ? "1]\n" : "0]\n");
*/

            // CCR
            writer.print("coefficienti di costo ridotto: [");
            // Var originali
            for (GRBVar var : model.getVars()) {
                writer.printf("%.04f, ", Math.abs(var.get(GRB.DoubleAttr.RC)));
            }
            // Var slack
            /*for (int i = 0; i < datiPb.getM() - 1; i++) {
                writer.printf("%.04f, ", Math.abs(model.getConstr(i).get(GRB.DoubleAttr.Pi)));
            }
            writer.printf("%.04f]\n", Math.abs(model.getConstr(datiPb.getM() - 1).get(GRB.DoubleAttr.Pi)));
*/

            // Soluzione ottima multipla
            writer.print("soluzione ottima multipla: ");
            // Controllo se c'è una var non in base con CCR nullo
            boolean multipla = false;

            // Ciclo su tutte le var, guardo tra quelle non in base
            // se loro CCR è nullo

            // Per var originali
            for (GRBVar var : model.getVars()) {
                if (var.get(GRB.IntAttr.VBasis) != 0) {
                    if (var.get(GRB.DoubleAttr.RC) == 0.) {
                        multipla = true;
                        break;
                    }
                }
            }
            //Per var di slack
            for (GRBConstr constr : model.getConstrs()) {
                if (constr.get(GRB.IntAttr.CBasis) != 0) {
                    if (constr.get(GRB.DoubleAttr.Pi) == 0.) {
                        multipla = true;
                        break;
                    }
                }
            }

            writer.println(multipla ? "Sì" : "No");


            // Soluzione ottima degenere
            writer.print("soluzione ottima degenere: ");
            // Controllo se c'è una var in base nulla.
            boolean degenere = false;

            // Ciclo su tutte le var, guardo tra quelle in base se nulle
            // Per var originali
            for (GRBVar var : model.getVars()) {
                if (var.get(GRB.IntAttr.VBasis) == 0) {
                    if (var.get(GRB.DoubleAttr.X) == 0.) {
                        degenere = true;
                        break;
                    }
                }
            }
            // Per var di slack
            for (GRBConstr constr : model.getConstrs()) {
                if (constr.get(GRB.IntAttr.CBasis) == 0) {
                    if (constr.get(GRB.DoubleAttr.Slack) == 0.) {
                        degenere = true;
                        break;
                    }
                }
            }
            writer.println(degenere ? "Sì" : "No");


            // Vincoli al vertice all'ottimo
            writer.print("vincoli vertice ottimo: ");
            // Se la var di slack del vincolo corrente è 0,
            // allora il vincolo identifica il vertice ottimo
            List<String> vincoliOttimo = new ArrayList<>();
            for (GRBConstr constr : model.getConstrs()) {
                if (constr.get(GRB.DoubleAttr.Slack) == 0.) {
                    vincoliOttimo.add(constr.get(GRB.StringAttr.ConstrName));
                }
            }
            writer.println(vincoliOttimo);


            // Stampa risposta quesito 3
            //writer.println("\nQUESITO III:");

           /* // Estraggo matrice A, m x ( k + m ), vincoli x variabili pb in f. standard
            SimpleMatrix a = new SimpleMatrix(datiPb.getM(), datiPb.getK() + datiPb.getM());
            for (int i = 0; i < a.numRows(); i++) {
                for (int nVar = 0; nVar < model.getRow(model.getConstr(i)).size(); nVar++) {
                    int j = model.getRow(model.getConstr(i)).getVar(nVar).index();
                    a.set(i, j, model.getCoeff(model.getConstr(i), model.getVar(j)));
                }
                char sense = model.getConstr(i).get(GRB.CharAttr.Sense);
                double coeff;
                if (sense == '>') {
                    coeff = -1.;
                } else {
                    coeff = 1.;
                }
                a.set(i, datiPb.getK() + i, coeff);
            }

            // Estraggo matrice b, m x 1
            SimpleMatrix b = new SimpleMatrix(datiPb.getM(), 1);
            for (int i = 0; i < a.numRows(); i++) {
                b.set(i, 0, model.getConstr(i).get(GRB.DoubleAttr.RHS));
            }

            // Estraggo matrice C, 1 x ( k + m )
            SimpleMatrix c = new SimpleMatrix(1 , datiPb.getK() + datiPb.getM());
            for (int i = 0; i < datiPb.getK(); i++) {
                c.set(0, i, model.getVar(i).get(GRB.DoubleAttr.RC));
            }
            for (int i = 0; i < datiPb.getM(); i++) {
                c.set(0, datiPb.getK() + i, Math.abs(model.getConstr(i).get(GRB.DoubleAttr.Pi)));
            }
*/

            // Chiudo writer
            writer.close();

        } catch (FileNotFoundException | GRBException e) {
            e.printStackTrace();
        }
    }

}
