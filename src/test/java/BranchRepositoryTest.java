import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BranchRepositoryTest {

    private BranchRepository dbBranchRepository;
    private InMemoryDBCreator inMemoryDBCreator;

    @Before
    public void setup(){
        inMemoryDBCreator = new InMemoryDBCreator();
        BranchDB branchDB = inMemoryDBCreator.create(BranchDB.class);
        dbBranchRepository = new BranchRepository(branchDB);
    }

    @After
    public void teardown(){
        inMemoryDBCreator.clean();
    }

    @Test
    public void shouldInsertRow(){
        Branch branch = new Branch("1","name");
        dbBranchRepository.create(branch);
        Branch createdBranch = dbBranchRepository.getById("1").get();
        assertThat(createdBranch.getName()).isEqualTo("name");
    }

    @Test
    public void shouldUpdateRow(){
        Branch branch = new Branch("1","name");
        dbBranchRepository.create(branch);
        branch.setName("new name");
        dbBranchRepository.update(branch);
        Branch updatedBranch = dbBranchRepository.getById("1").get();
        assertThat(updatedBranch.getName()).isEqualTo("new name");
    }
}
