package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.entity.Student;
import dmit2015.dto.StudentDto;
import dmit2015.mapper.StudentMapper;
import dmit2015.repository.StudentRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import net.datafaker.providers.base.Marketing;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on the DTO (Data Transfer Object) for a Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("StudentDtos")                // All methods in this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)
// All methods in this class expects method parameters to contain data in JSON format
@Produces(MediaType.APPLICATION_JSON)    // All methods in this class returns data in JSON format
public class StudentDtoResource {

    @Inject
    @Claim(standard = Claims.upn)   // The username for the user.
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    @Claim(standard = Claims.groups)    // The roles that the subject is a member of.
    private ClaimValue<Optional<Set<String>>> optionalGroups;

    @Inject
    private StudentRepository _studentRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response findAllStudentsStudents() {
        return Response.ok(
                _studentRepository
                        .findAll()
                        .stream()
                        .map(StudentMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())
        ).build();
    }

//    @RolesAllowed({"Marketing","Sales"})
    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findStudentByIdStudentById(@PathParam("id") Long id) {
        String username = optionalUsername.getValue().orElseThrow();
        Set<String> groups  = optionalGroups.getValue().orElseThrow();
        System.out.printf("username: %s\ngroups:%s\n", username, groups);

        Student existingStudent = _studentRepository.findById(id).orElseThrow(NotFoundException::new);

        StudentDto dto = StudentMapper.INSTANCE.toDto(existingStudent);

        return Response.ok(dto).build();
    }

    @RolesAllowed("HockeyPlayer")
    @POST    // This method only accepts HTTP POST requests.
    public Response createStudentStudent(StudentDto dto, @Context UriInfo uriInfo) {
        Student newStudent = StudentMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(newStudent);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        try {
            // Persist the new Student into the database
            _studentRepository.add(newStudent);
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // uriInfo is injected via @Context parameter to this method
        URI location = UriBuilder
                .fromPath(uriInfo.getPath())
                .path("{id}")
                .build(newStudent.getId());

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Student was created.
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateStudentStudent(@PathParam("id") Long id, StudentDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Student existingStudent = _studentRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        Student updatedStudent = StudentMapper.INSTANCE.toEntity(dto);

        String errorMessage = JavaBeanValidator.validateBean(updatedStudent);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        existingStudent.setVersion(updatedStudent.getVersion());
        existingStudent.setFirstName(updatedStudent.getFirstName());
        existingStudent.setLastName(updatedStudent.getLastName());
        existingStudent.setCourseSection(updatedStudent.getCourseSection());

        try {
            _studentRepository.update(existingStudent);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        StudentDto updatedDto = StudentMapper.INSTANCE.toDto(existingStudent);
        return Response.ok(updatedDto).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response deleteStudentStudent(@PathParam("id") Long id) {

        Student existingStudent = _studentRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        try {
            _studentRepository.delete(existingStudent);    // Removes the Student from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" to indicate the resource was deleted
        return Response.noContent().build();

    }

}