package com.example.user.drawinggame.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class UI {

    public static int width;
    public static int height;

    public static void getWindowSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }


    private static final int FLAGS = View.SYSTEM_UI_FLAG_IMMERSIVE
//            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Hide the nav bar and status bar
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    public static void hideSystemUI(final View decorView) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(FLAGS);
        }
    }

    public static void hideSystemUI(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        final View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(FLAGS);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(FLAGS);
                    }
                }
            });
        }
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public static void showImmersiveModeDialog(Dialog dialog, Boolean cancel) {
        // 全螢幕 https://stackoverflow.com/questions/22794049/how-do-i-maintain-the-immersive-mode-in-dialogs
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.setCanceledOnTouchOutside(cancel);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        hideSystemUI(dialog.getWindow().getDecorView());

    }


    public static void fragmentSwitcher(Fragment fragment, Boolean back) {
        if (back) {
            MainActivity.fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            MainActivity.fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    // https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

//    public static void uploadImageToPHP(String url, Bitmap bm, String account, File file) {
//        try {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
//            byte [] byte_arr = stream.toByteArray();
//            String image_str = Base64.encodeBytes(byte_arr);
//            ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
//
//            OutputStream out = (OutputStream) new URL(url).getContent();
//            out.write(account.getBytes());
//            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//            return d;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    // https://stackoverflow.com/questions/5776851/load-image-from-url
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            Log.e("task", "change image");
        }
    }

    public static class SaveImageTask extends AsyncTask<String, Void, Bitmap> {
        private Context context;
        private ImageView bmImage;
        private String path;
        private String picName;

        public SaveImageTask(Context context, ImageView bmImage, String path, String picName) {
            this.context = context;
            this.bmImage = bmImage;
            this.path = path;
            this.picName = picName;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);

                path = saveToInternalStorage(bmp, context, picName);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            Log.e("path", path);
            try {
                loadImageFromStorage(bmImage, path, picName);
            } catch (FileNotFoundException e) {
                Log.e("Error",e.getMessage());
            }
        }
    }

    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    public static String saveToInternalStorage(Bitmap bitmapImage, Context context, String picName) {
        //cpntext fragment的context
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = new File(directory, picName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


    public static void loadImageFromStorage(ImageView img, String path, String picName) throws FileNotFoundException {
        Log.e("load", picName);

            File f = new File(path, picName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
    }
}
