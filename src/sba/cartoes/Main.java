package sba.cartoes;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String originEDNP = "/EMIS-cartoes/EMISout/";
    private static String originEASC = "/EMIS-cartoes/EMISout/";
    private static String originECSV = "/EMIS-cartoes/EMISout/";
    private static String originEERR = "/EMIS-cartoes/EMISin/";
    private static String originEERC = "/EMIS-cartoes/EMISin/";

    private static String copyToProcessEDNP = "/EMIS-cartoes/ToWatch/";
    private static String copyToProcessEASC = "/EMIS-cartoes/ToWatch/";
    private static String copyToProcessECSV = "/EMIS-cartoes/ToWatch/";

    public static boolean checkOldEDNP(EDNP ednp) throws IOException {
        return ednp.firstValidateSameDir();
    }
    public static boolean checkOldEASC(EASC easc) throws IOException {
        return easc.firstValidateSameDir();
    }
    public static boolean checkOldECSV(ECSV ecsv) throws IOException {
        return ecsv.firstValidateSameDir();
    }
    // Get processed sequences ECSVs
    public static boolean getProcessedSequenceECSV(File fileECSV){
        List<String> storeECSV = new ArrayList<>();
        boolean isInList = true;
        if (!storeECSV.contains(fileECSV) ){
            isInList = false;
            storeECSV.add(fileECSV.toString());
        }
        return isInList;
    }
    // Get processed sequences EDNPs
    public static boolean getProcessedSequenceEDNP(File fileENDP){
        List<String> storeEDNP = new ArrayList<>();
        boolean isInList = true;
        if (!storeEDNP.contains(fileENDP)){
            isInList = false;
            storeEDNP.add(fileENDP.toString());
        }
        return isInList;
    }
    // Get processed sequences EASCs
    public static boolean getProcessedSequenceEASC(File fileEASC){
        List<String> storeEASC = new ArrayList<>();
        boolean isInList = true;
        if (!storeEASC.contains(fileEASC)){
            isInList = false;
            storeEASC.add(fileEASC.toString());
        }
        return isInList;
    }

    public static boolean monitorForFiles(String folder) {
        boolean fileProcessed = false;
        Path path = Paths.get(folder);
        try {
            WatchService watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_DELETE);
            System.out.println("Monitoring " + folder + " for changes...");
            WatchKey watchKey = watcher.take();
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Processed: " + event.context().toString());
                    fileProcessed = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileProcessed;
    }

    public static boolean isThereFile(String path){
        File file = new File(path);
        boolean fileOnFolder = false;

        if ( file.list().length > 0) {
            fileOnFolder = true;
        }
        return fileOnFolder;
    }

    public static void setCopyToProcessEDNP(boolean processed, EDNP ednp) throws IOException {
        if (processed) {
            try {
                ednp.copyEDNP(copyToProcessEDNP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setCopyToProcessEASC(boolean processed, EASC easc){
        if (processed) {
            try {
                easc.copyEASC(copyToProcessEASC);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setCopyToProcessECSV(boolean processed, ECSV ecsv){
        if (processed) {
            try {
                ecsv.copyECSV(copyToProcessECSV);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String [] args) throws IOException {

        boolean isProcessed = false;
        boolean fileCheck = false;
        boolean isEDNP2BProcessed = false;
        boolean isECSV2BProcessed = false;
        boolean isEASC2BProcessed = false;
        boolean monitorFolderEDNP = false;
        boolean monitorFolderECSV = false;
        boolean monitorFolderEASC = false;

        // Get the last ednp
        EDNP ednp = new EDNP(originEDNP);
        // Get the last easc
        EASC easc = new EASC(originEASC);
        // Get the last ecsv
        ECSV ecsv = new ECSV(originECSV);

        // Get err files
        EERR eerr = new EERR(originEERR);
        EERC eerc = new EERC(originEERC);

        // Get the last generated files
        File fileENDP = ednp.getLastEDNP();
        File fileEASC = easc.getLastEASC();
        File fileECSV = ecsv.getLastECSV();

        isEDNP2BProcessed = getProcessedSequenceEDNP(fileENDP);
        if (isEDNP2BProcessed == false) {
            //   Validate on EERR files
            isProcessed = eerr.validateSequence(ednp.lastGeneratedFile());
            setCopyToProcessEDNP(isProcessed, ednp);
            System.out.println("Error file read EDNP");
        } else {
            isProcessed = checkOldEDNP(ednp);
            setCopyToProcessEDNP(isProcessed, ednp);
            System.out.println("Same dir read EDNP");
        }

        isECSV2BProcessed = getProcessedSequenceECSV(fileECSV);
        if (isECSV2BProcessed == false){
            // Validate on EERR files
            isProcessed = eerc.validateSequence(ecsv.lastGeneratedFile());
            setCopyToProcessECSV(isProcessed, ecsv);
            System.out.println("Error file read ECSV");
            }else{
                isProcessed = checkOldECSV(ecsv);
                setCopyToProcessECSV(isProcessed, ecsv);
                System.out.println("Same dir read ECSV");
        }

        isEASC2BProcessed = getProcessedSequenceEASC(fileEASC);
        if ( isEASC2BProcessed == false){
            // Validate on EERR files
            isProcessed = eerr.validateSequence(easc.lastGeneratedFile());
            setCopyToProcessEASC(isProcessed, easc);
            System.out.println("Error file read EASC");
        }else {
            isProcessed = checkOldEASC(easc);
            setCopyToProcessEASC(isProcessed, easc);
            System.out.println("Same dir read EASC");
        }

        // Monitor folders

        fileCheck = isThereFile(copyToProcessEDNP);

        if ( fileCheck == false){
            Logs.genFileReport("Processed " + fileENDP);
        } else{
            monitorFolderEDNP = monitorForFiles(copyToProcessEDNP);
            if (monitorFolderEDNP == true){
                Logs.genFileReport("Processed " + fileENDP);
            }
        }

        if ( (fileCheck = isThereFile(copyToProcessEASC)) == false){
            Logs.genFileReport("Processed " + fileEASC);
        } else{
            monitorFolderEASC = monitorForFiles(copyToProcessEASC);
            if (monitorFolderEASC == true){
                Logs.genFileReport("Processed " + fileEASC);
            }
        }

        if ( (fileCheck = isThereFile(copyToProcessECSV)) == false){
            Logs.genFileReport("Processed " + fileECSV);
        } else{
            monitorFolderECSV = monitorForFiles(copyToProcessECSV);
            if (monitorFolderECSV == true){
                Logs.genFileReport("Processed " + fileECSV);
            }
        }
    }
}
