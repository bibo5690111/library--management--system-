package interfaces;
import model.Book;
public interface BookFilter {
    boolean matches(Book book);
    default BookFilter and(BookFilter other){
        return book -> this.matches(book) && other.matches(book);
    }
}
