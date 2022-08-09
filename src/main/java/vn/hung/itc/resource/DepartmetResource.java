package vn.hung.itc.resource;

import io.quarkus.panache.common.Sort;
import org.jboss.logging.Logger;
import org.modelmapper.ModelMapper;
import vn.hung.itc.dto.DepartmentDto;
import vn.hung.itc.entity.Department;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("departments")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmetResource {

    private static final Logger log = Logger.getLogger(UserResource.class.getName());

    private final DepartmentRepository departmentRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Inject
    public DepartmetResource(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GET
    public List<Department> getDepartment() {
        return departmentRepository.listAll(Sort.by("id"));
    }

//    @GET
//    @Path("{id}")
//    public Department getSingle(Long id) {
//        User entity = departmentRepository.findById(id);
//        if (entity == null) {
//            throw new WebApplicationException("User with id of " + id + " does not exist.", 404);
//        }
//        return entity;
//    }

    @POST
    @Transactional
    public Response addDepartment(DepartmentDto departmentDto) {
        if (departmentDto.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        Department department = modelMapper.map(departmentDto, Department.class);
        departmentRepository.persist(department);
        return Response.ok(department).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Department update(@PathParam("id") Long id, DepartmentDto departmentDto) {
        Department entity = departmentRepository.findById(id);
        departmentDto.setId(id);
        modelMapper.map(departmentDto, entity);
        return entity;
    }

}