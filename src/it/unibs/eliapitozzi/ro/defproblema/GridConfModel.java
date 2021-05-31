package it.unibs.eliapitozzi.ro.defproblema;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelizza la grid computazionale,
 * tenendo conto di ciascuna delle possibili instanze di conf
 * per l'esecuzione del processore di ciascuna filiale.
 *
 * @author Elia
 */
public class GridConfModel {
    /**
     * Lista più esterna: righe della filiali;
     * Lista più interna: configurazioni possibili per sing processo
     */
    private final List<List<GridConfModelItem>> grid = new ArrayList<>();

    public void addGridModelItemInFilialeNum(int i, GridConfModelItem newItem) {
        if (grid.size() < i + 1) {
            grid.add(new ArrayList<>());
        }
        System.out.println(grid.size());
        grid.get(i).add(newItem);
    }

    public List<GridConfModelItem> getConfItemsListByFilialeNum(int i) {
        return grid.get(i);
    }

    @Override
    public String toString() {
        return "GridConfModel{" +
                "grid=" + grid +
                '}';
    }
}
