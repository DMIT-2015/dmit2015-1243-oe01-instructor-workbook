package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.entity.Student;
import dmit2015.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

/**
 * This Jakarta RESTful Web Services root resource class provides common REST API endpoints to
 * perform CRUD operations on Jakarta Persistence entity.
 */
@ApplicationScoped
@Path("Students")                    // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)    // All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)    // All methods returns data that has been converted to JSON format
public class StudentResource {

    @Inject
    private StudentRepository _studentRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response findAllStudentsStudents() {
        return Response.ok(_studentRepository.findAll()).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findStudentById(@PathParam("id") Long id) {
        Student existingStudent = _studentRepository.findById(id).orElseThrow(NotFoundException::new);

        return Response.ok(existingStudent).build();
    }

    @POST    // This method only accepts HTTP POST requests.
    public Response createStudentStudent(Student newStudent, @Context UriInfo uriInfo) {

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

        // userInfo is injected via @Context parameter to this method
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newStudent.getId()))
                .build();

        // Set the location path of the new entity with its identifier
        // Returns an HTTP status of "201 Created" if the Student was successfully persisted
        return Response
                .created(location)
                .build();
    }

    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateStudentStudent(@PathParam("id") Long id, Student updatedStudent) {
        if (!id.equals(updatedStudent.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = JavaBeanValidator.validateBean(updatedStudent);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        Student existingStudent = _studentRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

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
        return Response.ok(existingStudent).build();
    }

    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response deleteStudent(@PathParam("id") Long id) {

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

        // Returns an HTTP status "204 No Content" to indicated that the resource was deleted
        return Response.noContent().build();
    }

}