import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class InMemoryDBCreator {
    private DBI dbi;
    private Flyway flyway;

    public InMemoryDBCreator() {
        JdbcDataSource dataSource = createDataSource();
        migrate(dataSource);
        dbi = new DBI(dataSource);
    }

    public <T> T create(Class<? extends T> clazz) {
        return dbi.onDemand(clazz);
    }

    public void clean() {
        flyway.clean();
    }

    private JdbcDataSource createDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:branch;DB_CLOSE_DELAY=-1");
        dataSource.setUser("");
        dataSource.setPassword("");
        return dataSource;
    }

    private void migrate(DataSource dataSource) {
        flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("db/migration");
        flyway.migrate();
    }
}
