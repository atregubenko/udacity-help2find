package udacity.gdg.help2find.entities;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nnet on 04.01.15.
 */
public class Category {
    private static final String CATEGORY_ID_KEY = "id";
    private static final String CATEGORY_NAME_KEY = "name";
    private static final String CATEGORY_PHOTO_KEY = "photo";
    private static final String CATEGORY_DESCRIPTION_KEY = "description";

    @JsonProperty(CATEGORY_ID_KEY)
    private long id;
    @JsonProperty(CATEGORY_NAME_KEY)
    private String name;
    @JsonProperty(CATEGORY_PHOTO_KEY)
    private String photo;
    @JsonProperty(CATEGORY_DESCRIPTION_KEY)
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
