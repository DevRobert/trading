package trading.domain.simulation;

public class RunParameters {
    private long runIndex;
    private Object[] parameters;

    public RunParameters(long runIndex, Object[] parameters) {
        this.runIndex = runIndex;
        this.parameters = parameters;
    }

    public long getRunIndex() {
        return runIndex;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
