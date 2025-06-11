import controller.UserController;
import model.User; 
import model.enums.enum_Gender;
import model.enums.enum_Role;
import model.enums.enum_UserStatus;
import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class test {

    @Test
    public void testGetUserById_ReturnsCorrectUser_FromDatabase() {
        UserController userController = new UserController();
        int testUserId = 1; // Gỉa sử
        User user = userController.getUserByID(testUserId);
        assertNotNull(user);
        assertEquals(testUserId, user.getUserId());
    }

//    @Test
//     public void testCreateUser_And_CheckUsernameAndEmailExistence() {
//         // Arrange
//         UserController userController = new UserController();
//         User user = new User();

//         long timestamp = System.currentTimeMillis();
//         String uniqueUsername = "testuser_" + timestamp;
//         String uniqueEmail = "testuser_" + timestamp + "@mail.com";

//         user.setUsername(uniqueUsername);
//         user.setPassword("testpassword123");
//         user.setEmail(uniqueEmail);
//         user.setPhoneNumber("0123456789");
//         user.setFullName("Test User");
//         user.setDateOfBirth(LocalDate.of(2000, 1, 1));
//         user.setGender(enum_Gender.MALE);
//         user.setAddress("Hanoi");
//         user.setStatus(enum_UserStatus.ACTIVE);
//         user.setRole(enum_Role.MEMBER);

//         // Act
//         int userId = userController.createUser(user);

//         // Assert
//         assertTrue("User creation failed, userId should be > 0", userId > 0);

//         boolean isUsernameExists = userController.isUsernameExists(uniqueUsername);
//         boolean isEmailExists = userController.isEmailExists(uniqueEmail);

//         assertTrue("Username should exist after creation", isUsernameExists);
//         assertTrue("Email should exist after creation", isEmailExists);
//     }
}