package service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {

    public static final String BOOKS_FILE = "books.dat";
    public static final String MEMBERS_FILE = "members.dat";
    public static final String MAGAZINES_FILE = "magazines.dat";

    /**
     * Serializes any List of objects into a binary file.
     */
    public <T> void saveData(String filePath, List<T> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(data);
            System.out.println("Data successfully saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Deserializes a List of objects from a binary file.
     * Returns an empty list if the file is not found or fails to load.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> loadData(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<T>) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No existing file found for " + filePath + ". Starting with empty list.");
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}