package groupaltspaces.alternativespacesandroid.util;

import java.io.Serializable;

/**
 * Created by BrageEkroll on 14.10.2014.
 */
public class Interest implements Serializable{

    private int id;
    private String name;

    public Interest(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() { return name; }
}
