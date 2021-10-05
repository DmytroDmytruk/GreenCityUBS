package greencity.filters;

import lombok.*;
import org.springframework.data.domain.Sort;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPage {
    private int pageNumber = 0;
    private int pageSize = 10;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private String sortBy = "id";
}