package com.lucastheisen.mp4tools.autotagger.tag;


public class RepositorySearchException extends RuntimeException {
    private static final long serialVersionUID = -7991398378850747047L;

    public RepositorySearchException( String message ) {
        super( message );
    }
    
    public RepositorySearchException( Throwable t ) {
        super( t );
    }
    
    public RepositorySearchException( String message, Throwable t ) {
        super( message, t );
    }
}
