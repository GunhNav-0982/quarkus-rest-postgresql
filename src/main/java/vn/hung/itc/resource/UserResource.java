package vn.hung.itc.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;
import vn.hung.itc.dto.UserDto;
import vn.hung.itc.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Path("users")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private static final Logger log = Logger.getLogger(UserResource.class.getName());

    @Inject
    UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();


    @GET
    public List<User> get() {
        return userRepository.listAll(Sort.by("id"));
    }

    @GET
    @Path("{id}")
    public User getSingle(Long id) {
        User entity = userRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(UserDto userDto) {
        if (userDto.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        User user = modelMapper.map(userDto, User.class);
        userRepository.persist(user);
        return Response.ok(user).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public User update(@PathParam("id") Long id, UserDto userDto) {
        User entity = userRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
        }
//        entity.setFirstName(userDto.getFirstName());
//        entity.setLastName(userDto.getLastName());
//        entity.setPhoneNumber(userDto.getPhoneNumber());
//        entity.setEmail(userDto.getEmail());
//        entity.setAddress(userDto.getAddress());
        userDto.setId(id);
//        entity = modelMapper.map(userDto, User.class);
        modelMapper.map(userDto,entity);
        return entity;

    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        User entity = userRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
        }
        userRepository.delete(entity);
        return Response.status(204).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            log.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}
