package dmit2015.service;

import dmit2015.restclient.Student;
import dmit2015.restclient.StudentMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientStudentService")
@ApplicationScoped
public class MpRestClientStudentService implements StudentService {

    @Inject
    @RestClient
    private StudentMpRestClient mpRestClient;

    @Override
    public Student createStudent(Student student) {
        try (Response response = mpRestClient.create(student)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            } else {
                String location = response.getHeaderString("Location");
                int resourceIdIndex = location.lastIndexOf("/") + 1;
                Long resourceId = Long.parseLong(location.substring(resourceIdIndex));
                student.setId(resourceId);
            }
        }
        return student;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return Optional.of(mpRestClient.findById(id));
    }

    @Override
    public List<Student> getAllStudents() {
        return mpRestClient.findAll();
    }

    @Override
    public Student updateStudent(Student student) {
        return mpRestClient.update(student.getId(),student);
    }

    @Override
    public void deleteStudentById(Long id) {
        mpRestClient.delete(id);
    }
}
