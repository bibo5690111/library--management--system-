package model;
import enums.BookStatus;
public class Magazine extends LibraryItem  {
    private static final long serialVersionUID = 1L;
    private int issueNumber;
    public Magazine(String id,String title, int issueNumber, BookStatus status) {
        super(id, title, status);
        this.issueNumber = issueNumber;

    }
    public int getIssueNumber() {
    return issueNumber;
    }
    public void setIssueNumber(int issueNumber) {
    this.issueNumber = issueNumber;
    }
    @Override
    public String getItemType() {
        return "Magazine";
    }
}
