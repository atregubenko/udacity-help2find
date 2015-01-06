package udacity.gdg.help2find.entities;

import android.text.TextUtils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import udacity.gdg.help2find.utils.JsonUtils;


/**
 * Created by nnet on 28.12.14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Announcement implements Serializable {
    public static final String ANNOUNCEMENT_ID_KEY ="id";
    public static final String ANNOUNCEMENT_ADDRESS_KEY ="address";
    public static final String ANNOUNCEMENT_CATEGORY_KEY ="category_id";
    public static final String ANNOUNCEMENT_DATE_KEY ="date";
    public static final String ANNOUNCEMENT_DESCRIPTION_KEY ="about";
    public static final String ANNOUNCEMENT_IS_LOST_KEY ="islost";
    public static final String ANNOUNCEMENT_LATITUDE_KEY ="latitude";
    public static final String ANNOUNCEMENT_LONGITUDE_KEY ="longitude";
    public static final String ANNOUNCEMENT_TITLE_KEY ="title";
    public static final String ANNOUNCEMENT_PREVIEW_IMAGE ="preview_image";
    public static final String ANNOUNCEMENT_CREATED_AT ="created_at";
    public static final String ANNOUNCEMENT_UPDATED_AT ="updated_at";

    public Announcement() {
    }

    @JsonProperty(ANNOUNCEMENT_ID_KEY)
    private long id;
    @JsonProperty(ANNOUNCEMENT_ADDRESS_KEY)
    private String address;
    @JsonProperty(ANNOUNCEMENT_CATEGORY_KEY)
    private int category;
    @JsonProperty(ANNOUNCEMENT_DATE_KEY)
    private long date;
    @JsonProperty(ANNOUNCEMENT_DESCRIPTION_KEY)
    private String description;
    @JsonProperty(ANNOUNCEMENT_IS_LOST_KEY)
    private boolean lost;
    @JsonProperty(ANNOUNCEMENT_LATITUDE_KEY)
    private double latitude;
    @JsonProperty(ANNOUNCEMENT_LONGITUDE_KEY)
    private double longitude;
    @JsonProperty(ANNOUNCEMENT_TITLE_KEY)
    private String title;
    @JsonProperty(ANNOUNCEMENT_PREVIEW_IMAGE)
    private String previewImageUrl;
    @JsonProperty(ANNOUNCEMENT_CREATED_AT)
    private long createdAt;
    @JsonProperty(ANNOUNCEMENT_UPDATED_AT)
    private long updatedAt;


    @JsonProperty("images")
    private Collection<Image> images;

    public void setImages(JsonNode imagesJsonNode) {
        try {
            images = JsonUtils.defaultMapper().readValue(imagesJsonNode.traverse(), new TypeReference<List<Image>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Image> getImages() {
        return images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(String dateValue) {
        if (!TextUtils.isEmpty(dateValue)) {
            try {
                date = JsonUtils.jsonDateFormat().parse(dateValue).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String dateValue) {
        if (!TextUtils.isEmpty(dateValue)) {
            try {
                this.createdAt = JsonUtils.jsonDateFormat().parse(dateValue).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String dateValue) {
        if (!TextUtils.isEmpty(dateValue)) {
            try {
                this.updatedAt = JsonUtils.jsonDateFormat().parse(dateValue).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
