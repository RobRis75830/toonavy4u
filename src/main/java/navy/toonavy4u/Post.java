package navy.toonavy4u;

import entities.*;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Post {
    private String username;

    private String[] imageURL;
    private Reader reader;
    private Series series;
    private Comic comic;
    private String[] categories;
    private Comments comment;
    private Rating rating;

    public String getUsername() {
        return username;
    }

    public void setUsername(String title) {
        this.username = username;
    }

    public String[] getImageURL() { return imageURL; }

    public void setImageURL(String[] imageURL) { this.imageURL = imageURL; }

    public Reader getReader() { return reader; }

    public void setReader(Reader reader) { this.reader = reader; }

    public Series getSeries() { return series; }

    public void setSeries(Series series) { this.series = series; }

    public Comic getComic() { return comic; }

    public void setComic(Comic comic) { this.comic = comic; }

    public String[] getCategories() { return categories; }

    public void setCategories(String[] categories) { this.categories = categories; }

    public Comments getComment() { return comment; }

    public void setComment(Comments comment) { this.comment = comment; }

    public Rating getRating() { return rating; }

    public void setRating(Rating rating) { this.rating = rating; }
}
