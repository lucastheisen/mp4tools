package com.lucastheisen.mp4tools.autotagger.tag.isoparser;


import java.util.List;


import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.apple.AppleArtistBox;
import com.coremedia.iso.boxes.apple.AppleCoverBox;
import com.coremedia.iso.boxes.apple.AppleCustomGenreBox;
import com.coremedia.iso.boxes.apple.AppleDescriptionBox;
import com.coremedia.iso.boxes.apple.AppleGenericBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.apple.AppleRecordingYearBox;
import com.coremedia.iso.boxes.apple.AppleSynopsisBox;
import com.coremedia.iso.boxes.apple.AppleTrackTitleBox;
import com.lucastheisen.mp4tools.autotagger.tag.Image;
import com.lucastheisen.mp4tools.autotagger.tag.TagInfo;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleBoxUtils.GenericMeaning;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleBoxUtils.GenericName;
import com.lucastheisen.mp4tools.autotagger.tag.isoparser.AppleBoxUtils.PlistKey;


public class AppleItemListBoxWriter implements BoxWriter<MetaBox> {
    private void appendListToDict( StringBuilder builder, PlistKey key,
            List<String> values ) {
        builder.append( "    <key>" ).append( key.toString() ).append( "</key>\n" )
                .append( "    <array>\n" );
        for ( String value : values ) {
            builder.append( "      <dict>\n" )
                    .append( "        <key>name</key>\n" )
                    .append( "        <string>" ).append( value ).append( "</string>\n" )
                    .append( "      </dict>\n" );
        }
        builder.append( "    </array>\n" );
    }

    private String buildPlist( TagInfo tagInfo ) {
        // TODO: refactor to use a better xml build, probably XMLStreamWriter
        StringBuilder builder = new StringBuilder()
                .append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" )
                .append( "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" )
                .append( "<plist version=\"1.0\">\n" )
                .append( "  <dict>\n" );
        if ( tagInfo.getCast() != null ) {
            appendListToDict( builder, PlistKey.cast, tagInfo.getCast() );
        }
        if ( tagInfo.getDirectors() != null ) {
            appendListToDict( builder, PlistKey.directors, tagInfo.getDirectors() );
        }
        if ( tagInfo.getProducers() != null ) {
            appendListToDict( builder, PlistKey.producers, tagInfo.getProducers() );
        }
        if ( tagInfo.getScreenWriters() != null ) {
            appendListToDict( builder, PlistKey.screenwriters, tagInfo.getScreenWriters() );
        }
        builder.append( "  </dict>\n" )
                .append( "</plist>" );
        return builder.toString();
    }

    @Override
    public void write( MetaBox metaBox, TagInfo tagInfo ) {
        List<AppleItemListBox> appleItemListBoxes = metaBox.getBoxes( AppleItemListBox.class );
        AppleItemListBox appleItemListBox = null;
        if ( appleItemListBoxes.size() == 0 ) {
            appleItemListBox = new AppleItemListBox();
            metaBox.addBox( appleItemListBox );
        }
        else {
            appleItemListBox = appleItemListBoxes.get( 0 );
        }
        appleItemListBox.getBoxes( AppleTrackTitleBox.class );

        // Prepare generic boxes...
        AppleGenericBox iTunEXTC = null;
        AppleGenericBox iTunMOVI = null;
        for ( AppleGenericBox appleGenericBox : appleItemListBox.getBoxes( AppleGenericBox.class ) ) {
            GenericName name = GenericName.forText( AppleBoxUtils.getName(
                    appleGenericBox ) );
            if ( name != null ) {
                switch ( name ) {
                case iTunEXTC:
                    iTunEXTC = appleGenericBox;
                    break;
                case iTunMOVI:
                    iTunMOVI = appleGenericBox;
                    break;
                }
            }
        }

        if ( tagInfo.getRating() != null ) {
            if ( iTunEXTC == null ) {
                iTunEXTC = new AppleGenericBox();
                appleItemListBox.addBox( iTunEXTC );
                AppleBoxUtils.setName( iTunEXTC, GenericName.iTunEXTC.getText() );
                AppleBoxUtils.setMeaning( iTunEXTC, GenericMeaning.iTunes.getText() );
            }
            AppleBoxUtils.setData( iTunEXTC, tagInfo.getRating().toString().getBytes( AppleBoxUtils.CHARSET ) );
        }

        if ( tagInfo.getCast() != null || tagInfo.getDirectors() != null
                || tagInfo.getProducers() != null
                || tagInfo.getScreenWriters() != null ) {
            if ( iTunMOVI == null ) {
                iTunMOVI = new AppleGenericBox();
                appleItemListBox.addBox( iTunMOVI );
                AppleBoxUtils.setName( iTunMOVI, GenericName.iTunMOVI.getText() );
                AppleBoxUtils.setMeaning( iTunMOVI, GenericMeaning.iTunes.getText() );
            }
            AppleBoxUtils.setData( iTunMOVI, buildPlist( tagInfo ).getBytes( AppleBoxUtils.CHARSET ) );
        }

        if ( tagInfo.getTitle() != null ) {
            List<AppleTrackTitleBox> appleTrackTitleBoxes = appleItemListBox.getBoxes( AppleTrackTitleBox.class );
            if ( appleTrackTitleBoxes.size() == 0 ) {
                AppleTrackTitleBox appleTrackTitleBox = new AppleTrackTitleBox();
                appleTrackTitleBox.setValue( tagInfo.getTitle() );
                appleItemListBox.addBox( appleTrackTitleBox );
            }
            else {
                appleTrackTitleBoxes.get( 0 ).setValue( tagInfo.getTitle() );
            }
        }

        if ( tagInfo.getCast() != null ) {
            List<AppleArtistBox> appleArtistBoxes = appleItemListBox.getBoxes( AppleArtistBox.class );
            StringBuilder castBuilder = new StringBuilder();
            boolean first = true;
            for ( String castMember : tagInfo.getCast() ) {
                if ( first ) {
                    first = false;
                }
                else {
                    castBuilder.append( "," );
                }
                castBuilder.append( castMember );
            }
            if ( appleArtistBoxes.size() == 0 ) {
                AppleArtistBox appleArtistBox = new AppleArtistBox();
                appleArtistBox.setValue( castBuilder.toString() );
                appleItemListBox.addBox( appleArtistBox );
            }
            else {
                appleArtistBoxes.get( 0 ).setValue( castBuilder.toString() );
            }
        }

        if ( tagInfo.getGenre() != null ) {
            List<AppleCustomGenreBox> genreBoxes = appleItemListBox.getBoxes( AppleCustomGenreBox.class );
            if ( genreBoxes.size() == 0 ) {
                AppleCustomGenreBox genreBox = new AppleCustomGenreBox();
                genreBox.setValue( tagInfo.getGenre() );
                appleItemListBox.addBox( genreBox );
            }
            else {
                genreBoxes.get( 0 ).setValue( tagInfo.getGenre() );
            }
        }

        if ( tagInfo.getReleaseDate() != null ) {
            List<AppleRecordingYearBox> recordingYearBoxes = appleItemListBox.getBoxes( AppleRecordingYearBox.class );
            if ( recordingYearBoxes.size() == 0 ) {
                AppleRecordingYearBox recordingYearBox = new AppleRecordingYearBox();
                recordingYearBox.setValue( tagInfo.getReleaseDate() );
                appleItemListBox.addBox( recordingYearBox );
            }
            else {
                recordingYearBoxes.get( 0 ).setValue( tagInfo.getReleaseDate() );
            }
        }

        if ( tagInfo.getShortDescription() != null ) {
            List<AppleDescriptionBox> descriptionBoxes = appleItemListBox.getBoxes( AppleDescriptionBox.class );
            if ( descriptionBoxes.size() == 0 ) {
                AppleDescriptionBox descriptionBox = new AppleDescriptionBox();
                descriptionBox.setValue( tagInfo.getShortDescription() );
                appleItemListBox.addBox( descriptionBox );
            }
            else {
                descriptionBoxes.get( 0 ).setValue( tagInfo.getShortDescription() );
            }
        }

        if ( tagInfo.getLongDescription() != null ) {
            List<AppleSynopsisBox> synopsisBoxes = appleItemListBox.getBoxes( AppleSynopsisBox.class );
            if ( synopsisBoxes.size() == 0 ) {
                AppleSynopsisBox synopsisBox = new AppleSynopsisBox();
                synopsisBox.setValue( tagInfo.getLongDescription() );
                appleItemListBox.addBox( synopsisBox );
            }
            else {
                synopsisBoxes.get( 0 ).setValue( tagInfo.getLongDescription() );
            }
        }

        if ( tagInfo.getImage() != null ) {
            List<AppleCoverBox> coverBoxes = appleItemListBox.getBoxes( AppleCoverBox.class );
            Image image = tagInfo.getImage();
            byte[] bytes = image.getBytes();
            Image.Type type = image.getType();

            if ( bytes != null && type != null ) {
                AppleCoverBox coverBox = null;
                if ( coverBoxes.size() == 0 ) {
                    coverBox = new AppleCoverBox();
                    appleItemListBox.addBox( coverBox );
                }
                else {
                    coverBox = coverBoxes.get( 0 );
                }

                switch ( type ) {
                case png:
                    coverBox.setPng( image.getBytes() );
                    break;
                case jpg:
                    coverBox.setJpg( image.getBytes() );
                    break;
                }
            }
        }
    }
}
