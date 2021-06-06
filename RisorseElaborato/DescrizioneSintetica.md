# Descrizione Elaborato Gurobi Seconda Parte di Elia Pitozzi

Dopo la lettura del testo, si è passati alla definizione del corrispondente modello matematico di PLI.
Il modello da me scritto e usato è il seguente:

```text
    Max p_tot
    p_tot = som su m con som su n di q_pn * c_pn
    
    som su n di c_pn = 1   // Seleziono esattamente una conf per filiale.
    som su m con som su n di c_pn * a_pn <= k   // vinc max uso di processori.
    som su m con som su n di c_pn * b_pn <= g   // vinc max uso di gb memoria.
    
    som su m con som su n c_pn * a_pn >= k * 0.9 * v_cpu   // vinc uso risorsa al 90%.
    som su m con som su n c_pn * b_pn >= g * 0.9 * (1 - v_cpu)   // vinc uso risorsa al 90%.
    
    c_pn, v_cpu € {0, 1}  per ogni p e n.   // Scelta conf n-esima per p-esima filiale, si=1, no=0.
                                            // E var bin per vincolo disgiuntivo. 
```


Il programma Java che ho realizzato risolve il problema eseguendo questi punti principali:
* Legge i dati del pb dal file _singolo55.txt_.
* Successivamente scrive in maniera automatica il file _problema.lp_ con i dati ricavati, e struttura la funzione obbiettivo, i vincoli e gli estremi come il modello sopra riportato.
* Quest'ultimo file viene usato come input per l'esecuzione del solver **Gurobi**.
* Infine, ricavata la soluzione del modello, si procede alla determinazione delle risposte e al loro output formattato nel file _risposte_gruppo55.txt_.

### Specifiche per l'esecuzione del file _gruppo55.jar_:
* Il file jar per funzionare ha bisogno del file _singolo55.txt_ come input. Pertanto questo file deve essere presente nella cartella **_RisorseElaborato_** disponibile dove si trova il jar.
* Durante l'esecuzione del jar, viene creato il file _problema.lp_ nella cartella delle risorse, questo serve all'evoluzione del programma, in quanto il file è l'input di partenza per la risoluzione del modello da parte del solver Gurobi.
* Durante l'avanzamento del solver viene prodotto nella cartella risorse un relativo file di log chiamato _Gurobi.log_.
* In seguito alla risoluzione del modello e dell'esecuzione del jar, viene creato un file _risposte_gruppo55.txt_, contenente le risposte ai quesiti secondo il formato richiesto.