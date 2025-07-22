package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RequestScoped
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(ForbiddenResponseMapper.class)
@RegisterRestClient(baseUri = "http://localhost:8091/restapi/StudentDtos")
public interface StudentMultitenantMpRestClient {

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
