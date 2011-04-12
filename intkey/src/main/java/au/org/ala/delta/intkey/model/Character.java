package au.org.ala.delta.intkey.model;


public abstract class Character {

    private String _description;
    private IntkeyCharacterType _type;
    private double reliablity;
    
    public String getDescription() {
        return _description;
    }
    
    public IntkeyCharacterType getType() {
        return _type;
    }
    
    void setDescription(String description) {
        this._description = description;
    }
    
    void setType(IntkeyCharacterType type) {
        this._type = type;
    }
    
    @Override
    public String toString() {
        return _description;
    }

    public double getReliablity() {
        return reliablity;
    }

    void setReliablity(double reliablity) {
        this.reliablity = reliablity;
    }
    
    
}
