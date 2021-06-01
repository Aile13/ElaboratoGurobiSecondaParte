package it.unibs.eliapitozzi.ro.defproblema;

/**
 * Modellizza una singola istanza della grid computazionale,
 * aggregando i dati relativi ad una singola istanza di configurazione,
 * relativi ad una singola filiale.
 *
 * a: numero processori richiesti dalla conf.
 * b: numero GB di memoria richiesti dalla canf.
 * q: profitto relativo a tale configurazione.
 *
 * @author Elia
 */
public class GridConfModelItem {
    private final int q;
    private final int a;
    private final int b;

    public GridConfModelItem(int q, int a, int b) {
        this.q = q;
        this.a = a;
        this.b = b;
    }

    public int getQ() {
        return q;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}
