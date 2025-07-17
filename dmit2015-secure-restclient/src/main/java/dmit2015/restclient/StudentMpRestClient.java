package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RequestScoped
@RegisterRestClient(baseUri = "http://localhost:8090/restapi/StudentDtos")
public interface StudentMpRestClient {

    @POST
    Response create(Student student);

    @GET
    List<Student> findAll();

    @GET
    @Path("/{id}")
    Student findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    Student update(@PathParam("id") Long id, Student student);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);
}
