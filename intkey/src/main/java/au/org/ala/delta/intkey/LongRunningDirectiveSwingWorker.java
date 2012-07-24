package au.org.ala.delta.intkey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.SwingWorker;

import au.org.ala.delta.intkey.directives.invocation.DirectiveInvocationProgressHandler;
import au.org.ala.delta.intkey.directives.invocation.LongRunningIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class LongRunningDirectiveSwingWorker extends SwingWorker<Object, String> {

    private LongRunningIntkeyDirectiveInvocation<?> _invoc;
    private IntkeyContext _context;
    private IntkeyUI _appUI;
    private int _executedDirectivesIndex;
    private int _logInsertionIndex;

    public LongRunningDirectiveSwingWorker(LongRunningIntkeyDirectiveInvocation<?> invoc, IntkeyContext context, IntkeyUI ui, int executedDirectivesIndex, int logInsertionIndex) {
        _invoc = invoc;
        _context = context;
        _executedDirectivesIndex = executedDirectivesIndex;
        _logInsertionIndex = logInsertionIndex;
        _appUI = ui;
    }

    @Override
    protected Object doInBackground() throws Exception {
        DirectiveInvocationProgressHandler progressHandler = new DirectiveInvocationProgressHandler() {

            @Override
            public void progress(String message) {
                // When progress is updated, check if the worker has been
                // cancelled. If it has, throw a CancellationException to
                // stop the background thread. SwingWorker.cancel(true) cannot
                // be used because the open items and characters dataset files
                // can get into an unhappy state if a CancellationException is
                // thrown in the middle of a read.
                if (isCancelled()) {
                    throw new CancellationException();
                }

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
        _appUI.displayBusyMessageAllowCancelWorker(chunks.get(chunks.size() - 1), this);
    }

    @Override
    protected void done() {
        try {
            _appUI.removeBusyMessage();
            Object result = get();
            _invoc.done(_context, result);
            _context.handleDirectiveExecutionComplete(_invoc, _executedDirectivesIndex);
        } catch (CancellationException ex) {
            // worker was cancelled - no action required
            _appUI.removeBusyMessage();
        } catch (Exception ex) {
            ex.printStackTrace();
            _appUI.displayErrorMessage(ex.getMessage());
            _context.handleDirectiveExecutionFailed(_invoc, _logInsertionIndex);
        }
    }

}
