package greencity.service.ubs;

import greencity.constant.ErrorMessage;
import greencity.dto.*;
import greencity.entity.language.Language;
import greencity.entity.order.Bag;
import greencity.entity.order.BagTranslation;
import greencity.entity.order.Service;
import greencity.entity.user.User;
import greencity.exceptions.BagNotFoundException;
import greencity.repository.BagRepository;
import greencity.repository.BagTranslationRepository;
import greencity.repository.ServiceRepository;
import greencity.repository.UserRepository;
import greencity.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
    private final BagRepository bagRepository;
    private final BagTranslationRepository translationRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public Bag addTariffService(AddServiceDto dto, String uuid) {
        Language language = new Language();
        language.setId(dto.getLanguageId());
        Bag bag = modelMapper.map(dto, Bag.class);
        BagTranslation translation = modelMapper.map(dto, BagTranslation.class);
        bag.setFullPrice(dto.getPrice() + dto.getCommission());
        bag.setCreatedAt(LocalDate.now());
        translation.setBag(bag);
        translation.setLanguage(language);
        User user = userRepository.findByUuid(uuid);
        bag.setCreatedBy(user.getRecipientName() + " " + user.getRecipientSurname());
        bagRepository.save(bag);
        translationRepository.save(translation);
        return bag;
    }

    @Override
    public List<GetTariffServiceDto> getTariffService() {
        return translationRepository.findAll()
            .stream()
            .map(this::getTariffService)
            .collect(Collectors.toList());
    }

    private GetTariffServiceDto getTariffService(BagTranslation bagTranslation) {
        return GetTariffServiceDto.builder()
            .description(bagTranslation.getDescription())
            .price(bagTranslation.getBag().getPrice())
            .capacity(bagTranslation.getBag().getCapacity())
            .name(bagTranslation.getName())
            .commission(bagTranslation.getBag().getCommission())
            .languageCode(bagTranslation.getLanguage().getCode())
            .fullPrice(bagTranslation.getBag().getFullPrice())
            .id(bagTranslation.getBag().getId())
            .createdAt(bagTranslation.getBag().getCreatedAt())
            .createdBy(bagTranslation.getBag().getCreatedBy())
            .editedAt(bagTranslation.getBag().getEditedAt())
            .editedBy(bagTranslation.getBag().getEditedBy())
            .build();
    }

    @Override
    public void deleteTariffService(Integer id) {
        Bag bag = bagRepository.findById(id).orElseThrow(
            () -> new BagNotFoundException(ErrorMessage.BAG_NOT_FOUND + id));
        bagRepository.delete(bag);
    }

    @Override
    public GetTariffServiceDto editTariffService(EditTariffServiceDto dto, Integer id, String uuid) {
        User user = userRepository.findByUuid(uuid);
        Bag bag = bagRepository.findById(id).orElseThrow(() -> new BagNotFoundException(ErrorMessage.BAG_NOT_FOUND));
        bag.setPrice(dto.getPrice());
        bag.setCapacity(dto.getCapacity());
        bag.setCommission(dto.getCommission());
        bag.setFullPrice(dto.getPrice() + dto.getCommission());
        bag.setEditedAt(LocalDate.now());
        bag.setEditedBy(user.getRecipientName() + " " + user.getRecipientSurname());
        bagRepository.save(bag);
        BagTranslation bagTranslation =
            translationRepository.findBagTranslationByBagAndLanguageCode(bag, dto.getLangCode());
        bagTranslation.setName(dto.getName());
        bagTranslation.setDescription(dto.getDescription());
        translationRepository.save(bagTranslation);
        return getTariffService(bagTranslation);
    }

    @Override
    public Service addService(CreateServiceDto dto, String uuid) {
        Service service = modelMapper.map(dto, greencity.entity.order.Service.class);
        User user = userRepository.findByUuid(uuid);
        service.setCreatedBy(user.getRecipientName() + " " + user.getRecipientSurname());
        service.setCreatedAt(LocalDate.now());
        service.setFullPrice(dto.getPrice() + dto.getCommission());
        return serviceRepository.save(service);
    }

    @Override
    public List<GetServiceDto> getService() {
        return serviceRepository.findAll()
            .stream()
            .map(this::getService)
            .collect(Collectors.toList());
    }

    private GetServiceDto getService(Service service) {
        return GetServiceDto.builder()
            .description(service.getDescription())
            .price(service.getBasePrice())
            .capacity(service.getCapacity())
            .name(service.getName())
            .commission(service.getCommission())
            .fullPrice(service.getFullPrice())
            .id(service.getId())
            .createdAt(service.getCreatedAt())
            .createdBy(service.getCreatedBy())
            .editedAt(service.getEditedAt())
            .editedBy(service.getEditedBy())
            .build();
    }
}
