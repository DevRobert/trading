package trading.simulation;

public class MongoMultiStockMarketDataStoreParametersBuilder {
    private String database;
    private String collection;

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public MongoMultiStockMarketDataStoreParameters build() {
        return new MongoMultiStockMarketDataStoreParameters(
                this.database,
                this.collection
        );
    }
}
