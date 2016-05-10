package com.nandtotetris.jackcompiler;

import java.io.File;

/**
 * Static utility methods
 */
public class Utils {
    /**
     * Returns true if an input file has a jack extension.
     * Returns false otherwise.
     * @param input
     * @return true if the input file has jack extension
     *         false otherwise
     */
    public static Boolean isJackFile(File input) {

        String fileName = input.getAbsolutePath();
        String jackExtensionPattern = "(.*)(\\.jack)";

        return fileName.matches(jackExtensionPattern);
    }

    /**
     * Validates that the input is either
     * a file of the form xxx.jack, or a directory.
     * In case its a directory, validates that it
     * contains atleast one jack file.
     * Exits with an error message if that is not the
     * case.
     * @param input The input File object
     */
    public static void validateInput(File input) {

        if(input.isDirectory()) {

            File[] listOfInputFiles = input.listFiles();

            boolean hasJackFile = false;

            for(File file:listOfInputFiles) {

                if (isJackFile(file)) {
                    hasJackFile = true;
                    break;
                }

            }

            if (!hasJackFile) {
                System.out.println("Error: The input directory does not have a jack file");
                System.out.println("Exiting");
                System.exit(1);
            }

        }

        else {

            if (!isJackFile(input)) {
                System.out.println("Error: The input file does not have a jack extension");
                System.out.println("Exiting");
                System.exit(1);
            }

        }
    }
}
