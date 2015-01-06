package udacity.gdg.help2find.utils;

import android.util.Log;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by nnet on 28.12.14.
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();
    final static String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);

    public static <T> T parseJson(JsonNode node, Class<T> clazz) {
        try {
            return JsonUtils.defaultMapper().treeToValue(node, clazz);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse JSON entity " + clazz.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper sObjectMapper = new ObjectMapper();
    public static ObjectMapper defaultMapper() {
        return sObjectMapper;
    }

    public static SimpleDateFormat jsonDateFormat() {
        return dateFormat;
    }
}
