import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(BranchMapper.class)
public interface BranchDB {
    @SqlQuery("SELECT * from BRANCH where id= :id")
    Branch find(@Bind("id") String id);

    @SqlUpdate("INSERT INTO Branch VALUES(:b.id, :b.name)")
    void save(@BindBean("b") Branch branch);

    @SqlUpdate("UPDATE Branch SET name = :b.name WHERE id = :b.id")
    void update(@BindBean("b") Branch branch);
}
