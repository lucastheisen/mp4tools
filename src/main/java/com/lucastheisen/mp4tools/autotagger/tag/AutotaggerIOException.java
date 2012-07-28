package com.lucastheisen.mp4tools.autotagger.tag;

public class AutotaggerIOException extends RuntimeException {
    private static final long serialVersionUID = -6873256452774404625L;

    public AutotaggerIOException( String message ) {
        super( message );
    }
    
    public AutotaggerIOException( Throwable t ) {
        super( t );
    }
    
    public AutotaggerIOException( String message, Throwable t ) {
        super( message, t );
    }
}
