package at.langhofer.yellowdesks3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arminlanghofer on 19.11.16.
 */

public class Data {

    List<Item> arrayOfList = new ArrayList<Item>();

    private Data() {
        arrayOfList.add(new Item(10,35, "Extras: Highspeed Internet, Küche, Besprechungsraum, Telefonierräume, Garten, Kantine, Kaffee, Barrierefrei. Transportation: O-Bus 3&6 Parken: kostenpflichtig. ", "a", "b", "c"));
        arrayOfList.add(new Item(3,11, "b", "a", "b", "c"));
        arrayOfList.add(new Item(4,12, "b", "a", "b", "c"));
        arrayOfList.add(new Item(5,13, "b", "a", "b", "c"));
        arrayOfList.add(new Item(6,14, "b", "a", "b", "c"));
        arrayOfList.add(new Item(7,15, "b", "a", "b", "c"));
        arrayOfList.add(new Item(8,16, "b", "a", "b", "c"));
        arrayOfList.add(new Item(9,17, "b", "a", "b", "c"));
        arrayOfList.add(new Item(10,18, "b", "a", "b", "c"));
        arrayOfList.add(new Item(11,19, "b", "a", "b", "c"));
    }


    private static Data _instance = null;
    public static Data getInstance() {

        if (_instance == null)
            _instance = new Data();

        return _instance;

    }

    public List<Item> getData() {
        return arrayOfList;
    }
}
