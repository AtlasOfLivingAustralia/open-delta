package au.org.ala.delta.intkey.model;

public class Taxon {
    
    private String _name;

    public String getName() {
        return _name;
    }

    void setName(String name) {
        this._name = name;
    }

    @Override
    public String toString() {
        return _name;
    }
    
}
