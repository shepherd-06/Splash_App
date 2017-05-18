package ninja.ibtehaz.splash.utility;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import ninja.ibtehaz.splash.R;
import ninja.ibtehaz.splash.background.DownloadManager;
import ninja.ibtehaz.splash.background.InternalDownloadService;
import ninja.ibtehaz.splash.db_helper.SplashDb;
import ninja.ibtehaz.splash.models.SplashDbModel;

/**
 * Created by ibtehaz on 2/20/2017.
 */

public class Util {

    public static String NOTIFICATION_BROADCAST_CONSTANT = "14442";
    public static String NOTIFICATION_BROADCAST_ID_EXTRA = "uniqueId";

    public static String EXTRA_SERVICE_DATA_ID = "data_id";
    public static String EXTRA_SERVICE_CURRENT_INDEX = "current_index";
    public static String EXTRA_SERVICE_DATA_MODEL = "data_model";
    public static String EXTRA_SERVICE_CURRENT_DOWNLOAD_URL = "download_url";


    /**
     * singleton instance of Util to handle notification stuffs
     * @return
     */
    public Util getInstance() {
        return new Util();
    }

    /**
     *
     * @param context
     * @param parentView
     * @param message
     */
    public void showSnackbar(Context context, View parentView, String message) {
        Snackbar snackbar = Snackbar
                .make(parentView, message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(context.getResources().getDimension(R.dimen.text_12sp));
        snackbar.show();
    }


    /**
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            Log.e("hideSoftKeyboard", "Has focus to close" + view.getTag());
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (inputManager != null) {
                if (Build.VERSION.SDK_INT < 11) {
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                            0);
                } else {
                    if (activity.getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(activity
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    view.clearFocus();
                }
                view.clearFocus();
            }
        } else {
            Log.e("focus", "No focus to close");
        }
    }


    /**
     *
     * @param context
     * @return
     */
    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * only load image
     * @param context
     * @param url
     * @param img
     */
    public void loadImage(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .fitCenter()
                .into(img);
    }

    /**
     *
     * @param context
     * @param url
     * @param img
     */
    public void loadImage(Context context, String url, final ImageView img, final ImageView imgLayer) {
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (imgLayer != null)imgLayer.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imgLayer != null)imgLayer.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }


    /**
     *
     * @param context
     * @param url
     * @param img
     * @param progressBar
     */
    public void loadImage(Context context, String url, final ImageView img, final ProgressBar progressBar) {
        Glide.with(context)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }


    /**
     * loads profile pic only
     * @param context
     * @param url
     * @param img
     */
    public void loadProfilePic(Context context, String url, ImageView img) {
        Glide.with(context)
                .load(url)
                .into(img);
    }


    /**
     *
     * @param message
     * @param context
     */
    public void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**
     * creates a scrollable website.
     * sets up wallpaper into an image
     * @param context
     * @param image
     */
    public boolean setupWallpaper(Context context, Bitmap image) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        width *= 2;
        boolean isSuccess = false;
        try {
            if (image == null) {
                makeToast(context, "Image is null!");
            } else {
                float scale = width / (float) image.getWidth();
                height = (int) (scale * image.getHeight());
                Bitmap scaledImage = Bitmap.createScaledBitmap(image, width,height, true);
                Log.d("RetrieveFeed", "Scaled Image size "+scaledImage.getByteCount());
                wallpaperManager.setBitmap(scaledImage);
                isSuccess = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return isSuccess;
    }


    /**
     * uses a service to download and later sets it up as wallpaper.
     * @param downloadUrl
     * @param context
     */
    public void setupWallpaperFromBackground(Context context, String downloadUrl) {
        Intent i = new Intent(context, DownloadManager.class);
        i.putExtra(Util.EXTRA_SERVICE_CURRENT_DOWNLOAD_URL, downloadUrl);
        context.startService(i);
    }


    /**
     * returns the capitalize version of a string/each string
     * @param word
     * @return
     */
    public String capitalizeWords(String word) {
        String []words = word.split(" ");
        String returnVal = "";
        for (int i =0; i < words.length; i++) {
            returnVal += words[i].substring(0,1).toUpperCase()
                    + words[i].substring(1).toLowerCase() +" ";
        }
        return returnVal;
    }


    /**
     * @deprecated
     * @see InternalDownloadService
     * @Link startInternalImageDownload()
     * downloads the image from internet to Internal storage
     * Filename has to be sent to database based on ID
     * This function will be called from splashDB to download the image
     * @see SplashDb
     * @param rawUrl | raw url of the current photo
     * @param databaseId | sqlite unique id
     * @param context
     */
    public void downloadImageToStore(String rawUrl, long databaseId, Context context) {
        RetrieveFeed retrieveFeed = new RetrieveFeed(context, null, true, databaseId);
        retrieveFeed.execute(rawUrl);
    }


    /**
     * store images in android's internal storage
     * this function will be called from RetrieveFeed Async class to load image on background rather than
     * foreground
     * @see InternalDownloadService
     * @param image | just downloaded image from the server
     * @param context
     * @param dataId | sqlite primary key
     */
    public void storeImageInternalStorage(Bitmap image, Context context, long dataId) {
        String TAG = "InternalStorage";
        try {
            String fileName = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + ""
                    + Calendar.getInstance().get(Calendar.MINUTE) + ""
                    + Calendar.getInstance().get(Calendar.SECOND) + ""
                    + Calendar.getInstance().get(Calendar.MILLISECOND)+".jpeg";

            // path to /data/data/yourapp/app_data/splashDir
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.close();

            //load based on id
            new SplashDb().updateFileName(fileName, dataId);
            Log.d(TAG, "FileName: "+fileName + " for "+dataId + " and bitmap size is "+image.getByteCount());

            //send a broadcast from here that the download is complete
            Intent intent = new Intent(Util.NOTIFICATION_BROADCAST_CONSTANT);
            intent.putExtra(Util.NOTIFICATION_BROADCAST_ID_EXTRA, dataId);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            Log.d(TAG, "------------- Broadcast fired -------------");
        } catch (IOException e) {
            Log.d(TAG, "Exception "+e.toString());
            e.printStackTrace();
        } catch (Exception exc) {
            Log.d(TAG, "Exception "+exc.toString());
            exc.printStackTrace();
        }
    }


    /**
     * pulls the Internally stored photo from local storage and show it in an ImageView
     * @param fileName | name of the JPEG file.
     * @param context
     * @param imgPreview | to preview
     */
    public void getInternalStorageImage(String fileName, Context context, ImageView imgPreview) {
        try {
            File f = new File(context.getFilesDir(), fileName);
            Bitmap output = BitmapFactory.decodeStream(new FileInputStream(f));
            imgPreview.setImageBitmap(output);
        } catch (IOException iox) {
            iox.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     * this function will start downloading photos for internal/in app storage.
     * this function will use notification to keep track of downloading.
     * this function works on background thread of another process which is separated from main process.
     * @param data contains SplashDB data
     * @param context handling context to put/do stuffs
     */
    public void startInternalImageDownload(ArrayList<SplashDbModel> data, Context context) {
        Intent i = new Intent(context, InternalDownloadService.class);
        SplashDbModel local = new SplashDbModel();
        local.setSplashDbModels(data);
        i.putExtra("data", local);
        context.startService(i);
    }
}
