package sba.cartoes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EASC {

    private String generatedPath;

    public EASC(String generatedPath) {

        this.generatedPath = generatedPath;
    }

    public String getGeneratedPath() {
        return generatedPath;
    }

    // Get an array list of all Eascs

    private List<String> returnLocationOfEasc() {

        File file = new File(getGeneratedPath());
        String[] filterEasc = file.list();

        List<String> listOfEasc = new ArrayList<>();
        for (int i = 0; i < file.list().length; i++) {
            if (filterEasc[i].startsWith("EASC")) {
                listOfEasc.add(filterEasc[i]);
            }
        }
        Collections.sort(listOfEasc, Collections.reverseOrder());
        return listOfEasc;
    }
    // Get array list of all Eascs generated within 6 days

    private List<File> getListOfEascs() {

        File file = null;
        File[] arrFile = new File[returnLocationOfEasc().size()];
        List<String> listOfEascs = returnLocationOfEasc();
        List<File> Eascs = new ArrayList<>();
        for (int i = 0; i < listOfEascs.size(); i++) {
            file = new File(getGeneratedPath() + listOfEascs.get(i));
            if (isWithinDate(file.lastModified())) {
                Eascs.add(arrFile[i] = new File(listOfEascs.get(i)));
            }
        }
        // Reverse the list and get the last file generated
        Collections.sort(Eascs, Collections.reverseOrder());
        return Eascs;
    }

    // Get the last element

    public File getLastEASC() {

        List<File> EascList = new ArrayList<>();
        EascList = getListOfEascs();
        String lastFileName = EascList.get(0).toString();
        File file = new File(lastFileName);
        return file;
    }

    // Check whether date is within range

    private boolean isWithinDate(long milliseconds) {

        boolean isWithin = false;
        Date pDate = null;
        Date cDate = null;

        SimpleDateFormat formateDate1 = new SimpleDateFormat("yyyy-MM-dd");
        // Past Date ----------------------------------------------	5 days	432000000 // 10 days 864000000 // 6 days 518400000
        // 2 days 172800000
        Date pastDate = new Date(System.currentTimeMillis() - 1728000000); // Current date - 6 days

        try {
            pDate = formateDate1.parse(formateDate1.format(pastDate));
//			System.out.println("Past date " + pDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Current Date ----------------------------------------------
        Date currentDate = new Date(System.currentTimeMillis());
        try {
            cDate = formateDate1.parse(formateDate1.format(currentDate));
//			System.out.println("Current date " + cDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (format(milliseconds).after(pDate) && (format(milliseconds).before(cDate) || format(milliseconds).equals(cDate))) {
            isWithin = true;
        }
        return isWithin;
    }

    private Date format(long time) {
        return new Date(time);
    }

    // Copy the file from 10.90 to 61.43
    public void copyEASC(String copyEasc) throws IOException {

        File source = null;
        File destination = null;
        File file = getLastEASC();
        String[] fileNames = new String[]{file.toString()};

        for (int i = 0; i < fileNames.length; i++) {
            source = new File(getGeneratedPath() + fileNames[i]);
            destination = new File(copyEasc + fileNames[i]);
            System.out.println("File source " + source);
            System.out.println("File destination " + destination);

            try {
                copyFile(source, destination);
                System.out.println("File copied");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void copyFile(File src, File dest) throws IOException {
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Get the last generated file successfully processed

    public String lastGeneratedFile() throws IOException {

        String sequence = "";
        String fileType = "EASC_";
        String fileFormat = ".inp";

        File file = new File(getGeneratedPath() + getLastEASC());

        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            //process the line
        //    System.out.println("Header " + line);
            sequence = line.substring(24, 35);
            break;
        }
        br.close();
        return sequence = fileType.concat(sequence).concat(fileFormat);
    }

    // Read file and get sequence

    public boolean firstValidateSameDir() throws IOException {

        boolean isOnFolder = false;
        File fileExists = new File(getGeneratedPath() + lastGeneratedFile());
        if (fileExists.exists()) {
            return isOnFolder = true;
        }
        return false;
    }

}
