package ex6.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Sjavac class that functions as a code verifier for s-Java files.
 */
public class Sjavac {

    private static final int NUMBER_VALID_ARGUMENTS = 1;
    private static final int GENERAL_ERROR_OUTPUT = 2;

    private static final String ERROR = " Error: ";
    private static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments";
    private static final String UNKNOWN_ERROR = "Unknown Error";


    private String sourceFilePath;


    public Sjavac(String sourceFileName){
        sourceFilePath = sourceFileName;
    }

    private void validateCode(){
        BufferedReader reader;

        File sourceFile = new File(sourceFilePath);

        try{
            reader = new BufferedReader(new FileReader(sourceFilePath));
            String line = reader.readLine();
        }
        catch (IOException error){
            printGeneralError(error.getMessage());
        }

    }

    public static void main(String[] args){

        //Check that the number of system arguments is valid
        if (args.length != NUMBER_VALID_ARGUMENTS){
            printGeneralError(INVALID_NUMBER_OF_ARGUMENTS);
        }

        Sjavac mySjavac = new Sjavac(args[0]);
        mySjavac.validateCode();

    }

    private static void printGeneralError(String errorMessage){
        if (errorMessage == null) {
            errorMessage = UNKNOWN_ERROR;
        }
        System.out.println(GENERAL_ERROR_OUTPUT);
        System.err.println(ERROR + errorMessage);
        System.exit(0);
    }
}
