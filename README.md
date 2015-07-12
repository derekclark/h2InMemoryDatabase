# How to Implement a H2 In Memory Database
## Motivation
This is an example of using a H2 in memory database so that you can TDD without hitting a real database.
## Detail
This is the test class. The setup method first creates the H2 in memory database instance...
```java
public class DBBranchRepositoryTest {

  private BranchRepository dbBranchRepository; 
  private InMemoryDBCreator inMemoryDBCreator;

  @Before
  public void setup(){
     inMemoryDBCreator = new InMemoryDBCreator();
     …

public class InMemoryDBCreator{
   public InMemoryDBCreator() {
      JdbcDataSource dataSource = createDataSource();
      … 
   }

   private JdbcDataSource createDataSource() {
      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setUrl("jdbc:h2:mem:branch;DB_CLOSE_DELAY=-1");
      ...
```
Then it runs all of the flyway migration scripts to create the database tables.
```java
public InMemoryDBCreator() {
   ...
   migrate(dataSource);
   dbi = new DBI(dataSource);
}
```
Notice that the flyway migration scripts are versioned and show the evolution of the database; creating tables, adding fields, dropping fields etc.

Back to the test setup() which then links the BranchDB DAO to the in memory database.
```java
   @Before
   public void setup() {
       ...
       BranchDB branchDB = inMemoryDBCreator.create(BranchDB.class);
       ...
   }
```
create() will obtain a sql object instance...
```java
public <T> T create(Class<? extends T> clazz) {
   return dbi.onDemand(clazz);
}
```
DBI is part of the JDBI library which exposes relational database access in idiomatic java. See [here](http://jdbi.org/).

jDBI is a convenience library built on top of JDBC. JDBC works very well but generally seems to optimize for the database vendors (driver writers) over the users. jDBI attempts to expose the same functionality, but in an API optimized for users.

BranchDB is a DAO interface. Using the JDBI library it simplifies creating DAO objects mapping a single method to a single SQL statement via an annotated interface.

```java
@RegisterMapper(BranchMapper.class)
public interface BranchDB {
   @SqlQuery("SELECT * from BRANCH where id= :id")
   Branch find(@Bind("id") String id);

   @SqlUpdate("INSERT INTO Branch VALUES(:b.id, :b.name)")
   void save(@BindBean("b") Branch branch);

   @SqlUpdate("UPDATE Branch SET name = :b.name WHERE id = :b.id")
   void update(@BindBean("b") Branch branch);
}
```
BranchMapper specifies what to do with each row of a result set. In our example, it converts the result set into a Branch object.

Back to the test setup() - finally it creates our test object - DBBranchRepository - and passes in the DBBranch DAO into its constructor.
```java
   @Before
   public void setup() {
       ...
       dbBranchRepository = new BranchRepository(branchDB);
   }
```
Now let’s write a test to write a branch record (with id=1) to the database, and then assert that the row was written by reading it back and validating its contents.
```java
@Test
public void shouldInsertRow(){
   Branch branch = new Branch("1","name");
   dbBranchRepository.create(branch);
   Branch createdBranch = dbBranchRepository.getById("1").get();
   assertThat(createdBranch.getName()).isEqualTo("name");
}

public class BranchRepository {
   public void create(Branch branch) {
       branchDB.save(branch);
   }
```
dbBranchRepository.create calls branchDB.save(branch) which is defined in the BranchDB annotated interface as to insert a row into the table
```java
   @SqlUpdate("INSERT INTO Branch VALUES(:b.id, :b.name)")
   void save(@BindBean("b") Branch branch);
```
Assert that the insert worked by reading the record just written…
```java
Branch createdBranch = dbBranchRepository.getById("1").get();
assertThat(createdBranch.getName()).isEqualTo("name");
```
The teardown() method calls the flyway.clean() drops all database objects ready for the next test.

```java
@After
public void teardown(){
   inMemoryDBCreator.clean();
}

InMemoryDBCreator.class
public void clean() {
   flyway.clean();
}
```
