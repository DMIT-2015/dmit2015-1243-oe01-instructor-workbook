package dmit2015.service;

import dmit2015.model.Student;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Named("memoryStudentService")
@ApplicationScoped
public class MemoryStudentService implements StudentService {

    private final List<Student> students = new ArrayList<>();

    @PostConstruct
    public void init() {

        var faker = new Faker();
//        var randomGenerator = RandomGenerator.getDefault();
        for (int counter = 1; counter <= 5; counter++) {
            var currentStudent = new Student();
            currentStudent.setId(UUID.randomUUID().toString());

            currentStudent.setFirstName(faker.name().firstName());
            currentStudent.setLastName(faker.name().lastName());
            currentStudent.setCourseSection("DMIT2015-1243-OE01");

            students.add(currentStudent);
        }

    }

    @Override
    public Student createStudent(Student student) {
        student.setId(UUID.randomUUID().toString());
        students.add(student);
        return student;
    }

    @Override
    public Optional<Student> getStudentById(String id) {
        return students
                .stream()
                .filter(currentStudent -> currentStudent.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Student> getAllStudents() {
        return students;
    }

    @Override
    public Student updateStudent(Student student) {
        return student;
    }

    @Override
    public void deleteStudentById(String id) {
        Optional<Student> optionalStudent = getStudentById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.orElseThrow();
            students.remove(student);
        } else {
            throw new RuntimeException("Could not find Student with id: " + id);
        }
    }
}