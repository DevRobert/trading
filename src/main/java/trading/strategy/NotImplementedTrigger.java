package trading.strategy;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NotImplementedTrigger implements Trigger {
    public NotImplementedTrigger() {
        throw new NotImplementedException();
    }

    @Override
    public boolean checkFires() {
        throw new NotImplementedException();
    }
}
