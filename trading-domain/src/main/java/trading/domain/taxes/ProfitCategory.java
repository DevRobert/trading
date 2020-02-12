package trading.domain.taxes;

public class ProfitCategory {
    private final String name;

    public ProfitCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
