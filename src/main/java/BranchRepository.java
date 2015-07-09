import java.util.Optional;

public class BranchRepository {
    private BranchDB branchDB;

    public BranchRepository(BranchDB branchDB){
        this.branchDB = branchDB;
    }

    public void create(Branch branch) {
        branchDB.save(branch);
    }

    public Optional<Branch> getById(String id) {
        return Optional.ofNullable(branchDB.find(id));
    }

    public void update(Branch branch) {
        branchDB.update(branch);
    }
}
