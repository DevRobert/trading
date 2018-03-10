package trading.simulation;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import trading.Amount;
import trading.ISIN;
import trading.market.MarketPriceSnapshot;
import trading.market.MarketPriceSnapshotBuilder;

import java.util.ArrayList;
import java.util.List;

public class MongoMultiStockMarketDataStore {
    private final MongoMultiStockMarketDataStoreParameters parameters;

    public MongoMultiStockMarketDataStore(MongoMultiStockMarketDataStoreParameters parameters) {
        if(parameters == null) {
            throw new RuntimeException("The parameters must not be null.");
        }

        this.parameters = parameters;
    }

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
                MarketPriceSnapshotBuilder marketPriceSnapshotBuilder = new MarketPriceSnapshotBuilder();

                Document quotesDocument = (Document) document.get("quotes");

                for(String isinText: quotesDocument.keySet()) {
                    Document stockDocument = (Document) quotesDocument.get(isinText);
                    double closingPrice = stockDocument.getDouble("close");
                    marketPriceSnapshotBuilder.setMarketPrice(new ISIN(isinText), new Amount(closingPrice));
                }

                allMarketPriceSnapshots.add(marketPriceSnapshotBuilder.build());
            }

            return allMarketPriceSnapshots;
        }
        finally {
            client.close();
        }
    }
}
