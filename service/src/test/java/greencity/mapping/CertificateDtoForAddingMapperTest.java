package greencity.mapping;

import greencity.ModelUtils;
import greencity.entity.enums.CertificateStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import greencity.dto.CertificateDtoForAdding;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class CertificateDtoForAddingMapperTest {
    @InjectMocks
    CertificateDtoForAddingMapper certificateDtoForAddingMapper;

    @Test
    void convert() {
        CertificateDtoForAdding certificateDtoForAdding = ModelUtils.getCertificateDtoForAdding();

        assertEquals(certificateDtoForAdding.getPoints(),
            certificateDtoForAddingMapper.convert(certificateDtoForAdding).getPoints());
        assertEquals(certificateDtoForAdding.getCode(),
            certificateDtoForAddingMapper.convert(certificateDtoForAdding).getCode());
        assertEquals(LocalDate.now(), certificateDtoForAddingMapper.convert(certificateDtoForAdding).getCreationDate());
        assertEquals(LocalDate.now().plusMonths(certificateDtoForAdding.getMonthCount()),
            certificateDtoForAddingMapper.convert(certificateDtoForAdding).getExpirationDate());
        assertEquals(CertificateStatus.ACTIVE,
            certificateDtoForAddingMapper.convert(certificateDtoForAdding).getCertificateStatus());
    }
}