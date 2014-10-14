package groupaltspaces.alternativespacesandroid.util;

import java.io.Serializable;

public class Interest implements Serializable{
    private String id, name;

    public Interest(String id, String name){
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Interest)
            return this.id.equals(((Interest) other).getId());
        else
            return false;
    }
}
