package com.lucastheisen.mp4tools.autotagger.tag;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TagInfo {
    private String amazonAsin;
    private List<String> cast;
    private List<String> directors;
    private String genre;
    private Image image;
    private String imdbId;
    private String iTunesUrl;
    private String longDescription;
    private String netflixId;
    private List<String> producers;
    private Rating rating;
    private String releaseDate;
    private List<String> screenWriters;
    private String shortDescription;
    private String tagChimpId;
    private String title;
    private String totalChapters;

    public TagInfo() {
    }

    public String getAmazonAsin() {
        return amazonAsin;
    }

    public List<String> getCast() {
        return cast;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public String getGenre() {
        return genre;
    }

    public Image getImage() {
        return image;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getITunesUrl() {
        return iTunesUrl;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getNetflixId() {
        return netflixId;
    }

    public List<String> getProducers() {
        return producers;
    }

    public Rating getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public List<String> getScreenWriters() {
        return screenWriters;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getTagChimpId() {
        return tagChimpId;
    }

    public String getTitle() {
        return title;
    }

    public String getTotalChapters() {
        return totalChapters;
    }

    public void setAmazonAsin( String amazonAsin ) {
        this.amazonAsin = amazonAsin;
    }

    public void setCast( String... cast ) {
        setCast( Arrays.asList( cast ) );
    }

    public void setCast( List<String> cast ) {
        this.cast = cast;
    }

    public void setDirectors( String... directors ) {
        setDirectors( Arrays.asList( directors ) );
    }

    public void setDirectors( List<String> directors ) {
        this.directors = directors;
    }

    public void setGenre( String genre ) {
        this.genre = genre;
    }

    public void setImage( InputStream inputStream, Image.Type type ) throws IOException {
        this.image = new Image( inputStream, type );
    }
    
    public void setImage( Image image ) {
        this.image = image;
    }

    public void setImdbId( String imdbId ) {
        this.imdbId = imdbId;
    }

    public void setITunesUrl( String iTunesUrl ) {
        this.iTunesUrl = iTunesUrl;
    }

    public void setLongDescription( String longDescription ) {
        this.longDescription = longDescription;
    }

    public void setNetflixId( String netflixId ) {
        this.netflixId = netflixId;
    }

    public void setProducers( String... producers ) {
        setProducers( Arrays.asList( producers ) );
    }

    public void setProducers( List<String> producers ) {
        this.producers = producers;
    }

    public void setRating( Rating rating ) {
        this.rating = rating;
    }

    public void setReleaseDate( String releaseDate ) {
        this.releaseDate = releaseDate;
    }

    public void setScreenWriters( String... screenWriters ) {
        setScreenWriters( Arrays.asList( screenWriters ) );
    }

    public void setScreenWriters( List<String> screenWriters ) {
        this.screenWriters = screenWriters;
    }

    public void setShortDescription( String shortDescription ) {
        this.shortDescription = shortDescription;
    }

    public void setTagChimpId( String tagChimpId ) {
        this.tagChimpId = tagChimpId;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public void setTotalChapters( String totalChapters ) {
        this.totalChapters = totalChapters;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append( "title=" )
                .append( title == null ? "<null>" : title )
                .append( ",releaseDate=" )
                .append( releaseDate == null ? "<null>" : releaseDate )
                .append( ",totalChapters=" )
                .append( totalChapters == null ? "<null>" : totalChapters )
                .append( ",cast=" )
                .append( cast == null ? "<null>" : cast )
                .append( ",imageUrl=" )
                .append( image == null ? "<null>" : image.getUrl() )
                .toString();
    }

    /**
     * All known ratings as listed <a href=
     * "http://shadowofged.blogspot.com/2008/06/itunes-content-ratings.html"
     * >here</a>.
     * 
     * @author ltheisen
     * 
     */
    public enum Rating {
        auMovieNotRated("au-movie", "Not Rated", "000"),
        auMovieG("au-movie", "G", "100"),
        auMoviePG("au-movie", "PG", "200"),
        auMovieM("au-movie", "M", "350"),
        auMovieMA15("au-movie", "MA 15+", "375"),
        auMovieR18("au-movie", "R18+", "400"),
        auMovieUnrated("au-movie", "Unrated", "???"),
        auTv("au-tv", "N/A", ""),
        caMovieNotRated("ca-movie", "Not Rated", "000"),
        caMovieG("ca-movie", "G", "100"),
        caMoviePG("ca-movie", "PG", "200"),
        caMovie14("ca-movie", "14", "325"),
        caMovie18("ca-movie", "18", "400"),
        caMovieR("ca-movie", "R", "500"),
        caMovieUnrated("ca-movie", "Unrated", "???"),
        caTvNotRated("ca-tv", "Not Rated", "000"),
        caTvC("ca-tv", "C", "100"),
        caTvC8("ca-tv", "C8", "200"),
        caTvG("ca-tv", "G", "300"),
        caTvPG("ca-tv", "PG", "400"),
        caTv14("ca-tv", "14+", "500"),
        caTv18("ca-tv", "18+", "600"),
        caTvUnrated("ca-tv", "Unrated", "???"),
        frMovie("fr-movie", "N/A", ""),
        frTvNotRated("fr-tv", "Not Rated", "000"),
        frTv10("fr-tv", "-10", "100"),
        frTv12("fr-tv", "-12", "200"),
        frTv16("fr-tv", "-16", "500"),
        frTv18("fr-tv", "-18", "600"),
        frTvUnrated("fr-tv", "Unrated", "???"),
        deMovie("de-movie", "N/A", ""),
        deTvNotRated("de-tv", "Not Rated", "000"),
        deTv6("de-tv", "ab 6 Jarhen", "100"),
        deTv12("de-tv", "ab 12 Jarhen", "200"),
        deTv16("de-tv", "ab 16 Jarhen", "500"),
        deTv18("de-tv", "ab 18 Jarhen", "600"),
        deTvUnrated("de-tv", "Unrated", "???"),
        nzMovieNotRated("nz-movie", "Not Rated", "000"),
        nzMovieG("nz-movie", "G", "100"),
        nzMoviePG("nz-movie", "PG", "200"),
        nzMovieM("nz-movie", "M", "300"),
        nzMovieR13("nz-movie", "R13", "325"),
        nzMovieR15("nz-movie", "R15", "350"),
        nzMovieR16("nz-movie", "R16", "375"),
        nzMovieR18("nz-movie", "R18", "400"),
        nzMovieR("nz-movie", "R", "500"),
        nzMovieUnrated("nz-movie", "Unrated", "???"),
        ukMovieNotRated("uk-movie", "Not Rated", "000"),
        ukMovieU("uk-movie", "U", "100"),
        ukMovieUc("uk-movie", "Uc", "150"),
        ukMoviePG("uk-movie", "PG", "200"),
        ukMovie12("uk-movie", "12", "300"),
        ukMovie12A("uk-movie", "12A", "325"),
        ukMovie15("uk-movie", "15", "350"),
        ukMovie18("uk-movie", "18", "400"),
        ukMovieE("uk-movie", "E", "600"),
        ukMovieUnrated("uk-movie", "Unrated", "???"),
        ukTvNotRated("uk-tv", "Not Rated", "000"),
        ukTvCaution("uk-tv", "CAUTION", "500"),
        ukTvUnrated("uk-tv", "Unrated", "???"),
        mpaaNotRated("mpaa", "Not Rated", "000"),
        mpaaG("mpaa", "G", "100"),
        mpaaPG("mpaa", "PG", "200"),
        mpaaPG13("mpaa", "PG-13", "300"),
        mpaaR("mpaa", "R", "400"),
        mpaaNC17("mpaa", "NC-17 (unverified)", "500"),
        mpaaUnrated("mpaa", "Unrated", "???"),
        usTvNotRated("us-tv", "Not Rated", "000"),
        usTvTVY("us-tv", "TV-Y", "100"),
        usTvTVY7("us-tv", "TV-Y7", "200"),
        usTvTVG("us-tv", "TV-G", "300"),
        usTvTVPG("us-tv", "TV-PG", "400"),
        usTvTV14("us-tv", "TV-14", "500"),
        usTvTVMA("us-tv", "TV-MA (unverified)", "600"),
        usTvUnrated("us-tv", "Unrated", "???");

        private String standard;
        private String rating;
        private String score;
        private static Map<String, Rating> lookup;

        static {
            lookup = new HashMap<>();
            for ( Rating rating : Rating.values() ) {
                lookup.put( new StringBuilder( getKey( rating.getStandard(), rating.getRating() ) ).toString(), rating );
            }
        }

        private Rating( String standard, String rating, String score ) {
            this.standard = standard;
            this.rating = rating;
            this.score = score;
        }

        public static Rating fromRating( String rating ) {
            return fromStandardAndRating( "mpaa", rating );
        }

        public static Rating fromStandardAndRating( String standard,
                String rating ) {
            return lookup.get( getKey( standard, rating ) );
        }

        private static String getKey( String standard, String rating ) {
            return new StringBuilder( standard ).append( "|" ).append( rating ).toString().toUpperCase();
        }

        public String getRating() {
            return rating;
        }

        public String getScore() {
            return score;
        }

        public String getStandard() {
            return standard;
        }

        @Override
        public String toString() {
            return new StringBuilder( standard )
                    .append( "|" )
                    .append( rating )
                    .append( "|" )
                    .append( score )
                    .toString();
        }
    }
}
