package model;
import interfaces.Persistable;
import enums.BookStatus;
public abstract class LibraryItem implements Persistable {
    private static final long serialVersionUID = 1L;
private String id;
private String title;
protected BookStatus status;
public LibraryItem(String id, String title, BookStatus status) {
    this.id = id;
    this.title = title;
    this.status = status;
}
public String getId(){
    return id;
}
public void setId(String id){
    this.id = id;
}
public String getTitle(){
    return title;
}
public void setTitle(String title){
    this.title = title;

}
public BookStatus getStatus(){
    return status;
}
public void setStatus(BookStatus status){
    this.status = status;

}
public abstract String getItemType();
}
