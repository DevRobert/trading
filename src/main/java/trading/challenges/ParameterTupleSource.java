package trading.challenges;

import java.util.Iterator;

public interface ParameterTupleSource {
    Iterator<Object[]> getIterator();
    int size();
}
