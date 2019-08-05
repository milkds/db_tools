package dbtools;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class BackupHandler {

    private static final String MYSQL_PATH = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump";
    private static final String USER = "root";
    private static final String PASS = "root";
    private static final String PROJECT_SCHEMA_NAME = "production_db";
    private static final String PATH_DIR = "C:\\db_backup\\production\\";



    public static void backupDB(){
        Runtime runtime = Runtime.getRuntime();
        String executeCmd = buildExecuteCmd();
        Process runtimeProcess = null;
        try {
            runtimeProcess = runtime.exec(executeCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int processComplete = runtimeProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }

    private static String buildExecuteCmd() {
     StringBuilder sb = new StringBuilder();
     String path = getPath();
     sb.append(MYSQL_PATH);
     sb.append(" -u ");
     sb.append(USER);
     sb.append("  -p");
     sb.append(PASS);
     sb.append(" ");
     sb.append(PROJECT_SCHEMA_NAME);
     sb.append(" -r ");
     sb.append(path);

      //  String executeCmd = MYSQL_PATH + " "+" -proot "+"superlift"+" -r D:\\backup.sql";

        return sb.toString();
    }

    private static String getPath() {
        String path = formatTime(Instant.now());
        path = path.replaceAll(":", "-");
        path = path.substring(0, path.length()-3);
        path =  "ToyTec " + path + ".sql";
        path = PATH_DIR + path;
        path = path.replaceAll(" ", "_");
        path = path.replaceAll(",", "");
        path = path.replaceAll("-", "_");

        return path;
    }

    private static String formatTime(Instant instant) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM )
                        .withLocale( Locale.UK )
                        .withZone( ZoneId.systemDefault() );

        return formatter.format(instant);
    }
}
