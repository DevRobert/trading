package trading.domain.taxes;

public class ProfitCategory {
    private final int id;
    private final String name;

    public ProfitCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
