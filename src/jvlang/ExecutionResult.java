package jvlang;

public class ExecutionResult {
    public static final ExecutionResult CONTINUE = new ExecutionResult(null, false);

    public final Object returnValue;
    public final boolean isReturn;

    private ExecutionResult(Object returnValue, boolean isReturn) {
        this.returnValue = returnValue;
        this.isReturn = isReturn;
    }

    public static ExecutionResult returnWith(Object value) {
        return new ExecutionResult(value, true);
    }
}