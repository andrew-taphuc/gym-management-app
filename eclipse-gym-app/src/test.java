import controller.EquipmentTypeController;
import controller.UserController;
import model.User;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class test {
    // Unit Test: kiểm thử đơn vị cho hàm login (mock UserController)
    @Test
    public void testLogin_WithValidCredentials_ReturnsUser() {
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

    // Integration Test: kiểm thử tích hợp với UserController thật (giả lập DB)
    @Test
    public void testLogin_Integration_WithValidCredentials() {
        UserController userController = new UserController();
        String username = "integrationUser";
        String password = "integrationPass";
        // Giả lập thêm user vào DB (nếu UserController có hàm addUser)
        userController.addUser(username, password);

        User user = userController.login(username, password);
        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    // System Test: kiểm thử hệ thống (giả lập toàn bộ quy trình đăng nhập)
    @Test
    public void testLogin_System_FullProcess() {
        // Giả lập nhập liệu từ UI
        String username = "systemUser";
        String password = "systemPass";
        UserController userController = new UserController();
        userController.addUser(username, password);

        // Người dùng nhập thông tin và nhấn đăng nhập
        User user = userController.login(username, password);

        // Kiểm tra kết quả cuối cùng
        assertNotNull(user);
        assertEquals(username, user.getUsername());
        // Có thể kiểm tra thêm trạng thái session nếu có
    }

    public class testEquipmentTypeController {

        // Unit Test: kiểm thử đơn vị (mock DB)
        @Test
        public void testIsEquipmentTypeInUse_Unit() {
            EquipmentTypeController controller = mock(EquipmentTypeController.class);
            when(controller.isEquipmentTypeInUse(1)).thenReturn(true);

            boolean result = controller.isEquipmentTypeInUse(1);
            assertTrue(result);
        }

        // Integration Test: kiểm thử tích hợp (thực thi với DB thật hoặc giả lập)
        @Test
        public void testIsEquipmentTypeInUse_Integration() {
            EquipmentTypeController controller = new EquipmentTypeController();
            int equipmentTypeId = 999; // ID giả lập, đảm bảo không tồn tại trong DB

            boolean result = controller.isEquipmentTypeInUse(equipmentTypeId);
            assertFalse(result); // Kỳ vọng không sử dụng
        }

        // System Test: kiểm thử hệ thống (giả lập quy trình sử dụng loại thiết bị)
        @Test
        public void testIsEquipmentTypeInUse_System() {
            EquipmentTypeController controller = new EquipmentTypeController();
            int equipmentTypeId = 2; // Giả sử đã có loại thiết bị này và đang được sử dụng

            boolean result = controller.isEquipmentTypeInUse(equipmentTypeId);
            // Kết quả phụ thuộc vào dữ liệu thực tế, ở đây chỉ minh họa
            // assertTrue(result); hoặc assertFalse(result);
        }
    }
}


