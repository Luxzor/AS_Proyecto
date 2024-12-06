package mycompany.SpringPruebaMVC.services;

import mycompany.LibrarySystem.model.entities.LendingsDetailed;
import mycompany.LibrarySystem.model.repositories.LendingsDetailedRepository;
import mycompany.LibrarySystem.model.services.LendingsDetailedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class LendingsDetailedServiceImplTest {

    @Mock
    private LendingsDetailedRepository lendingsDetailedRepository;

    @InjectMocks
    private LendingsDetailedServiceImpl lendingsDetailedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks antes de cada prueba
    }

    @Test
    void testFindPendingLendingsByUserName_WithResults() {
        // Arrange
        String userName = "John Doe";

        // Crear una lista simulada de resultados
        List<LendingsDetailed> mockResults = List.of(
                new LendingsDetailed() {
            {
                setLendingId(1); // Usar setLendingId en lugar de setId
                setUserName(userName);
                setDateReturn(null);
            }
        },
                new LendingsDetailed() {
            {
                setLendingId(2); // Otro ejemplo
                setUserName(userName);
                setDateReturn(null);
            }
        }
        );

        // Simular comportamiento del repositorio
        when(lendingsDetailedRepository.findByUserNameAndDateReturnIsNull(userName)).thenReturn(mockResults);

        // Act
        List<LendingsDetailed> result = lendingsDetailedService.findPendingLendingsByUserName(userName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Verificar que el tamaño de la lista sea correcto
        verify(lendingsDetailedRepository, times(1)).findByUserNameAndDateReturnIsNull(userName); // Verificar la llamada al repositorio
    }

    @Test
    void testFindPendingLendingsByUserName_NoResults() {
        // Arrange
        String userName = "Unknown User";

        when(lendingsDetailedRepository.findByUserNameAndDateReturnIsNull(userName)).thenReturn(List.of());

        // Act
        List<LendingsDetailed> result = lendingsDetailedService.findPendingLendingsByUserName(userName);

        // Assert
        assertEquals(0, result.size()); // Verificar que la lista esté vacía
        verify(lendingsDetailedRepository, times(1)).findByUserNameAndDateReturnIsNull(userName); // Verificar que el método del repositorio se llamó correctamente
    }
}
