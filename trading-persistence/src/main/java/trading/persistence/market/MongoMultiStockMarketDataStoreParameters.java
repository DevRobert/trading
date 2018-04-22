package trading.persistence.market;

public class MongoMultiStockMarketDataStoreParameters {
    private String database;
    private String collection;

    public String getDatabase() {
        return database;
    }

    public String getCollection() {
        return collection;
    }

    public MongoMultiStockMarketDataStoreParameters(String database, String collection) {
        if(database == null) {
            throw new RuntimeException("The database has to be specified.");
        }

        if(collection == null) {
            throw new RuntimeException("The collection has to be specified.");
        }

        this.database = database;
        this.collection = collection;
    }
}
