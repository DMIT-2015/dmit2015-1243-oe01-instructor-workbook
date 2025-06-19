package dmit2015.hr.persistence;

import dmit2015.hr.entity.Department;
import dmit2015.hr.entity.Employee;
import jakarta.data.repository.Find;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

@Repository(dataStore = "oracle-jpa-hr-pu")
public interface EmployeeRepository extends Serializable {
    @Query("select d from Department d where lower(d.departmentName) like lower(?1) order by d.departmentName")
    List<Department> departmentsBy(String departmentName);

    @Query("select e from Employee e join fetch e.department join fetch e.job join fetch e.manager where e.department.id = ?1")
    List<Employee> employeesByDepartmentId(Short id);

    @Find
    Department departmentByDepartmentId(Short id);

}
