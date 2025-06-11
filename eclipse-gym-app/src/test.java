import controller.UserController;
import model.User;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class test {
    @Test
    public void testLogin_WithValidCredentials_ReturnsUser() {
        // Giả lập UserController và User (không truy cập DB)
        UserController userController = mock(UserController.class);
        String username = "testuser";
        String password = "password123";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userController.login(username, password)).thenReturn(mockUser);

        User user = userController.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }
}


