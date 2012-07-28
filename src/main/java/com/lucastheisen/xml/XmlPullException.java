package com.lucastheisen.xml;


public class XmlPullException extends RuntimeException {
    private static final long serialVersionUID = -3768056065903757519L;

    public XmlPullException( String message ) {
        super( message );
    }

    public XmlPullException( Throwable t ) {
        super( t );
    }

    public XmlPullException( String message, Throwable t ) {
        super( message, t );
    }
}
