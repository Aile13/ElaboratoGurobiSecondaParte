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
    private final int a;
    private final int b;
    private final int q;

    public GridConfModelItem(int a, int b, int q) {
        this.a = a;
        this.b = b;
        this.q = q;
    }

    @Override
    public String toString() {
        return "GridModelItem{" +
                "a=" + a +
                ", b=" + b +
                ", q=" + q +
                '}';
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getQ() {
        return q;
    }
}
