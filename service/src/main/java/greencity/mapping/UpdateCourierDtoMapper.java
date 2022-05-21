package greencity.mapping;

import greencity.dto.courier.CourierTranslationDto;
import greencity.dto.courier.CourierUpdateDto;
import greencity.entity.order.Courier;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UpdateCourierDtoMapper extends AbstractConverter<Courier, CourierUpdateDto> {
    @Override
    protected CourierUpdateDto convert(Courier source) {
        return CourierUpdateDto.builder()
            .courierId(source.getId())
            .courierTranslationDtos(source.getCourierTranslationList().stream()
                .map(courierTranslation -> CourierTranslationDto.builder()
                    .languageCode(courierTranslation.getLanguage().getCode())
                    .name(courierTranslation.getName())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
