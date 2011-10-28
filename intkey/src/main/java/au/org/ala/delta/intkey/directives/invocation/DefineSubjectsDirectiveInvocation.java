package au.org.ala.delta.intkey.directives.invocation;

import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineSubjectsDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String subjects;

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        List<String> subjectsList = Arrays.asList(subjects.split("\\s"));
        return true;
    }

}
