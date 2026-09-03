package service;

import enums.BookStatus;
import exception.BookAlreadyBorrowedException;
import exception.BookNotFoundException;
import exception.MemberNotFoundException;
import interfaces.BookFilter;
import model.Book;
import model.Magazine;
import model.Member;

import java.util.ArrayList;
import java.util.List;

public class LibraryService {

    private List<Book> books;
    private List<Magazine> magazines;
    private List<Member> members;
    private final LoggerService logger;

    public LibraryService() {
        this.books = new ArrayList<>();
        this.magazines = new ArrayList<>();
        this.members = new ArrayList<>();
        this.logger = new LoggerService();
    }

    public LibraryService(List<Book> books, List<Magazine> magazines, List<Member> members) {
        this.books = (books != null) ? books : new ArrayList<>();
        this.magazines = (magazines != null) ? magazines : new ArrayList<>();
        this.members = (members != null) ? members : new ArrayList<>();
        this.logger = new LoggerService();
    }

    // --- Add Operations ---
    public void addBook(Book book) {
        books.add(book);
        logger.logInfo("Book added: " + book.getTitle() + " (ID: " + book.getId() + ")");
    }

    public void addMagazine(Magazine magazine) {
        magazines.add(magazine);
        logger.logInfo("Magazine added: " + magazine.getTitle() + " (ID: " + magazine.getId() + ")");
    }

    public void addMember(Member member) {
        members.add(member);
        logger.logInfo("Member registered: " + member.getName() + " (ID: " + member.getMemberId() + ")");
    }

    // --- Search Helpers ---
    public Book findBookById(String bookId) {
        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(bookId)) {
                return book;
            }
        }
        return null;
    }

    public Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberId().equalsIgnoreCase(memberId)) {
                return member;
            }
        }
        return null;
    }

    // --- Borrow & Return Workflows ---
    public void borrowBook(String memberId, String bookId)
            throws MemberNotFoundException, BookNotFoundException, BookAlreadyBorrowedException {

        Member member = findMemberById(memberId);
        if (member == null) {
            MemberNotFoundException ex = new MemberNotFoundException("Member with ID '" + memberId + "' not found.");
            logger.logError("Borrow failure: Member not found", ex);
            throw ex;
        }

        Book book = findBookById(bookId);
        if (book == null) {
            BookNotFoundException ex = new BookNotFoundException("Book with ID '" + bookId + "' not found.");
            logger.logError("Borrow failure: Book not found", ex);
            throw ex;
        }

        if (book.getStatus() == BookStatus.BORROWED) {
            BookAlreadyBorrowedException ex = new BookAlreadyBorrowedException("Book '" + book.getTitle() + "' is already checked out.");
            logger.logError("Borrow failure: Book already borrowed", ex);
            throw ex;
        }

        book.setStatus(BookStatus.BORROWED);
        member.borrowItem(bookId);
        logger.logInfo("Member " + memberId + " successfully borrowed book: " + book.getTitle());
    }

    public void returnBook(String memberId, String bookId)
            throws MemberNotFoundException, BookNotFoundException {

        Member member = findMemberById(memberId);
        if (member == null) {
            MemberNotFoundException ex = new MemberNotFoundException("Member with ID '" + memberId + "' not found.");
            logger.logError("Return failure: Member not found", ex);
            throw ex;
        }

        Book book = findBookById(bookId);
        if (book == null) {
            BookNotFoundException ex = new BookNotFoundException("Book with ID '" + bookId + "' not found.");
            logger.logError("Return failure: Book not found", ex);
            throw ex;
        }

        book.setStatus(BookStatus.AVAILABLE);
        member.returnItem(bookId);
        logger.logInfo("Member " + memberId + " successfully returned book: " + book.getTitle());
    }

    // --- Filtering ---
    public List<Book> filterBooks(BookFilter filter) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (filter.matches(book)) {
                result.add(book);
            }
        }
        return result;
    }

    // --- Getters & Setters ---
    public List<Book> getBooks() { return books; }
    public List<Magazine> getMagazines() { return magazines; }
    public List<Member> getMembers() { return members; }
    public void setBooks(List<Book> books) { this.books = books; }
    public void setMagazines(List<Magazine> magazines) { this.magazines = magazines; }
    public void setMembers(List<Member> members) { this.members = members; }
}