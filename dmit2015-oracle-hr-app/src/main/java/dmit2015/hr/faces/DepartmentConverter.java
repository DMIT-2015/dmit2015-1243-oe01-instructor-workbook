package dmit2015.hr.faces;

import dmit2015.hr.entity.Department;
import dmit2015.hr.persistence.EmployeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
@FacesConverter(value = "departmentConverter", managed = true)
public class DepartmentConverter implements Converter<Department> {

    @Inject
    private EmployeeRepository _employeeRepository;


    @Override
    public Department getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isBlank()) {
            try {
                Short departmentId = Short.parseShort(value);
                return _employeeRepository.departmentByDepartmentId(departmentId);
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"Conversion Error","Not a valid department."));
            }
        } else {
            return null;
        }

    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Department value) {
        if (value != null) {
            return value.getId().toString();
        } else {
            return null;
        }
    }
}
