import enums.BookStatus;
import model.Book;
import model.Magazine;
import model.Member;
import service.FileStorageService;
import service.LibraryService;
import thread.BackgroundReportTask;
import thread.ThreadManager;
import util.ReflectionInspector;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    LIBRARY MANAGEMENT SYSTEM INITIALIZATION");
        System.out.println("==================================================\n");

        // 1. Initialize Services
        FileStorageService storageService = new FileStorageService();
        LibraryService libraryService = new LibraryService();
        ThreadManager threadManager = new ThreadManager();

        // 2. Load Existing Data or Create Default Data
        List<Book> loadedBooks = storageService.loadData(FileStorageService.BOOKS_FILE);
        List<Member> loadedMembers = storageService.loadData(FileStorageService.MEMBERS_FILE);

        if (loadedBooks.isEmpty()) {
            System.out.println("Populating default sample data...");
            libraryService.addBook(new Book("B101", "Clean Code", "Robert C. Martin", 2008, BookStatus.AVAILABLE));
            libraryService.addBook(new Book("B102", "Effective Java", "Joshua Bloch", 2018, BookStatus.AVAILABLE));
            libraryService.addMagazine(new Magazine("M201", "Tech Monthly", 45, BookStatus.AVAILABLE));
        } else {
            libraryService.setBooks(loadedBooks);
        }

        if (loadedMembers.isEmpty()) {
            libraryService.addMember(new Member("MEM01", "Bahaa Masri", "bahaa@example.com", "0590000000"));
        } else {
            libraryService.setMembers(loadedMembers);
        }

        // 3. Demonstrate Reflection Engine
        System.out.println("\n--- DEMONSTRATING REFLECTION UTIL ---");
        ReflectionInspector.inspectClass(Book.class);

        // 4. Test Borrow & Return Workflows
        System.out.println("\n--- TESTING BORROW WORKFLOW ---");
        try {
            libraryService.borrowBook("MEM01", "B101");
            System.out.println("Successfully borrowed book B101!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // 5. Trigger Asynchronous Background Report
        System.out.println("\n--- TRIGGERING BACKGROUND REPORT THREAD ---");
        threadManager.runTask(new BackgroundReportTask(libraryService));

        // 6. Save State to File System
        System.out.println("\n--- SAVING SYSTEM STATE ---");
        storageService.saveData(FileStorageService.BOOKS_FILE, libraryService.getBooks());
        storageService.saveData(FileStorageService.MEMBERS_FILE, libraryService.getMembers());

        // 7. Graceful Thread Shutdown
        threadManager.shutdown();
        System.out.println("\nApplication execution finished successfully.");
    }
}