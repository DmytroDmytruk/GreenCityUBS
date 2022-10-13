package greencity.mapping.employee;

import greencity.ModelUtils;
import greencity.dto.employee.EmployeeDto;
import greencity.entity.user.employee.Employee;
import greencity.mapping.employee.EmployeeDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmployeeDtoMapperTest {

    @InjectMocks
    EmployeeDtoMapper mapper;

    @Test
    void convert() {
        EmployeeDto dto = ModelUtils.getEmployeeDto();
        Employee employee = ModelUtils.getEmployee();

        assertEquals(employee.getId(), mapper.convert(dto).getId());
        assertEquals(employee.getFirstName(), mapper.convert(dto).getFirstName());
        assertEquals(employee.getLastName(), mapper.convert(dto).getLastName());
        assertEquals(employee.getPhoneNumber(), mapper.convert(dto).getPhoneNumber());
    }
}