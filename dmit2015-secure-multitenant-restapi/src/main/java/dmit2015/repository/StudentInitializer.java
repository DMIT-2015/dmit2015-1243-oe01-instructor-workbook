package dmit2015.repository;

import dmit2015.entity.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import net.datafaker.Faker;

import java.util.logging.Logger;

@ApplicationScoped
public class StudentInitializer {
    private final Logger _logger = Logger.getLogger(StudentInitializer.class.getName());

    @Inject
    private StudentRepository _studentRepository;


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

        if (_studentRepository.count() == 0) {

            try {
                var faker = new Faker();
                for (int count = 1; count <= 10; count++) {
                    var currentStudent = Student.of(faker);
                    _studentRepository.add(currentStudent);
                }

            } catch (Exception ex) {
                _logger.fine(ex.getMessage());
            }

            _logger.info("Created " + _studentRepository.count() + " records.");
        }
    }
}