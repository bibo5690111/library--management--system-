package model;
import java.io.Serializable;
public final class BookMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String publisher;
    private final int edition;
    private final String language;
    public BookMetadata(String publisher, int edition, String language) {
        this.publisher = publisher;
        this.edition = edition;
        this.language = language;
    }
    public String getPublisher(){
            return  publisher;
    }
    public int getEdition(){
        return  edition;
    }
    public String getLanguage(){
        return  language;
    }
    public String toString(){
        return "the publisher is" + publisher
                +", the edition is" + edition
                +", the language is" + language;
    }

}
