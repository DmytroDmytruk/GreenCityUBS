package greencity.dto;

import greencity.entity.enums.OrderStatus;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class OrderStatusPageDto {
    private Long id;
    private OrderStatus orderStatus;
    private double orderFullPrice;
    private double orderDiscountedPrice;
    private double orderCertificateTotalDiscount;
    private double orderBonusDiscount;
    private double orderExportedPrice;
    private double orderExportedDiscountedPrice;
    private String recipientName;
    private String recipientSurname;
    private String recipientPhone;
    private String recipientEmail;
    private String addressCity;
    private String addressStreet;
    private String addressDistrict;
    private String addressComment;
    private Map<Integer, Integer> amountOfBagsOrdered;
    private List<BagInfoDto> bags;
    private Map<Integer, Integer> amountOfBagsExported;
    private Set<String> additionalOrders;
}