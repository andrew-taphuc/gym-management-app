package controller;

import model.Member;
import java.util.List;

public interface IMemberController {
    Member getMemberByID(int memberId);
    int createMember(int userId);
    Member findMemberByCodeOrPhone(String keyword);
    List<Member> getAllMembers();
    Integer getMemberIdByUserId(int userId);
} 