package model;

import enums.MembershipType;
import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
    private static final long serialVersionUID = 1L;

    private MembershipType membershipType;
    private List<String> borrowedBooksIds;

    // Default Constructor
    public Member() {
        super("UNKNOWN","Unknown","unknown@example.com","0000000000");
        this.borrowedBooksIds = new ArrayList<>();
    }

    // 4-Argument Constructor (commonly used in Main / LibraryService)
    public Member(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        this.membershipType = MembershipType.STANDARD; // Default membership type
        this.borrowedBooksIds = new ArrayList<>();
    }

    // Full Constructor
    public Member(String id, String email, String phoneNumber, String name, MembershipType membershipType) {
        super(id, name, email, phoneNumber);
        this.membershipType = membershipType;
        this.borrowedBooksIds = new ArrayList<>();
    }

    // --- Getter for memberId ---
    public String getMemberId() {
        return super.getMemberId();
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public List<String> getBorrowedBooksIds() {
        return borrowedBooksIds;
    }

    public void setBorrowedBooksIds(List<String> borrowedBooksIds) {
        this.borrowedBooksIds = borrowedBooksIds;
    }

    // --- Borrowing Methods ---
    public void borrowBook(String bookId) {
        borrowedBooksIds.add(bookId);
    }

    public void returnBook(String bookId) {
        borrowedBooksIds.remove(bookId);
    }

    // --- Alias methods matching LibraryService calls ---
    public void borrowItem(String itemId) {
        borrowBook(itemId);
    }

    public void returnItem(String itemId) {
        returnBook(itemId);
    }
}