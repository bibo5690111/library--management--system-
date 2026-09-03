package thread;

import model.Book;
import model.Magazine;
import model.Member;
import service.LibraryService;

import java.util.List;

public class BackgroundReportTask implements Runnable {

    private final LibraryService libraryService;

    public BackgroundReportTask(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Override
    public void run() {
        System.out.println("\n--- [Background Task] Generating Library Status Report ---");

        List<Book> books = libraryService.getBooks();
        List<Magazine> magazines = libraryService.getMagazines();
        List<Member> members = libraryService.getMembers();

        long borrowedCount = books.stream()
                .filter(b -> b.getStatus() == enums.BookStatus.BORROWED)
                .count();

        System.out.println("Total Books Registered: " + books.size());
        System.out.println("Books Currently Borrowed: " + borrowedCount);
        System.out.println("Books Available: " + (books.size() - borrowedCount));
        System.out.println("Total Magazines Registered: " + magazines.size());
        System.out.println("Total Registered Members: " + members.size());
        System.out.println("--- [Background Task] Report Complete ---\n");
    }
}