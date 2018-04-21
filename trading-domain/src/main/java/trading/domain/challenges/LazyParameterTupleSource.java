package trading.domain.challenges;

import java.util.Iterator;
import java.util.List;

/**
 * The lazy parameter tuple source combines multiple parameter value lists
 * and exposes all possible combinations via the iterator interface. The parameter
 * tuples are generated on demand.
 *
 * The lazy parameter tuple source should be used for simulations with many parameters
 * as it optimizes the memory consumption compared to the prepared parameter tuple source.
 */
public class LazyParameterTupleSource implements ParameterTupleSource {
    private final List<List<Object>> parameterLists;

    public LazyParameterTupleSource(List<List<Object>> parameterLists) {
        this.parameterLists = parameterLists;
    }

    @Override
    public Iterator<Object[]> getIterator() {
        return new Iterator<Object[]>() {
            private int[] currentPosition = new int[parameterLists.size()];
            private boolean first = true;

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

                Object[] result = new Object[parameterLists.size()];

                for(int parameterIndex = 0; parameterIndex < parameterLists.size(); parameterIndex++) {
                    List<Object> parameterValues = parameterLists.get(parameterIndex);
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

                for(int parameterIndex = parameterLists.size() - 1; parameterIndex >= 0 ; parameterIndex--) {
                    int currentParameterPosition = this.currentPosition[parameterIndex];
                    int maxParameterPosition = parameterLists.get(parameterIndex).size() - 1;

                    if(currentParameterPosition < maxParameterPosition) {
                        result[parameterIndex] = currentParameterPosition + 1;

                        for(int subsequentParameterIndex = parameterIndex + 1; subsequentParameterIndex < parameterLists.size(); subsequentParameterIndex++) {
                            result[subsequentParameterIndex] = 0;
                        }

                        return result;
                    }
                }

                return null;
            }
        };
    }

    public int size() {
        int result = 1;

        for(int parameterIndex = 0; parameterIndex < this.parameterLists.size(); parameterIndex++) {
            result = result * this.parameterLists.get(parameterIndex).size();
        }

        return result;
    }
}
