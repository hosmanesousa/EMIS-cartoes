package sba.cartoes;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EERR {

    private String eerrFilePath;

    public EERR(String eerrFilePath) {
        this.eerrFilePath = eerrFilePath;
    }

    public String getEerrFilePath() {
        return eerrFilePath;
    }

    private List<String> returnListOfEERR() {

        File file = new File(getEerrFilePath());
        String[] filterEERR = file.list();

        List<String> listOfEerr = new ArrayList<>();
        for (int i = 0; i < file.list().length; i++) {
            if (filterEERR[i].startsWith("eerr")) {
                listOfEerr.add(filterEERR[i]);
            }
        }
        Collections.sort(listOfEerr, Collections.reverseOrder());
        return listOfEerr;
    }

    public List<File> returnListSequence (){

        File file = null;
        File [] arrFile = new File[returnListOfEERR().size()];
        List<String> listOfEERRs = returnListOfEERR();
        List<File> eerrs = new ArrayList<>();

        for ( int i = 0; i < listOfEERRs.size();i++){
            file = new File(getEerrFilePath() + listOfEERRs.get(i));
            if (isWithinDate(file.lastModified())) {
                arrFile[i] = new File(listOfEERRs.get(i));
                eerrs.add(arrFile[i]);
            }
        }
        return eerrs;
    }

    public boolean validateSequence(String sequence) throws FileNotFoundException {

        String fileType = "";
        String fileSequence = "";
        boolean wasProcessed = false;

        // Check the sequence of the file to be validated
        String validateFileType = sequence.substring(0,4);
//        System.out.println("validate file type " + validateFileType);
        String validateFileSequence = sequence.substring(5,16);
//        System.out.println("validate file sequence " + validateFileSequence);


        List<File> file = returnListSequence();

        int n = 1; // The line number
        String line;
        for ( int j = 0; j < n; j++){
            for (int i = 0; i < file.size(); i++){
                try (BufferedReader br = new BufferedReader(new FileReader(getEerrFilePath() + file.get(i)))) {
                    br.readLine();
                    line = br.readLine();
                    fileType = line.substring(1,5);
                    fileSequence = line.substring(12,23);
//                    System.out.println("Processing response " + line.substring(23,24));
                    if ( (fileType.equals(validateFileType)) && (fileSequence.equals(validateFileSequence)) && (Integer.parseInt(line.substring(23,24)) == 0)){
                        wasProcessed = true;
//                        System.out.println("The file was processed " + wasProcessed);
                    }else {
                        continue;
                    //    System.out.println("File was not processed/EERR was not received");
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
