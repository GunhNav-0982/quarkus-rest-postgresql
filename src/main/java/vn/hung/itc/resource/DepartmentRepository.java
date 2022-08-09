package vn.hung.itc.resource;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import vn.hung.itc.entity.Department;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DepartmentRepository implements PanacheRepositoryBase<Department, Long> {
}
