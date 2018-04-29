package trading.persistence.market;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;
import trading.domain.market.MarketPriceSnapshotBuilder;
import trading.domain.simulation.MultiStockMarketDataStore;

import java.util.ArrayList;
import java.util.List;

public class MongoMultiStockMarketDataStore implements MultiStockMarketDataStore {
    private final MongoMultiStockMarketDataStoreParameters parameters;

    public MongoMultiStockMarketDataStore(MongoMultiStockMarketDataStoreParameters parameters) {
        if(parameters == null) {
            throw new RuntimeException("The parameters must not be null.");
        }

        this.parameters = parameters;
    }

    @Override
    public List<MarketPriceSnapshot> getAllClosingPrices() {
       MongoClient client = new MongoClient();

        try {
            MongoDatabase database = client.getDatabase(this.parameters.getDatabase());
            MongoCollection<Document> collection = database.getCollection(this.parameters.getCollection());

            Bson filter = new BasicDBObject();
            Bson sort = new BasicDBObject("_id", 1);

            FindIterable<Document> documents = collection.find(filter).sort(sort);

            List<MarketPriceSnapshot> allMarketPriceSnapshots = new ArrayList<>();

            for(Document document: documents) {
                MarketPriceSnapshot marketPriceSnapshot = this.readMarketPriceSnapshotFromDocument(document);
                allMarketPriceSnapshots.add(marketPriceSnapshot);
            }

            return allMarketPriceSnapshots;
        }
        finally {
            client.close();
        }
    }

    @Override
    public MarketPriceSnapshot getLastClosingPrices() {
        MongoClient client = new MongoClient();

        try {
            MongoDatabase database = client.getDatabase(this.parameters.getDatabase());
            MongoCollection<Document> collection = database.getCollection(this.parameters.getCollection());

            Bson filter = new BasicDBObject();
            Bson sort = new BasicDBObject("_id", -1);

            Document document = collection.find(filter).sort(sort).limit(1).first();
            return this.readMarketPriceSnapshotFromDocument(document);
        }
        finally {
            client.close();
        }
    }

    private MarketPriceSnapshot readMarketPriceSnapshotFromDocument(Document document) {
        MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

        Document quotesDocument = (Document) document.get("stocks");

        for(String isinText: quotesDocument.keySet()) {
            Document stockDocument = (Document) quotesDocument.get(isinText);

            Object value = stockDocument.get("close");
            double closingPrice;

            if(value instanceof Integer) {
                closingPrice = ((Integer) value).doubleValue();
            }
            else {
                closingPrice = (double) value;
            }

            marketPriceSnapshotBuilder.setMarketPrice(new ISIN(isinText), new Amount(closingPrice));
        }

        return marketPriceSnapshotBuilder.build();
    }
}
