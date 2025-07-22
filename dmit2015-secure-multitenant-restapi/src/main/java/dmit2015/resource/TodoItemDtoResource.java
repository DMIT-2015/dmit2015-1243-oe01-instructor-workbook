package dmit2015.resource;

import common.validation.JavaBeanValidator;
import dmit2015.dto.TodoItemDto;
import dmit2015.mapper.TodoItemMapper;
import dmit2015.entity.TodoItem;
import dmit2015.repository.TodoItemRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Web API with CRUD methods for managing TodoItem.
 *  URI						        Http Method     Request Body		                            Description
 * 	----------------------          -----------		-------------------------------------------     ------------------------------------------
 *	/restapi/TodoItemsDto		    POST	      	{                                               Create a new TodoItem
 *                                                  "name":"Demo DMIT2015 assignment 1",
 *                                         	        "complete":false
 *                                         	        }
 * 	/restapi/TodoItemsDto/{id}		GET			                                                    Find one TodoItem with a id value
 * 	/restapi/TodoItemsDto		    GET			                                                    Find all TodoItem
 * 	/restapi/TodoItemsDto/{id}      PUT             {
 * 	                                                "id":1,                                         Update the TodoItem
 * 	                                                "name":"Demo DMIT2015 assignment 1",
 *                                                  "complete":true
 *                                                  }
 * /restapi/TodoItemsDto/{id}		DELETE			                                                Remove the TodoItem
 *
 *
 */

@ApplicationScoped
@RolesAllowed("Sales")
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("TodoItemsDto")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class TodoItemDtoResource {

    @Inject
    @Claim(standard = Claims.upn)
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    private UriInfo uriInfo;

    @Inject
    private TodoItemRepository todoItemRepository;

    @POST   // POST: restapi/TodoItemsDto
    public Response postTodoItem(TodoItemDto dto) {
        if (dto == null) {
            throw new BadRequestException();
        }

        String errorMessage = JavaBeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

//        TodoItem newTodoItem = mapFromDto(dto);
        TodoItem newTodoItem = TodoItemMapper.INSTANCE.toEntity(dto);
        String username = optionalUsername.getValue().orElseThrow();
        newTodoItem.setUsername(username);
        todoItemRepository.add(newTodoItem);

        URI todoItemsUri = uriInfo.getAbsolutePathBuilder().path(newTodoItem.getId().toString()).build();
        return Response.created(todoItemsUri).build();
    }

    @GET    // GET: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response getTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }
        TodoItem existingTodoItem = optionalTodoItem.get();
//        TodoItemDto dto = mapToDto(existingTodoItem);
        String username = optionalUsername.getValue().orElseThrow();
        if (!existingTodoItem.getUsername().equals(username)) {
            throw new BadRequestException("You are not allowed to access a resource owned by another user.");
        }

        TodoItemDto dto = TodoItemMapper.INSTANCE.toDto(existingTodoItem);

        return Response.ok(dto).build();
    }

    @GET    // GET: restapi/TodoItemsDto
    public Response getTodoItems() {
        String username = optionalUsername.getValue().orElseThrow();
        return Response.ok(todoItemRepository.findAllByUsername(username)
                .stream()
//                .map(this::mapToDto)
                .map(TodoItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList()))
                .build();
    }

    @PUT    // PUT: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response updateTodoItem(@PathParam("id") Long id, TodoItemDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);
        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        String errorMessage = JavaBeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

        TodoItem existingTodoItem = optionalTodoItem.orElseThrow();

        String username = optionalUsername.getValue().orElseThrow();
        if (!existingTodoItem.getUsername().equals(username)) {
            throw new BadRequestException("You are not allowed to access a resource owned by another user.");
        }

        // Copy data from the updated entity to the existing entity
        existingTodoItem.setVersion(dto.getVersion());
        existingTodoItem.setTask(dto.getName());
        existingTodoItem.setDone(dto.isComplete());

        try {
            todoItemRepository.update(existingTodoItem);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You are updating an old version of the data. Please fetch new version.")
                    .build();
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }
//        todoItemRepository.update(mapFromDto(dto));

        return Response.ok(dto).build();
    }

    @DELETE // DELETE: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response deleteTodoItem(@PathParam("id") Long id) {
        Optional<TodoItem> optionalTodoItem = todoItemRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        String username = optionalUsername.getValue().orElseThrow();
        TodoItem existingTodoItem = optionalTodoItem.orElseThrow();
        if (!existingTodoItem.getUsername().equals(username)) {
            throw new BadRequestException("You are not allowed to access a resource owned by another user.");
        }
        todoItemRepository.deleteById(id);

        return Response.noContent().build();
    }

//    private TodoItemDto mapToDto(TodoItem todoItem) {
//        return new TodoItemDto(todoItem.getId(), todoItem.getName(), todoItem.isComplete(), todoItem.getVersion());
//    }
//
//    private TodoItem mapFromDto(TodoItemDto dto) {
//        return new TodoItem(dto.getId(), dto.getName(), dto.isComplete());
//    }

}