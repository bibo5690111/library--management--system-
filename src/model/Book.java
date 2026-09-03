package model;
import java.util.*;
import enums.BookStatus;
import enums.BookCategory;
public class Book extends LibraryItem implements Comparable<Book> {
    private String author;
    private String isbn;
    private BookCategory category;
    private int publicationYear;
    private int numberOfPages;
    private double rating;
    private double price;
    private BookMetadata metadata;
    private transient int sessionViewCount = 0 ;
    private static final long serialVersionUID = 1L;

    public Book(BookCategory category, String id, String title, BookStatus status , String author, String isbn, int publicationYear, int numberOfPages, double rating, double price, BookMetadata metadata) {
        super(id, title, status);
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.numberOfPages = numberOfPages;
        this.rating = rating;
        this.price = price;
        this.metadata = metadata;
        this.category= category;
    }

    // Convenience constructor used by Main for quick sample data
    public Book(String id, String title, String author, int publicationYear, BookStatus status) {
        super(id, title, status);
        this.author = author;
        this.isbn = "";
        this.publicationYear = publicationYear;
        this.numberOfPages = 0;
        this.rating = 0.0;
        this.price = 0.0;
        this.metadata = new BookMetadata("Unknown", 1, "Unknown");
        this.category = BookCategory.TECHNOLOGY;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public BookCategory getCategory() {
        return category;
    }
    public void setCategory(BookCategory category) {
        this.category = category;
    }
    public int getPublicationYear() {
        return publicationYear;

    }
    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }
    public int getNumberOfPages() {
        return numberOfPages;
    }
    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getSessionViewCount() {
        return sessionViewCount;
    }
    public void incrementSessionViewCount() {
        this.sessionViewCount++;
    }
public BookMetadata getMetadata(){
        return metadata;
}
public void setMetadata(BookMetadata metadata) {
        this.metadata = metadata;
}

    @Override
    public int compareTo(Book other) {

        return this.getTitle().compareToIgnoreCase(other.getTitle());

    }


    @Override
    public String getItemType() {
        return "book";
    }
}
