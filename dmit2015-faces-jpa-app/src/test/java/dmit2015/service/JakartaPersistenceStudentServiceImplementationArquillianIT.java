package dmit2015.service;

import dmit2015.config.ApplicationConfig;
import dmit2015.model.Student;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import net.datafaker.Faker;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ArquillianExtension.class)
public class JakartaPersistenceStudentServiceImplementationArquillianIT { // The class must be declared as public

    static Faker faker = new Faker();

    static String mavenArtifactIdId;

    @Deployment
    public static WebArchive createDeployment() throws IOException, XmlPullParserException {
        PomEquippedResolveStage pomFile = Maven.resolver().loadPomFromFile("pom.xml");
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        mavenArtifactIdId = model.getArtifactId();
        final String archiveName = model.getArtifactId() + ".war";
        return ShrinkWrap.create(WebArchive.class, archiveName)
                .addAsLibraries(pomFile.resolve("org.codehaus.plexus:plexus-utils:3.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.hamcrest:hamcrest:3.0").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.assertj:assertj-core:3.27.3").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("net.datafaker:datafaker:2.4.2").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.h2database:h2:2.3.232").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.microsoft.sqlserver:mssql-jdbc:12.10.0.jre11").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("com.oracle.database.jdbc:ojdbc11:23.7.0.25.01").withTransitivity().asFile())
                .addAsLibraries(pomFile.resolve("org.postgresql:postgresql:42.7.5").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("com.mysql:mysql-connector-j:9.2.0").withTransitivity().asFile())
//                .addAsLibraries(pomFile.resolve("org.mariadb.jdbc:mariadb-java-client:3.4.1").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.hibernate.orm:hibernate-spatial:6.6.15.Final").withTransitivity().asFile())
                // .addAsLibraries(pomFile.resolve("org.eclipse:yasson:3.0.4").withTransitivity().asFile())
                .addClass(ApplicationConfig.class)
                .addClasses(Student.class, StudentService.class, JakartaPersistenceStudentService.class)
//                .addAsLibraries(pomFile.resolve("jakarta.platform:jakarta.jakartaee-api:10.0.0").withTransitivity().asFile())
                // .addPackage("dmit2015.entity")
                .addAsResource("META-INF/persistence.xml")
                // .addAsResource(new File("src/test/resources/META-INF/persistence-entity.xml"),"META-INF/persistence.xml")
                .addAsResource("META-INF/beans.xml");
    }

    @Inject
    @Named("jakartaPersistenceStudentService")
    private JakartaPersistenceStudentService _studentService;

    @Resource
    private UserTransaction _beanManagedTransaction;

    @BeforeAll
    static void beforeAllTests() {
        // code to execute before all tests in the current test class
    }

    @AfterAll
    static void afterAllTests() {
        // code to execute after all tests in the current test class
    }

    @BeforeEach
    void beforeEachTestMethod() throws SystemException, NotSupportedException {
        // Start a new transaction
        _beanManagedTransaction.begin();
    }

    @AfterEach
    void afterEachTestMethod() throws SystemException {
        // Rollback the transaction
        _beanManagedTransaction.rollback();
    }

    @Order(1)
    @Test
    void givenNewEntity_whenAddEntity_thenEntityIsAdded() throws SystemException, NotSupportedException {
        // Arrange
        Student newStudent = Student.of(faker);

        // Act
        _studentService.createStudent(newStudent);

        // Assert
        assertThat(newStudent.getId())
                .isNotNull();

    }

    @Order(2)
    @Test
    void givenExistingId_whenFindById_thenReturnEntity() {
        // Arrange
        Student newStudent = Student.of(faker);

        // Act
        newStudent = _studentService.createStudent(newStudent);

        // Assert
        Optional<Student> optionalStudent = _studentService.getStudentById(newStudent.getId());
        assertThat(optionalStudent.isPresent())
                .isTrue();
        // Assert
        var existingStudent = optionalStudent.orElseThrow();
        assertThat(existingStudent)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newStudent);

    }

    @Order(3)
    @Test
    void givenExistingEntity_whenUpdatedEntity_thenEntityIsUpdated() {
        // Arrange
        Student newStudent = Student.of(faker);

        newStudent = _studentService.createStudent(newStudent);
        // TODO: change the values of all properties
        //newStudent.setProperty1(faker.providerName().methodName());
        //newStudent.setProperty2(faker.providerName().methodName());
        //newStudent.setProperty3(faker.providerName().methodName());

        // Act
        Student updatedStudent = _studentService.updateStudent(newStudent);

        // Assert
        Optional<Student> optionalStudent = _studentService.getStudentById(updatedStudent.getId());
        assertThat(optionalStudent.isPresent())
                .isTrue();
        var existingStudent = optionalStudent.orElseThrow();
        assertThat(existingStudent)
                .usingRecursiveComparison()
                // .ignoringFields("field1", "field2")
                .isEqualTo(newStudent);

    }

    @Order(4)
    @Test
    void givenExistingId_whenDeleteEntity_thenEntityIsDeleted() {
        // Arrange
        Student newStudent = Student.of(faker);
        newStudent = _studentService.createStudent(newStudent);
        // Act
        _studentService.deleteStudentById(newStudent.getId());
        // Assert
        Optional<Student> optionalStudent = _studentService.getStudentById(newStudent.getId());
        assertThat(optionalStudent.isPresent())
                .isFalse();

    }

//    @Order(5)
//    @ParameterizedTest
//    @CsvSource({"10"})
//    void givenMultipleEntity_whenFindAll_thenReturnEntityList(int expectedRecordCount) {
//        // Arrange: Set up the initial state
//
//        // Delete all existing data
//        assertThat(_studentService).isNotNull();
//        _studentService.deleteAllStudents();
//        // Generate expectedRecordCount number of fake data
//        Student firstExpectedStudent = null;
//        Student lastExpectedStudent = null;
//        for (int counter = 1; counter <= expectedRecordCount; counter++) {
//            Student currentStudent = Student.of(faker);
//            if (counter == 1) {
//                firstExpectedStudent = currentStudent;
//            } else if (counter == expectedRecordCount) {
//                lastExpectedStudent = currentStudent;
//            }
//
//            newStudent = _studentService.createStudent(currentStudent);
//        }
//
//        // Act: Perform the action to be tested
//        List<Student> studentList = _studentService.getAllStudents();
//
//        // Assert: Verify the expected outcome
//        assertThat(studentList.size())
//                .isEqualTo(expectedRecordCount);
//
//        // Get the first entity and compare with expected results
//        var firstActualStudent = studentList.getFirst();
//        assertThat(firstActualStudent)
//                .usingRecursiveComparison()
//                // .ignoringFields("field1", "field2")
//                .isEqualTo(firstExpectedStudent);
//        // Get the last entity and compare with expected results
//        var lastActualStudent = studentList.getLast();
//        assertThat(lastActualStudent)
//                .usingRecursiveComparison()
//                // .ignoringFields("field1", "field2")
//                .isEqualTo(lastExpectedStudent);
//
//    }

    @Order(6)
    @ParameterizedTest
    @CsvSource(value = {
            ", Less, DMIT2015-1243-OE1, First name is required",
            "Bruce, , DMIT2015-1243-OE1, Last name is required",
    }, nullValues = {"null"})
    void givenEntityWithValidationErrors_whenAddEntity_thenThrowException(
            String firstName,
            String lastName,
            String courseSection,
            String expectedExceptionMessage
    ) {
        // Arrange
        Student newStudent = new Student();
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setCourseSection(courseSection);

        try {
            // Act
            _studentService.createStudent(newStudent);
            fail("An bean validation constraint should have been thrown");
        } catch (Exception ex) {
            // Assert
            assertThat(ex)
                    .hasMessageContaining(expectedExceptionMessage);
        }

    }

}