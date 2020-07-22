package sba.cartoes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// Logs.genFileReport(files[i]);

public class Logs {

    public static void genFileReport(String input) throws IOException {

        Date ficheiroDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

        File file = null;
        String fileName = dateFormat.format(ficheiroDate);
        String fileNamePath = "/Desktop/";
        try {
            file = new File(fileNamePath + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
        }catch (Exception ex) {
            ex.printStackTrace();
            ex.getMessage();
        }
    }
}






