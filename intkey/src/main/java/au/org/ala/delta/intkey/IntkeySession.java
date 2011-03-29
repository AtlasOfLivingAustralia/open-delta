package au.org.ala.delta.intkey;

/**
 * This class is for holding global application settings. It is a singleton
 * @author Chris
 *
 */
public class IntkeySession {
    
    private static final IntkeySession INSTANCE = new IntkeySession();
    
    private IntkeySession() {}
    
    public static IntkeySession getInstance() {
        return INSTANCE;
    }
    
    public void setFileTaxa(String fileName) {
        System.out.println("Items file set to: " + fileName);
    }

}
