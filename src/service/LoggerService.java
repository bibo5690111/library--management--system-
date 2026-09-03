package service;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class LoggerService {
   public static final String LOG_FILE ="app.log";
   public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private  void writeLog(String level , String message, Throwable throwable){
      String timeStamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
       try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
           writer.println("[" + timeStamp + "] [" + level + "] " + message);
           if (throwable != null) {
               throwable.printStackTrace(writer);
           }

       }
       catch ( IOException e) {
           System.err.println("Failed to write log file"+ e.getMessage());
       }
}
public void logInfo(String message){
   writeLog("INFO",message , null);}

    public void logError(String message, Throwable throwable){
       writeLog("ERROR", message , throwable);
    }
}
