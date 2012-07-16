package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import au.org.ala.delta.intkey.directives.invocation.DirectiveInvocationProgressHandler;
import au.org.ala.delta.intkey.directives.invocation.LongRunningIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class LongRunningDirectiveSwingWorker extends SwingWorker<Object, String> {

    private LongRunningIntkeyDirectiveInvocation<?> _invoc;
    private IntkeyContext _context;
    private IntkeyUI _appUI;
    private int _executedDirectivesIndex;

    public LongRunningDirectiveSwingWorker(LongRunningIntkeyDirectiveInvocation<?> invoc, IntkeyContext context, IntkeyUI ui, int executedDirectivesIndex) {
        _invoc = invoc;
        _context = context;
        _executedDirectivesIndex = executedDirectivesIndex;
        _appUI = ui;
    }

    @Override
    protected Object doInBackground() throws Exception {
        DirectiveInvocationProgressHandler progressHandler = new DirectiveInvocationProgressHandler() {

            @Override
            public void progress(String message) {
                List<String> messageInList = new ArrayList<String>();
                messageInList.add(message);
                process(messageInList);
            }
        };

        return _invoc.runInBackground(_context, progressHandler);
    }

    @Override
    protected void process(List<String> chunks) {
        // We are only interested in the latest progress message
        _appUI.displayBusyMessage(chunks.get(chunks.size() - 1));
    }

    @Override
    protected void done() {
        try {
            _appUI.removeBusyMessage();
            Object result = get();
            _invoc.done(_context, result);
            _context.handleDirectiveExecutionComplete(_invoc, _executedDirectivesIndex);
        } catch (Exception ex) {
            _appUI.displayErrorMessage(ex.getMessage());
        }
    }

}
