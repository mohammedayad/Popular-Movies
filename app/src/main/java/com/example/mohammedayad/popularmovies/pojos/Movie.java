package com.example.mohammedayad.popularmovies.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mohammed.ayad on 2/28/2017.
 */

public class Movie implements Parcelable{
    private String posterPath;
    private boolean isAdult;
    private String overview;
    private String releaseDate;
    private String genreIds;
    private String id;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private String popularity;
    private String voteCount;
    private boolean isVideo;
    private String voteAverage;
    private int isFavoriteMovie;
    private String posterFullPath;

    public Movie() {

    }

    public Movie(String posterPath, boolean isAdult, String overview, String releaseDate, String genreIds, String id, String originalTitle, String originalLanguage, String title, String backdropPath, String popularity, String voteCount, boolean isVideo, String voteAverage) {
        this.posterPath = posterPath;
        this.isAdult = isAdult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.isVideo = isVideo;
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(String genreIds) {
        this.genreIds = genreIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getIsFavoriteMovie() {
        return isFavoriteMovie;
    }

    public void setIsFavoriteMovie(int isFavoriteMovie) {
        this.isFavoriteMovie = isFavoriteMovie;
    }

    public String getPosterFullPath() {
        return posterFullPath;
    }

    public void setPosterFullPath(String posterFullPath) {
        this.posterFullPath = posterFullPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{posterPath,overview,releaseDate,genreIds,id,originalTitle,originalLanguage,title,backdropPath,popularity,voteCount,voteAverage,posterFullPath});
        dest.writeBooleanArray(new boolean[]{isAdult,isVideo});
        dest.writeInt(isFavoriteMovie);

    }
    public Movie(Parcel in) {
        String[] stringData = new String[13];
        in.readStringArray(stringData);
        this.posterPath = stringData[0];
        this.overview = stringData[1];
        this.releaseDate = stringData[2];
        this.genreIds = stringData[3];
        this.id = stringData[4];
        this.originalTitle = stringData[5];
        this.originalLanguage = stringData[6];
        this.title = stringData[7];
        this.backdropPath = stringData[8];
        this.popularity = stringData[9];
        this.voteCount = stringData[10];
        this.voteAverage = stringData[11];
        this.posterFullPath=stringData[12];


        boolean[] booleanData=new boolean[2];
        in.readBooleanArray(booleanData);
        this.isAdult = booleanData[0];
        this.isVideo = booleanData[1];
        this.isFavoriteMovie=in.readInt();
    }

    public static final Parcelable.Creator CREATOR =new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Movie[size];
        }
    };
}
