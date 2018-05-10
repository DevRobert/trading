package trading.persistence;

public class MySqlRepositoryParameters {
    private final String server;
    private final String username;
    private final String password;
    private final String database;

    public MySqlRepositoryParameters(String server, String username, String password, String database) {
        this.server = server;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public String getServer() {
        return this.server;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDatabase() {
        return this.database;
    }
}
