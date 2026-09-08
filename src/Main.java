import enums.BookStatus;
import exception.BookAlreadyBorrowedException;
import exception.BookNotFoundException;
import exception.MemberNotFoundException;
import model.Book;
import model.Magazine;
import model.Member;
import service.FileStorageService;
import service.LibraryService;
import thread.BackgroundReportTask;
import thread.ThreadManager;
import util.ReflectionInspector;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static void printMenu() {
        System.out.println("\n--- LIBRARY MANAGEMENT SYSTEM MENU ---");
        System.out.println("1. Add Book");
        System.out.println("2. Add Magazine");
        System.out.println("3. Register Member");
        System.out.println("4. Borrow Book");
        System.out.println("5. Return Book");
        System.out.println("6. Search Books");
        System.out.println("7. List Available Books");
        System.out.println("8. List Members");
        System.out.println("9. Generate Report (background)");
        System.out.println("10. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    private static void handleAddBook(Scanner scanner, LibraryService libraryService) {
        try {
            System.out.print("Enter book ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter book title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Enter author: ");
            String author = scanner.nextLine().trim();
            System.out.print("Enter publication year: ");
            String yearText = scanner.nextLine().trim();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty() || yearText.isEmpty()) {
                System.out.println("Missing fields — book not added.");
                return;
            }

            int publicationYear = Integer.parseInt(yearText);
            Book book = new Book(id, title, author, publicationYear, BookStatus.AVAILABLE);
            libraryService.addBook(book);
            System.out.println("Book added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid year. Please enter a numeric year.");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private static void handleAddMagazine(Scanner scanner, LibraryService libraryService) {
        try {
            System.out.print("Enter magazine ID: ");
            String id = scanner.nextLine().trim();
            System.out.print("Enter magazine title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Enter issue number: ");
            String issueText = scanner.nextLine().trim();

            if (id.isEmpty() || title.isEmpty() || issueText.isEmpty()) {
                System.out.println("Missing fields — magazine not added.");
                return;
            }

            int issueNumber = Integer.parseInt(issueText);
            Magazine mag = new Magazine(id, title, issueNumber, BookStatus.AVAILABLE);
            libraryService.addMagazine(mag);
            System.out.println("Magazine added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid issue number. Please enter a numeric value.");
        } catch (Exception e) {
            System.out.println("Error adding magazine: " + e.getMessage());
        }
    }

    private static void handleRegisterMember(Scanner scanner, LibraryService libraryService) {
        try {
            System.out.print("Enter member ID: ");
            String memberId = scanner.nextLine().trim();
            System.out.print("Enter member name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter phone number: ");
            String phoneNumber = scanner.nextLine().trim();

            if (memberId.isEmpty() || name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
                System.out.println("Missing member fields — registration aborted.");
                return;
            }

            Member member = new Member(memberId, name, email, phoneNumber);
            libraryService.addMember(member);
            System.out.println("Member registered successfully.");
        } catch (Exception e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    private static void handleBorrowBook(Scanner scanner, LibraryService libraryService) {
        try {
            System.out.print("Enter member ID: ");
            String memberId = scanner.nextLine().trim();
            System.out.print("Enter book ID: ");
            String bookId = scanner.nextLine().trim();
            libraryService.borrowBook(memberId, bookId);
            System.out.println("Book borrowed successfully.");
        } catch (MemberNotFoundException | BookNotFoundException | BookAlreadyBorrowedException e) {
            System.out.println("Borrow failed: " + e.getMessage());
        }
    }

    private static void handleReturnBook(Scanner scanner, LibraryService libraryService) {
        try {
            System.out.print("Enter member ID: ");
            String memberId = scanner.nextLine().trim();
            System.out.print("Enter book ID: ");
            String bookId = scanner.nextLine().trim();
            libraryService.returnBook(memberId, bookId);
            System.out.println("Book returned successfully.");
        } catch (MemberNotFoundException | BookNotFoundException e) {
            System.out.println("Return failed: " + e.getMessage());
        }
    }

    private static void handleSearchBooks(Scanner scanner, LibraryService libraryService) {
        System.out.print("Enter book ID or title keyword: ");
        String query = scanner.nextLine().trim();
        boolean found = false;

        for (Book book : libraryService.getBooks()) {
            if (book.getId().equalsIgnoreCase(query) || book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                System.out.println("- " + book.getId() + " | " + book.getTitle() + " | " + book.getAuthor() + " | " + book.getStatus());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private static void handleListAvailableBooks(LibraryService libraryService) {
        boolean found = false;
        for (Book book : libraryService.getBooks()) {
            if (book.getStatus() == BookStatus.AVAILABLE) {
                System.out.println("- " + book.getId() + " | " + book.getTitle() + " | " + book.getAuthor());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available books found.");
        }
    }

    private static void handleListMembers(LibraryService libraryService) {
        if (libraryService.getMembers().isEmpty()) {
            System.out.println("No members registered.");
            return;
        }

        for (Member member : libraryService.getMembers()) {
            System.out.println("- " + member.getMemberId() + " | " + member.getName() + " | " + member.getEmail());
        }
    }

    private static void handleGenerateReport(ThreadManager threadManager, LibraryService libraryService) {
        threadManager.runTask(new BackgroundReportTask(libraryService));
        System.out.println("Report generation started in the background.");
    }

    private static void runMenuLoop(Scanner scanner, LibraryService libraryService, FileStorageService storageService, ThreadManager threadManager) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice;
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                continue;
            }

            switch (choice) {
                case 1 -> handleAddBook(scanner, libraryService);
                case 2 -> handleAddMagazine(scanner, libraryService);
                case 3 -> handleRegisterMember(scanner, libraryService);
                case 4 -> handleBorrowBook(scanner, libraryService);
                case 5 -> handleReturnBook(scanner, libraryService);
                case 6 -> handleSearchBooks(scanner, libraryService);
                case 7 -> handleListAvailableBooks(libraryService);
                case 8 -> handleListMembers(libraryService);
                case 9 -> handleGenerateReport(threadManager, libraryService);
                case 10 -> {
                    System.out.println("Saving data and exiting...");
                    saveAll(storageService, libraryService);
                    threadManager.shutdown();
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void saveAll(FileStorageService storageService, LibraryService libraryService) {
        storageService.saveData(FileStorageService.BOOKS_FILE, libraryService.getBooks());
        storageService.saveData(FileStorageService.MEMBERS_FILE, libraryService.getMembers());
        storageService.saveData(FileStorageService.MAGAZINES_FILE, libraryService.getMagazines());
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    LIBRARY MANAGEMENT SYSTEM INITIALIZATION");
        System.out.println("==================================================\n");

        FileStorageService storageService = new FileStorageService();
        LibraryService libraryService = new LibraryService();
        ThreadManager threadManager = new ThreadManager();
        Scanner scanner = new Scanner(System.in);

        List<Book> loadedBooks = storageService.loadData(FileStorageService.BOOKS_FILE);
        List<Magazine> loadedMagazines = storageService.loadData(FileStorageService.MAGAZINES_FILE);
        List<Member> loadedMembers = storageService.loadData(FileStorageService.MEMBERS_FILE);

        if (loadedBooks.isEmpty()) {
            System.out.println("Populating default sample data...");
            libraryService.addBook(new Book("B101", "Clean Code", "Robert C. Martin", 2008, BookStatus.AVAILABLE));
            libraryService.addBook(new Book("B102", "Effective Java", "Joshua Bloch", 2018, BookStatus.AVAILABLE));
        } else {
            libraryService.setBooks(loadedBooks);
        }

        if (loadedMagazines.isEmpty()) {
            libraryService.addMagazine(new Magazine("M201", "Tech Monthly", 45, BookStatus.AVAILABLE));
        } else {
            libraryService.setMagazines(loadedMagazines);
        }

        if (loadedMembers.isEmpty()) {
            libraryService.addMember(new Member("MEM01", "Bahaa Masri", "bahaa@example.com", "0590000000"));
        } else {
            libraryService.setMembers(loadedMembers);
        }

        System.out.println("\n--- DEMONSTRATING REFLECTION UTIL ---");
        ReflectionInspector.inspectClass(Book.class);

        runMenuLoop(scanner, libraryService, storageService, threadManager);

        System.out.println("Application exiting.");
    }
}