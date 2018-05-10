package trading.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlRepository {
    private final MySqlRepositoryParameters parameters;

    public MySqlRepository(MySqlRepositoryParameters parameters) {
        this.parameters = parameters;
    }

    protected Connection openNewConnection() {
        String connectionUrlTemplate = "jdbc:mysql://%s/%s?user=%s&password=%s&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

        String connectionUrl = String.format(
                connectionUrlTemplate,
                this.parameters.getServer(),
                this.parameters.getDatabase(),
                this.parameters.getUsername(),
                this.parameters.getPassword());

        try {
            return DriverManager.getConnection(connectionUrl);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void closeConnection(Connection connection) {
        try {
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
