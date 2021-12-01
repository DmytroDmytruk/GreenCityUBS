package greencity.mapping;

import greencity.dto.AddServiceDto;
import greencity.dto.TariffTranslationDto;
import greencity.entity.order.Bag;
import greencity.entity.order.BagTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddServiceDtoMapper extends AbstractConverter<Bag, AddServiceDto> {
    @Override
    protected AddServiceDto convert(Bag source) {
        List<BagTranslation> translations = new ArrayList<>(source.getBagTranslations());
        List<TariffTranslationDto> dto = translations.stream().map(
            i -> new TariffTranslationDto(i.getName(), i.getDescription(), i.getLanguage().getId()))
            .collect(Collectors.toList());
        source.setFullPrice(source.getPrice() + source.getCommission());
        return AddServiceDto.builder()
            .locationId(source.getLocation().getId())
            .commission(source.getCommission())
            .capacity(source.getCapacity())
            .price(source.getPrice())
            .tariffTranslationDtoList(dto)
            .build();
    }
}
