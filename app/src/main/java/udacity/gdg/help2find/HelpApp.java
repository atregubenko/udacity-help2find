package udacity.gdg.help2find;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by nnet on 02.01.15.
 */
public class HelpApp extends Application {
    private static HelpApp instance;
    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initImageLoader(getContext());
    }

    public void initImageLoader(Context context) {
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        imageLoader.init(config);
        imageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .build();
    }
    public static HelpApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    public static ImageLoader getImageLoader() {
        return instance.imageLoader;
    }

    public static void  displayImage(String imageUrl, ImageView imageView, int defaultImageRes) {
        displayImage(imageUrl, imageView, defaultImageRes, defaultImageRes);
    }

    public static void displayImage(String imageUrl, ImageView imageView, int loadingImageRes, int defaultImageRes) {
        instance.imageLoader.displayImage(imageUrl, imageView, new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingImageRes)
                .showImageForEmptyUri(defaultImageRes)
                .showImageOnFail(defaultImageRes)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build());
    }

    public static Uri getCachedImageUri(String imageUrl) {
        File src = DiskCacheUtils.findInCache(imageUrl, getImageLoader().getDiscCache());
        Uri bmpUri = null;
        if (src == null || src.length() == 0) {
            return bmpUri;
        }

        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image" + ".png");
            file.getParentFile().mkdirs();

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
