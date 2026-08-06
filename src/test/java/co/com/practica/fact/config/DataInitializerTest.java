package co.com.practica.fact.config;

import co.com.practica.fact.repository.ParametroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataInitializer")
class DataInitializerTest {

    @Mock
    private ParametroRepository parametroRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_persistsSeedParametersWhenEmpty() {
        when(parametroRepository.count()).thenReturn(0L);

        dataInitializer.run();

        verify(parametroRepository, times(1)).saveAll(anyList());
    }

    @Test
    void run_skipsWhenDataAlreadyExists() {
        when(parametroRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(parametroRepository, never()).saveAll(anyList());
    }
}
