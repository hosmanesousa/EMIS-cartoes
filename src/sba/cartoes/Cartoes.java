/*
package sba.cartoes;

import java.io.File;

public class Cartoes {

    private String path;

    public Cartoes(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String [] fileOnFolder() {

        File file = new File(getPath());
        if ( file.list().length == 0) {
            System.out.println("No files on this folder");
        }

        String [] fileName = file.list();

        return fileName;
    }

    public static void main(String[] args) {

        Cartoes readFiles = new Cartoes("/EMIS-cartoes/EMISout/");
        String [] array = readFiles.fileOnFolder();

        int count = 0;
        for ( String element: array) {
            count++;
            System.out.println("Element # " + count + "-->" + element);
        }

    }
}
*/