package vn.hung.itc.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.util.internal.StringUtil;
import io.quarkus.panache.common.Sort;
import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;
import vn.hung.itc.dto.DepartmentDto;
import vn.hung.itc.dto.UserDto;
import vn.hung.itc.entity.Department;
import vn.hung.itc.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    ModelMapper modelMapper = new ModelMapper();
    @Inject
    public UserResource(UserRepository userRepository, DepartmentRepository departmentRepository){
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    @GET
    public List<User> getUser() {
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
    public Response addUser(UserDto userDto) {
        Department department = departmentRepository.findById(userDto.getDepartmentId());
        if(department == null){
            throw new WebApplicationException("Department with id of " + userDto.getDepartmentId() + " does not exist.", 404);
        }
        User user = modelMapper.map(userDto, User.class);
        user.setDepartment(department);
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

//    @GET
//    public List<User> getUserInDepartment(@QueryParam("departmentId") Long departmentId){
//        Department department = departmentRepository.findById(departmentId);
//        return department.getListUser();
//    }

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
