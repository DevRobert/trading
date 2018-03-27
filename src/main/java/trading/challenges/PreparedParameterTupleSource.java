package trading.challenges;

import java.util.Iterator;
import java.util.List;

public class PreparedParameterTupleSource implements ParameterTupleSource {
    private final List<Object[]> list;

    public PreparedParameterTupleSource(List<Object[]> list) {
        this.list = list;
    }

    @Override
    public Iterator<Object[]> getIterator() {
        return this.list.iterator();
    }

    @Override
    public int size() {
        return this.list.size();
    }
}
