package com.lucastheisen.perl;

public class PerlHashTypeException extends RuntimeException {
    private static final long serialVersionUID = -380006399459147225L;

    public PerlHashTypeException( String message ) {
        super( message );
    }
    
    public PerlHashTypeException( Throwable t ) {
        super( t );
    }
    
    public PerlHashTypeException( String message, Throwable t ) {
        super( message, t );
    }
}
