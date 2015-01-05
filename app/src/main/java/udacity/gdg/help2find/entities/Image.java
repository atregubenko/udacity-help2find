package udacity.gdg.help2find.entities;

/**
 * Created by nnet on 28/12/14.
 */
public class Image {
    private long id;
    private Announcement announcement;
    private String imageUrl;

    public static final String IMAGE_ID_KEY ="id";
    public static final String IMAGE_ANNOUNCEMENT_ID_KEY ="announcement_id";
    public static final String IMAGE_URL_KEY ="image_url";

    public Image() {
    }

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Announcement getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(Announcement announcement) {
        this.announcement = announcement;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
