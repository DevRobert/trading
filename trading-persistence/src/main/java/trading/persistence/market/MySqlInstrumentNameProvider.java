package trading.persistence.market;

import trading.domain.ISIN;
import trading.domain.market.InstrumentNameProvider;
import trading.persistence.MySqlRepository;
import trading.persistence.MySqlRepositoryParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySqlInstrumentNameProvider extends MySqlRepository implements InstrumentNameProvider {
    private Map<ISIN, String> instrumentNames;

    public MySqlInstrumentNameProvider(MySqlRepositoryParameters parameters) {
        super(parameters);
    }

    private void loadInstrumentNames() {
        if(this.instrumentNames != null) {
            return;
        }

        Connection connection = this.openNewConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select Isin, Name from instrument");

            ResultSet resultSet = preparedStatement.executeQuery();

            Map<ISIN, String> instrumentNames = new HashMap<>();

            while(resultSet.next()) {
                String isin = resultSet.getString(1);
                String name = resultSet.getString(2);

                instrumentNames.put(new ISIN(isin), name);
            }

            this.instrumentNames = instrumentNames;
        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            this.closeConnection(connection);
        }
    }

    @Override
    public String getInstrumentName(ISIN isin) {
        this.loadInstrumentNames();
        return this.instrumentNames.get(isin);
    }
}
