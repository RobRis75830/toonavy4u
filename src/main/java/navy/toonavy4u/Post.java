package navy.toonavy4u;

import entities.Comic;
import entities.Comments;
import entities.Reader;
import entities.Series;

import java.io.File;
import java.util.Date;
import java.util.List;

public class Post {
    private long id;
    private String username;
    private String password;
    private Date created_at;
    private Date updated_at;

    private String[] imageURL;
    private Reader reader;
    private Series series;
    private Comic comic;
    private List<String> categories;
    private Comments comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String title) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String[] getImageURL() { return imageURL; }

    public void setImageURL(String[] imageURL) { this.imageURL = imageURL; }

    public Reader getReader() { return reader; }

    public void setReader(Reader reader) { this.reader = reader; }

    public Series getSeries() { return series; }

    public void setSeries(Series series) { this.series = series; }

    public Comic getComic() { return comic; }

    public void setComic(Comic comic) { this.comic = comic; }

    public List<String> getCategories() { return categories; }

    public void setCategories(List<String> categories) { this.categories = categories; }

    public Comments getComment() { return comment; }

    public void setComment(Comments comment) { this.comment = comment; }
}
