package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterClientHeaders(AuthHeaderFactory.class)
@RegisterProvider(TodoItemRestApiResponseMapper.class)
@RegisterRestClient(baseUri = "http://localhost:8091/restapi/TodoItemsDto")
public interface TodoItemDtoMultitenantMpRestClient {

    @POST
    Response create(TodoItemDto newTodoItemDto);

    @GET
    List<TodoItemDto> findAll();

    @GET
    @Path("/{id}")
    Optional<TodoItemDto> findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    TodoItemDto update(@PathParam("id") Long id, TodoItemDto updatedTodoItemDto);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id);

}