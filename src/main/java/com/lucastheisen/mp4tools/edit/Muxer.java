package com.lucastheisen.mp4tools.edit;


import java.io.File;
import java.io.IOException;


public interface Muxer {
    public void mux( File output, File... input ) throws IOException;
}
