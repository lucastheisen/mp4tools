package com.lucastheisen.mp4tools.console;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;


public class Console {
    private java.io.Console console;

    public Console() {
        this.console = System.console();
    }

    public void printf( String format, Object... args ) {
        if ( console == null ) {
            StringWriter stringWriter = new StringWriter();
            new PrintWriter( stringWriter ).printf( format, args );
            System.out.println( stringWriter.toString() );
            System.out.flush();
        }
        else {
            console.printf( format, args );
        }
    }

    public String readLine( String format, Object... args ) {
        if ( console == null ) {
            printf( format, args );
            try {
                return new BufferedReader( new InputStreamReader( System.in ) ).readLine();
            }
            catch ( IOException e ) {
                throw new RuntimeException( "unable to read user input: "
                        + e.getMessage(), e );
            }
        }
        else {
            return console.readLine( format, args );
        }
    }
}
