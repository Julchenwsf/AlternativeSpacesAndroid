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
        return other instanceof Interest && this.id.equals(((Interest) other).getId());
    }
}
