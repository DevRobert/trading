package trading.persistence;

public class MySqlRepositoryParametersBuilder {
    private String server;
    private String username;
    private String password;
    private String database;

    public MySqlRepositoryParametersBuilder setServer(String server) {
        this.server = server;
        return this;
    }

    public MySqlRepositoryParametersBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public MySqlRepositoryParametersBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public MySqlRepositoryParametersBuilder setDatabase(String database) {
        this.database = database;
        return this;
    }

    public MySqlRepositoryParameters build() {
        return new MySqlRepositoryParameters(
                this.server,
                this.username,
                this.password,
                this.database
        );
    }
}
