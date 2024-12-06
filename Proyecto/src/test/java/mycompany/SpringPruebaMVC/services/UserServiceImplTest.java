package mycompany.SpringPruebaMVC.services;

import mycompany.LibrarySystem.model.entities.User;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.UserRepository;
import mycompany.LibrarySystem.model.services.ReportEntryService;
import mycompany.LibrarySystem.model.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import mycompany.LibrarySystem.model.entities.Lending;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReportEntryService reportEntryService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllUsers() {
        // Arrange
        List<User> mockUsers = List.of(
                new User() {
            {
                setId(1);
                setName("John");
            }
        },
                new User() {
            {
                setId(2);
                setName("Jane");
            }
        }
        );
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<User> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindUserById_Found() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("John");
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.findUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testFindUserById_NotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        User result = userService.findUserById(1);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testSaveUser() {
        // Arrange
        User user = new User();
        user.setName("John");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName("John");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.saveUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getName());
        verify(userRepository, times(1)).save(user);
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.NEW_USER),
                eq("Se agregó el usuario: John"),
                eq(1)
        );
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setLendings(null);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1);

        // Assert
        verify(userRepository, times(1)).deleteById(1);
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.DELETE_USER),
                eq("Se eliminó el usuario: John"),
                eq(1)
        );
    }

    @Test
    void testDeleteUser_HasLendings() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setName("John");

        Lending lending = new Lending(); // Crear un objeto Lending simulado
        user.setLendings(List.of(lending)); // Configurar préstamos pendientes

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userService.deleteUser(1);
        });

        assertEquals("El usuario 'John' tiene préstamos pendientes y no puede ser eliminado.", exception.getMessage());
        verify(userRepository, never()).deleteById(anyInt());
        verify(reportEntryService, never()).createReport(any(), any(), any());
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        userService.deleteUser(1);

        // Assert
        verify(userRepository, never()).deleteById(anyInt());
        verify(reportEntryService, never()).createReport(any(), any(), any());
    }

    @Test
    void testFindUsersByNameContaining() {
        // Arrange
        List<User> mockUsers = List.of(
                new User() {
            {
                setId(1);
                setName("John");
            }
        },
                new User() {
            {
                setId(2);
                setName("Johnny");
            }
        }
        );
        when(userRepository.findByNameContainingIgnoreCase("John")).thenReturn(mockUsers);

        // Act
        List<User> result = userService.findUsersByNameContaining("John");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByNameContainingIgnoreCase("John");
    }

    @Test
    void testBulkDeleteUsers_Success() {
        // Arrange
        List<Integer> userIds = List.of(1, 2);

        List<User> users = List.of(
                new User() {
            {
                setId(1);
                setName("John");
                setLendings(null);
            }
        },
                new User() {
            {
                setId(2);
                setName("Jane");
                setLendings(null);
            }
        }
        );

        when(userRepository.findAllById(userIds)).thenReturn(users);

        // Act
        userService.bulkDeleteUsers(userIds);

        // Assert
        verify(userRepository, times(1)).deleteAll(users);
        verify(reportEntryService, times(2)).createReport(any(), any(), any());
    }

    @Test
    void testBulkDeleteUsers_HasLendings() {
        // Arrange
        List<Integer> userIds = List.of(1, 2);

        User user1 = new User();
        user1.setId(1);
        user1.setName("John");
        user1.setLendings(null);

        User user2 = new User();
        user2.setId(2);
        user2.setName("Jane");

        Lending lending = new Lending();
        lending.setId(2);
        lending.setUser(user2); // Asociar préstamo al usuario
        user2.setLendings(List.of(lending)); // Usuario con préstamos pendientes

        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.bulkDeleteUsers(userIds));

        assertEquals("El usuario 'Jane' tiene préstamos pendientes y no puede ser eliminado.", exception.getMessage());

        verify(userRepository, never()).deleteAll(any());
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.DELETE_USER),
                eq("Se eliminó el usuario: John"),
                eq(1)
        );
    }

}
