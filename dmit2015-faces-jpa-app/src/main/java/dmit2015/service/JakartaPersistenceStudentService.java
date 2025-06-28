package dmit2015.service;

import dmit2015.model.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import java.util.random.RandomGenerator;

@Named("jakartaPersistenceStudentService")
@ApplicationScoped
public class JakartaPersistenceStudentService implements StudentService {

    @Inject
    private SecurityContext securityContext;

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager _entityManager;

    @Override
    @Transactional
    public Student createStudent(Student student) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        String username = securityContext.getCallerPrincipal().getName();
        student.setUsername(username);
        _entityManager.persist(student);
        return student;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        try {
            Student querySingleResult = _entityManager.find(Student.class, id);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // id value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> getAllStudents() {
        // Deny anonymous users from calling this method
        String username = securityContext.getCallerPrincipal().getName();
        if (username.equalsIgnoreCase("anonymous")) {
            throw new RuntimeException("Access denied. Anonymous users are allowed to access this method.");
        }
        // Deny access to authenticated users not in the role Sales or Shipping.
        boolean isSalesOrShipping = securityContext.isCallerInRole("Sales")
                ||  securityContext.isCallerInRole("Shipping");
        if (!isSalesOrShipping) {
            throw new RuntimeException("Access denied. You do not have permission to access this method.");
        }
        // For the Sales role return all data associated with the current authenticated user
        boolean isSalesRole = securityContext.isCallerInRole("Sales");
        if (isSalesRole) {
            return _entityManager.createQuery(
                    "SELECT s FROM Student s WHERE s.username = :usernameValue", Student.class)
                    .setParameter("usernameValue", username)
                    .getResultList();
        }
        // For the Shipping role return all data
        return _entityManager.createQuery("SELECT o FROM Student o ", Student.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {
        // Deny access if the username of the entity does not match the authenticated username
        String username = securityContext.getCallerPrincipal().getName();
        if (!student.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException("Access denied. You do not have permission to updated data from another user.");
        }

        Optional<Student> optionalStudent = getStudentById(student.getId());
        if (optionalStudent.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", student.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingStudent = optionalStudent.orElseThrow();
            // Update only properties that is editable by the end user

            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());
            existingStudent.setCourseSection(student.getCourseSection());

            student = _entityManager.merge(existingStudent);
        }
        return student;
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {

        Optional<Student> optionalStudent = getStudentById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.orElseThrow();

            // Deny access if the username of the entity does not match the authenticated username
            String username = securityContext.getCallerPrincipal().getName();
            if (!student.getUsername().equalsIgnoreCase(username)) {
                throw new RuntimeException("Access denied. You do not have permission to updated data from another user.");
            }

            // Write code to throw a RuntimeException if this entity contains child records
            _entityManager.remove(student);
        } else {
            throw new RuntimeException("Could not find Student with id: " + id);
        }
    }

}