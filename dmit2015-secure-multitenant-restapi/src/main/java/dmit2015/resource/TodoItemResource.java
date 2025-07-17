package dmit2015.resource;

import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@ApplicationScoped
@Path("/TodoItems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoItemResource {

    @Inject
    private TodoItemRepository todoItemRepository;

    @POST
    public void createNewItem(TodoItem todoItem) {
        todoItemRepository.add(todoItem);
    }

    @GET
    @Path("{id}")
    public TodoItem getItemById(@PathParam("id") Long id) {
        return todoItemRepository.findById(id).orElseThrow();
    }

    @GET
    public List<TodoItem> getItems() {
        return todoItemRepository.findAll();
    }

    @PUT
    @Path("{id}")
    public void updateItem(@PathParam("id") Long id, TodoItem todoItem) {
        todoItemRepository.update(todoItem);
    }

    @DELETE
    @Path("{id}")
    public void deleteItem(@PathParam("id") Long id) {
        todoItemRepository.deleteById(id);
    }
}
