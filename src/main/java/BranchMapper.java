import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BranchMapper implements ResultSetMapper<Branch> {

    @Override
    public Branch map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        String id = r.getString("id");
        String name = r.getString("name");
        return new Branch(id, name);
    }

}

