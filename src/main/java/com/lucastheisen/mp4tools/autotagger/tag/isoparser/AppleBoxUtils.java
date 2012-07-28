package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.coremedia.iso.boxes.ContainerBox;
import com.coremedia.iso.boxes.apple.AppleDataBox;
import com.coremedia.iso.boxes.apple.AppleGenericBox;
import com.coremedia.iso.boxes.apple.AppleMeanBox;
import com.coremedia.iso.boxes.apple.AppleNameBox;


public class AppleBoxUtils {
    public static final Charset CHARSET = Charset.forName( "UTF-8" );
    
    public static byte[] getData( ContainerBox box ) {
        return box.getBoxes( AppleDataBox.class ).get( 0 ).getData();
    }
    
    public static int getFlags( ContainerBox box ) {
        return box.getBoxes( AppleDataBox.class ).get( 0 ).getFlags();
    }

    public static String getMeaning( AppleGenericBox box ) {
        return box.getBoxes( AppleMeanBox.class ).get( 0 ).getMeaning();
    }

    public static String getName( AppleGenericBox box ) {
        return box.getBoxes( AppleNameBox.class ).get( 0 ).getName();
    }

    public static void setName( AppleGenericBox box, String name ) {
        List<AppleNameBox> appleNameBoxes = box.getBoxes( AppleNameBox.class );
        AppleNameBox appleNameBox = null;
        if ( appleNameBoxes.size() == 0 ) {
            appleNameBox = new AppleNameBox();
            box.addBox( appleNameBox );
        }
        else {
            appleNameBox = appleNameBoxes.get( 0 );
        }
        appleNameBox.setName( name );
    }

    public static void setMeaning( AppleGenericBox box, String meaning ) {
        List<AppleMeanBox> appleMeanBoxes = box.getBoxes( AppleMeanBox.class );
        AppleMeanBox appleMeanBox = null;
        if ( appleMeanBoxes.size() == 0 ) {
            appleMeanBox = new AppleMeanBox();
            box.addBox( appleMeanBox );
        }
        else {
            appleMeanBox = appleMeanBoxes.get( 0 );
        }
        appleMeanBox.setMeaning( meaning );
    }

    public static void setData( AppleGenericBox box, byte[] data ) {
        List<AppleDataBox> appleDataBoxes = box.getBoxes( AppleDataBox.class );
        AppleDataBox appleDataBox = null;
        if ( appleDataBoxes.size() == 0 ) {
            appleDataBox = new AppleDataBox();
            box.addBox( appleDataBox );
        }
        else {
            appleDataBox = appleDataBoxes.get( 0 );
        }
        appleDataBox.setData( data );
    }

    public static enum GenericMeaning {
        iTunes("com.apple.iTunes");

        private static Map<String, GenericMeaning> lookup;
        private String text;

        static {
            lookup = new HashMap<>();
            for ( GenericMeaning meaning : values() ) {
                lookup.put( meaning.getText(), meaning );
            }
        }

        private GenericMeaning( String text ) {
            this.text = text;
        }

        public static GenericMeaning forText( String text ) {
            return lookup.get( text );
        }

        public String getText() {
            return text;
        }
    }

    public static enum GenericName {
        iTunEXTC("iTunEXTC"),
        iTunMOVI("iTunMOVI");

        private static Map<String, GenericName> lookup;
        private String text;

        static {
            lookup = new HashMap<>();
            for ( GenericName name : values() ) {
                lookup.put( name.getText(), name );
            }
        }
        
        private GenericName( String text ) {
            this.text = text;
        }
        
        public static GenericName forText( String text ) {
            return lookup.get( text );
        }

        public String getText() {
            return text;
        }
    }

    public enum PlistSchema {
        plist,
        key,
        array,
        dict,
        string
    }
    
    public enum PlistKey {
        cast,
        directors,
        producers,
        screenwriters
    }
}
