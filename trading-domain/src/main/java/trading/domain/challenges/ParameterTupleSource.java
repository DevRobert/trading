package trading.domain.challenges;

import java.util.Iterator;

public interface ParameterTupleSource {
    Iterator<Object[]> getIterator();
    int size();
}
