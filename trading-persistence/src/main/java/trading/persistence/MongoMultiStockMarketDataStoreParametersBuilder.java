package trading.persistence;

public class MongoMultiStockMarketDataStoreParametersBuilder {
    private String database;
    private String collection;

    public MongoMultiStockMarketDataStoreParametersBuilder setDatabase(String database) {
        this.database = database;
        return this;
    }

    public MongoMultiStockMarketDataStoreParametersBuilder setCollection(String collection) {
        this.collection = collection;
        return this;
    }

    public MongoMultiStockMarketDataStoreParameters build() {
        return new MongoMultiStockMarketDataStoreParameters(
                this.database,
                this.collection
        );
    }
}
