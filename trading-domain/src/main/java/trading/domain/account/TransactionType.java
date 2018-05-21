package trading.domain.account;

public class TransactionType {
    private final int index;
    private final String name;

    protected TransactionType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static final MarketTransactionType Buy = new MarketTransactionType(0, "Buy");
    public static final MarketTransactionType Sell = new MarketTransactionType(1, "Sell");
    public static final TransactionType Dividend = new TransactionType(2, "Dividend");

    public static TransactionType ofIndex(int index) {
        switch(index) {
            case 0:
                return TransactionType.Buy;

            case 1:
                return TransactionType.Sell;

            case 2:
                return TransactionType.Dividend;

            default:
                throw new RuntimeException("Invalid index.");
        }
    }

    public static TransactionType ofName(String name) {
        switch(name) {
            case "Buy":
                return TransactionType.Buy;

            case "Sell":
                return TransactionType.Sell;

            case "Dividend":
                return TransactionType.Dividend;

            default:
                throw new RuntimeException("Invalid name.");
        }
    }
}
