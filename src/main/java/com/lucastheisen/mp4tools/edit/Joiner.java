package com.lucastheisen.mp4tools.edit;


import java.io.File;
import java.io.IOException;


public interface Joiner {
    public void join( File output, File... parts ) throws IOException;
}
