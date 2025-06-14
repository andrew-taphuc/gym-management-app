package controller;

import model.Member;
import java.util.List;

public abstract class AbstractMemberController implements IMemberController {
    protected final String controllerName = "MemberController";

    protected void logAction(String action) {
        System.out.println("[" + controllerName + "] " + action);
    }

    // Có thể bổ sung logic chung cho các controller con ở đây nếu cần
} 