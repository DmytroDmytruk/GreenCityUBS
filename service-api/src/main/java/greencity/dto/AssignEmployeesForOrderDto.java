package greencity.dto;

import java.util.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AssignEmployeesForOrderDto {
    private Long orderId;
    List<AssignForOrderEmployee> employeesList;
}