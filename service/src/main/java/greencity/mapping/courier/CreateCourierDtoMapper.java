package greencity.mapping.courier;

import greencity.dto.courier.CreateCourierDto;
import greencity.entity.order.Courier;
import greencity.entity.order.CourierTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateCourierDtoMapper extends AbstractConverter<Courier, CreateCourierDto> {
    @Override
    protected CreateCourierDto convert(Courier source) {
        List<CourierTranslation> courierTranslations = source.getCourierTranslationList();
        String en = courierTranslations.get(0).getNameEng();
        String ua = courierTranslations.get(0).getName();

        return CreateCourierDto.builder()
            .nameEn(en)
            .nameUa(ua)
            .build();
    }
}