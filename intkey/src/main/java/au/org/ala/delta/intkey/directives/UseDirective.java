package au.org.ala.delta.intkey.directives;

import java.util.regex.Pattern;

public class UseDirective extends IntkeyDirective {
    
    private static Pattern COMMA_SEPARATED_VALUE_PATTERN = Pattern.compile("^.+,.*$");
    
    public UseDirective() {
        super("use");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        System.out.println("IN USE DIRECTIVE: " + data);
        /*String[] pieces = data.split(" ");
        
        boolean suppressAlreadySetWarning = false;
        
        for (String piece: pieces) {
            Object characters = null;
            Object values;
            
            // switch
            if (piece.equalsIgnoreCase("/M")) {
                suppressAlreadySetWarning = true;
            // comma separated value: c,v    
            } else if (COMMA_SEPARATED_VALUE_PATTERN.matcher(piece).matches()) {
                String lhs = null;
                String rhs = null;
                
                String[] innerPieces = piece.split(",");
                
                lhs = innerPieces[0];
                
                if (innerPieces.length == 2) {
                    rhs = innerPieces[1];
                }
                
                // lhs can be:
                // range
                // keyword
                // integer
                
                // rhs can be:
                // integer
                // 
            } else if (true) {
                //range
            } else {
                //keyword - any restriction on characters that can go into a keyword?
                //Must be wrapped in quotes if string contains spaces
            }
            
            
            
            
        }*/
        
        // TODO Auto-generated method stub
        
        // INITALIZE
        
        // PROCESS CHARACTERS WITH ATTRIBUTES FIRST
        // for each character specified
        //      process controlling characters of the character (dataset.cc_process)
        //      use character
        
        // PROCESS CHARACTERS WITHOUT ATTRIBUTES NEXT
        
        return null;
    }
    
    class UseDirectiveInvocation implements IntkeyDirectiveInvocation {

        @Override
        public void execute(IntkeyContext context) {
            // TODO Auto-generated method stub
            
        }
        
    }

}
