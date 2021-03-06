package trading.persistence.market;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import trading.domain.Amount;
import trading.domain.ISIN;
import trading.domain.market.MarketPriceSnapshot;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MongoMultiStockMarketDataStoreTest {
    private final static String TestDatabase = "trading";
    private final static String TestCollection = "test-merged-quotes";

    private MongoMultiStockMarketDataStoreParametersBuilder parametersBuilder;

    @Before
    public void before() {
        this.parametersBuilder = new MongoMultiStockMarketDataStoreParametersBuilder();
        this.parametersBuilder.setDatabase(TestDatabase);
        this.parametersBuilder.setCollection(TestCollection);
    }

    @BeforeClass
    public static void initializeTestDatabase() {
        MongoClient client = new MongoClient();

        try {
            MongoDatabase database = client.getDatabase(TestDatabase);
            MongoCollection<Document> collection = database.getCollection(TestCollection);
            collection.deleteMany(new BasicDBObject());

            List<Document> documents = new ArrayList<>();

            // The order of the entries is mixed intentionally in order
            // to test the sort behavior of the data store

            // #1 entry - 01.01.2000

            documents.add(new Document()
                    .append("_id", Date.valueOf(LocalDate.of(2000, 1, 1)))
                    .append("stocks", new BasicBSONObject()
                            .append(ISIN.MunichRe.getText(), new BasicBSONObject()
                                    .append("close", 100.0))
                            .append(ISIN.Allianz.getText(), new BasicBSONObject()
                                    .append("close", 50.0))
                    )
            );

            // #3 entry - 03.01.2000

            documents.add(new Document()
                    .append("_id", Date.valueOf(LocalDate.of(2000, 1, 3)))
                    .append("stocks", new BasicBSONObject()
                            .append(ISIN.MunichRe.getText(), new BasicBSONObject()
                                    .append("close", 102.5))
                            .append(ISIN.Allianz.getText(), new BasicBSONObject()
                                    .append("close", 49.5))
                    )
            );

            // #2 entry - 02.01.2000

            documents.add(new Document()
                    .append("_id", Date.valueOf(LocalDate.of(2000, 1, 2)))
                    .append("stocks", new BasicBSONObject()
                            .append(ISIN.MunichRe.getText(), new BasicBSONObject()
                                    .append("close", 101.5))
                            .append(ISIN.Allianz.getText(), new BasicBSONObject()
                                    .append("close", 49))
                    )
            );

            collection.insertMany(documents);
        } finally {
            client.close();
        }
    }

    private MongoMultiStockMarketDataStore createMongoMultiStockMarketDataStore() {
        MongoMultiStockMarketDataStoreParameters parameters = this.parametersBuilder.build();
        return new MongoMultiStockMarketDataStore(parameters);
    }

    @Test
    public void constructionFailsIfNoParametersSpecifiedAtAll() {
        MongoMultiStockMarketDataStoreParameters parameters = null;

        try {
            new MongoMultiStockMarketDataStore(parameters);
        }
        catch(Exception ex) {
            Assert.assertEquals("The parameters must not be null.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void constructionFailsIfNoDatabaseSpecified() {
        this.parametersBuilder.setDatabase(null);

        try {
            this.createMongoMultiStockMarketDataStore();
        }
        catch(Exception ex) {
            Assert.assertEquals("The database has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void constructionFailsIfNoCollectionSpecified() {
        this.parametersBuilder.setCollection(null);

        try {
            this.createMongoMultiStockMarketDataStore();
        }
        catch(Exception ex) {
            Assert.assertEquals("The collection has to be specified.", ex.getMessage());
            return;
        }

        Assert.fail("Exception expected.");
    }

    @Test
    public void returnsAllQuotesInCorrectOrder() {
        MongoMultiStockMarketDataStore multiStockMongoDataSource = this.createMongoMultiStockMarketDataStore();

        List<MarketPriceSnapshot> marketPriceSnapshots = multiStockMongoDataSource.getAllClosingPrices();

        Assert.assertEquals(3, marketPriceSnapshots.size());

        // 2000-01-01
        MarketPriceSnapshot firstDayMarketPriceSnapshot = marketPriceSnapshots.get(0);
        Assert.assertEquals(new Amount(100.0), firstDayMarketPriceSnapshot.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(50.0), firstDayMarketPriceSnapshot.getMarketPrice(ISIN.Allianz));
        Assert.assertEquals(LocalDate.of(2000, 1, 1), firstDayMarketPriceSnapshot.getDate());

        // 2000-01-02
        MarketPriceSnapshot secondDayMarketPriceSnapshot = marketPriceSnapshots.get(1);
        Assert.assertEquals(new Amount(101.5), secondDayMarketPriceSnapshot.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(49.0), secondDayMarketPriceSnapshot.getMarketPrice(ISIN.Allianz));
        Assert.assertEquals(LocalDate.of(2000, 1, 2), secondDayMarketPriceSnapshot.getDate());

        // 2000-01-03
        MarketPriceSnapshot thirdDayMarketPriceSnapshot = marketPriceSnapshots.get(2);
        Assert.assertEquals(new Amount(102.5), thirdDayMarketPriceSnapshot.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(49.5), thirdDayMarketPriceSnapshot.getMarketPrice(ISIN.Allianz));
        Assert.assertEquals(LocalDate.of(2000, 1, 3), thirdDayMarketPriceSnapshot.getDate());
    }

    @Test
    public void returnsLastClosingPrices() {
        MongoMultiStockMarketDataStore mongoMultiStockMarketDataStore = this.createMongoMultiStockMarketDataStore();

        MarketPriceSnapshot lastClosingPrices = mongoMultiStockMarketDataStore.getLastClosingPrices();

        Assert.assertNotNull(lastClosingPrices);
        Assert.assertEquals(new Amount(102.5), lastClosingPrices.getMarketPrice(ISIN.MunichRe));
        Assert.assertEquals(new Amount(49.5), lastClosingPrices.getMarketPrice(ISIN.Allianz));
    }
}
