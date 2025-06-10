package dmit2015.model;

import dmit2015.service.JakartaPersistenceStudentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import net.datafaker.Faker;
import oracle.jdbc.driver.ErrorMessages_el;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentInitializer {
    private final Logger _logger = Logger.getLogger(StudentInitializer.class.getName());

    @Inject
    @Named("jakartaPersistenceStudentService")
    private JakartaPersistenceStudentService _studentService;


    /**
     * Using the combination of `@Observes` and `@Initialized` annotations, you can
     * intercept and perform additional processing during the phase of beans or events
     * in a CDI container.
     * <p>
     * The @Observers is used to specify this method is in observer for an event
     * The @Initialized is used to specify the method should be invoked when a bean type of `ApplicationScoped` is being
     * initialized
     * <p>
     * Execute code to create the test data for the entity.
     * This is an alternative to using a @WebListener that implements a ServletContext listener.
     * <p>
     * ]    * @param event
     */
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        _logger.info("Initializing students");

        if (_studentService.getAllStudents().isEmpty()) {
            /* You have three options for creating the test data:
                Option 1) Hard code the test data when testing a small dataset.
                Option 2) Read the test data from a text file when testing a large dataset.
                Option 3) Generate the test data using DataFaker.
                          When used with Integration Testing you will need to save the generated data
                          to a file that can be read later to compare with expected values.
             */

            try {
                var faker = new Faker();
                for (int counter = 1; counter <= 10; counter++) {
                    Student currentStudent = Student.of(faker);
                    _studentService.createStudent(currentStudent);
                }


            } catch (Exception ex) {
                _logger.warning(ex.getMessage());
            }

            _logger.info("Created " + _studentService.getAllStudents().size() + " records.");
        }
    }
}