package gestorAplicacion;

import java.io.Serializable;

public abstract class WithId implements Serializable {

    private String id;

    protected WithId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}