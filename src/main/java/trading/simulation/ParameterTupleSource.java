package trading.simulation;

import java.util.Iterator;
import java.util.List;

/**
 * The parameter tuple source combines multiple parameter value lists
 * and exposes all possible combinations via the iterator interface.
 *
 * This is useful for simulations with many parameters in order to
 * generate the actual parameter tuples on demand so that the memory
 * consumption is minimized.
 */
public class ParameterTupleSource implements Iterator<Object[]> {
    private final List<List<Object>> parameterLists;
    private int[] currentPosition;
    private boolean first;

    public ParameterTupleSource(List<List<Object>> parameterLists) {
        this.parameterLists = parameterLists;
        this.currentPosition = new int[this.parameterLists.size()];
        this.first = true;
    }

    @Override
    public boolean hasNext() {
        int[] nextPosition = this.calculateNextPosition();
        return nextPosition != null;
    }

    @Override
    public Object[] next() {
        int[] nextPosition = calculateNextPosition();

        if(nextPosition == null) {
            throw new RuntimeException("No next parameter tuple available.");
        }

        this.currentPosition = nextPosition;
        this.first = false;

        Object[] result = new Object[this.parameterLists.size()];

        for(int parameterIndex = 0; parameterIndex < this.parameterLists.size(); parameterIndex++) {
            List<Object> parameterValues = this.parameterLists.get(parameterIndex);
            int parameterValueIndex = this.currentPosition[parameterIndex];
            Object parameterValue = parameterValues.get(parameterValueIndex);
            result[parameterIndex] = parameterValue;
        }

        return result;
    }

    private int[] calculateNextPosition() {
        if(first) {
            return this.currentPosition;
        }

        int[] result = this.currentPosition.clone();

        for(int parameterIndex = this.parameterLists.size() - 1; parameterIndex >= 0 ; parameterIndex--) {
            int currentParameterPosition = this.currentPosition[parameterIndex];
            int maxParameterPosition = this.parameterLists.get(parameterIndex).size() - 1;

            if(currentParameterPosition < maxParameterPosition) {
                result[parameterIndex] = currentParameterPosition + 1;

                for(int subsequentParameterIndex = parameterIndex + 1; subsequentParameterIndex < this.parameterLists.size(); subsequentParameterIndex++) {
                    result[subsequentParameterIndex] = 0;
                }

                return result;
            }
        }

        return null;
    }

    public int size() {
        int result = 1;

        for(int parameterIndex = 0; parameterIndex < this.parameterLists.size(); parameterIndex++) {
            result = result * this.parameterLists.get(parameterIndex).size();
        }

        return result;
    }
}
