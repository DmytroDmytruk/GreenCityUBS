package greencity.config;

import greencity.service.ubs.CertificatesActualityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CertificatesSchedulerTest {
    @InjectMocks
    CertificatesScheduler certificatesScheduler;
    @Mock
    CertificatesActualityServiceImpl certificatesActualityService;

    @Test
    void checkCertificatesForActualityTest() {
        doNothing().when(certificatesScheduler).checkCertificatesForActuality();
        certificatesScheduler.checkCertificatesForActuality();
        verify(certificatesActualityService).checkCertificatesForActuality();
    }
}
