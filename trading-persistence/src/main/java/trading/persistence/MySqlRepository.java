package trading.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlRepository {
    protected Connection openNewConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/trading?user=root&password=testtest&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
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
