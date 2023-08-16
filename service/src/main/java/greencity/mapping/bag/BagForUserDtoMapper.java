package greencity.mapping.bag;

import greencity.constant.AppConstant;
import greencity.dto.bag.BagForUserDto;
import greencity.entity.order.OrderBag;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BagForUserDtoMapper extends AbstractConverter<OrderBag, BagForUserDto> {
    @Override
    protected BagForUserDto convert(OrderBag source) {
        return BagForUserDto.builder()
            .service(source.getName())
            .serviceEng(source.getNameEng())
            .capacity(source.getCapacity())
            .fullPrice(BigDecimal.valueOf(source.getPrice())
                .movePointLeft(AppConstant.TWO_DECIMALS_AFTER_POINT_IN_CURRENCY)
                .doubleValue())
            .build();
    }
}
