package sba.cartoes;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EERC {

    private String EERCFilePath;

    public EERC(String EERCFilePath) {
        this.EERCFilePath = EERCFilePath;
    }

    public String getEERCFilePath() {
        return EERCFilePath;
    }

    private List<String> returnListOfEERC() {

        File file = new File(getEERCFilePath());
        String[] filterEERC = file.list();

        List<String> listOfEERC = new ArrayList<>();
        for (int i = 0; i < file.list().length; i++) {
            if (filterEERC[i].startsWith("eerc")) {
                listOfEERC.add(filterEERC[i]);
            }
        }
        Collections.sort(listOfEERC, Collections.reverseOrder());
        return listOfEERC;
    }

    public List<File> returnListSequence (){

        File file = null;
        File [] arrFile = new File[returnListOfEERC().size()];
        List<String> listOfEERCs = returnListOfEERC();
        List<File> EERCs = new ArrayList<>();

        for ( int i = 0; i < listOfEERCs.size();i++){
            file = new File(getEERCFilePath() + listOfEERCs.get(i));
            if (isWithinDate(file.lastModified())) {
                arrFile[i] = new File(listOfEERCs.get(i));
                EERCs.add(arrFile[i]);
            }
        }
        return EERCs;
    }

    public boolean validateSequence(String sequence) throws FileNotFoundException {

        String fileType = "";
        String fileSequence = "";
        boolean wasProcessed = false;

        // Check the sequence of the file to be validated
        String validateFileType = sequence.substring(0,4);
//        System.out.println("validate file type " + validateFileType);
        String validateFileSequence = sequence.substring(4,15);
//        System.out.println("validate file sequence " + validateFileSequence);


        List<File> file = returnListSequence();

        int n = 1; // The line number
        String line;
        for ( int j = 0; j < n; j++){
            for (int i = 0; i < file.size(); i++){
                try (BufferedReader br = new BufferedReader(new FileReader(getEERCFilePath() + file.get(i)))) {
                    br.readLine();
                    line = br.readLine();
                    fileType = line.substring(2,6);
                    fileSequence = line.substring(13,24);
//                    System.out.println("Processing response " + line.substring(24,25));
                    if ((fileType.equals(validateFileType)) && (fileSequence.equals(validateFileSequence)) && (Integer.parseInt(line.substring(24,25)) == 0)){
                        wasProcessed = true;
                    }else {
                        continue;
                        //    System.out.println("File was not processed/EERC was not received");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return wasProcessed;
    }

    // Check whether date is within range

    private boolean isWithinDate(long milliseconds) {

        boolean isWithin = false;
        Date pDate = null;
        Date cDate = null;

        SimpleDateFormat formateDate1= new SimpleDateFormat("yyyy-MM-dd");
        // Past Date --------- 15 days 1296000000	2 days in ms 172800000 5 days	432000000 // 10 days 864000000 // 6 days 518400000
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

    private static Date format(long time) {
        return new Date(time);
    }

}

