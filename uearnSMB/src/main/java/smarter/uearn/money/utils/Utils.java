package smarter.uearn.money.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import smarter.uearn.money.chats.activity.ChatActivity;
import smarter.uearn.money.chats.fragment.GroupFragment;

public class Utils {

    public static String[] split(String original, String separator) {
        if (!check(original))
            return null;

        ArrayList<String> nodes = new ArrayList<String>();

        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            String str = original.substring(0, index);
            if (str != null) {
                str = str.trim();
                if (str.length() > 0) {
                    nodes.add(str);
                }
            }

            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        // Get the last node
        if (original != null) {
            original = original.trim();
            if (original.length() > 0) {
                nodes.add(original);
            }
        }

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = nodes.get(loop);
            }
        }

        nodes.clear();
        //nodes = null;
        return result;
    }

    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    public static String convertStreamToString(InputStream is) {
        try {
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                } finally {
                    is.close();
                }

                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean check(String str) {
        return !(str == null || str.length() <= 0);

    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        catch (SecurityException se) {
            CommonUtils.displayDialog(context);
        }
        finally {

            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static long getFileSize(Uri uri, Context context) {
        long size = 0;
        String path = getPath(uri, context);
        if (path != null) {
            File f = new File(path);
            size = f.length();
        }
        if (size < 0) {
            size = 0;
        }
        return size;
    }

    public static String getPath(final Uri uri, final Context context) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String ping(String url) {
        String str = "";
        try {
            java.lang.Process process = Runtime.getRuntime().exec(
                    "ping -c 1 " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            String op[] = new String[64];
            String delay[] = new String[8];
            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);
            reader.close();

            if (op.length > 0) {
                op = output.toString().split("\n");
                if (op.length > 1) {
                    if (op[1] != null && !op[1].isEmpty()) {
                        delay = op[1].split("time=");
                        if (delay.length > 0) {
                            if (delay.length > 1) {
                                if (delay[1] != null && !delay[1].isEmpty()) {
                                    str = delay[1];
                                    Log.i("Pinger", "Ping: " + delay[1]);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

    public static String pingAndCheckWifiConnectivityStatus() {
        String wifiStatus = "";
        String str = "";
        String pingResponse = Utils.ping("www.google.com");
        if (pingResponse != null && !pingResponse.isEmpty() && pingResponse.contains(".")) {
            str = pingResponse.split("\\.")[0];
        } else {
            str = pingResponse;
        }
        if (str != null && !str.isEmpty()) {
            str = str.replaceAll("\\s", "");
            str = getNumericString(str);
            int result = Integer.parseInt(str);
            if (result >= 50 && result <= 500) {
                wifiStatus = "You are on a weak internet connection, please switch to faster network";
            } else if (result > 500) {
                wifiStatus = "Poor connection. Please connect to reliable internet to start.";
            }
        } else {
            wifiStatus = "You have no internet connection";
        }
        return wifiStatus;
    }

    public static String getNumericString(String ourString) {
        String onlyNumericalString = "";
        StringBuilder neededCharacters = new StringBuilder();

        //Read throught the entire length of your input string
        for (int i = 0; i < ourString.length(); i++) {
            //Get the current character
            char ch = ourString.charAt(i);

            //Check if the character is a numerical
            if (Character.isDigit(ch)) {
                //if the character is a number then add it to our string
                // builder
                neededCharacters.append(ch);
            }
        }
        onlyNumericalString = neededCharacters.toString();
        return onlyNumericalString;
    }

    public static String getDateTime(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static String getChatTime(String serverTime) {
        /*String formated_date = "";
        System.out.print(serverTime);
        if(serverTime.length() != 0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("HH:mm a");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return  formated_date;*/
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
        Date calldate = new Date();
        try {
            calldate = df.parse(serverTime);
        } catch (Exception e) {
            //Log.e("SMSComposeActivity", e.toString());
        }

        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String timeStamp = format.format(calldate);
        return timeStamp;
    }

    public static String getDate(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("dd MMM yyyy");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static String getDateWithoutDay(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("MMM yyyy");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static String getDateBatchFormat(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("dd/MMM/yyyy");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static String getDateTrainingMessageFormat(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("MMM dd,yyyy");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static Date getDateTrainingMessageFormatDate(String serverTime) {
        Date date = null;
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("dd/MM/yyyy");

            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
                date = requiredformatter.parse(formated_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return date;
    }

    public static String compressImage(String imageUri, Context context) {

        String filePath = getRealPathFromURI(imageUri, context);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    private static String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static String getDateBatchDaysFormat(String serverTime) {
        String formated_date = "";
        System.out.print(serverTime);
        if (serverTime.length() != 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat requiredformatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = null;
            try {
                date = formatter.parse(serverTime);
                formated_date = requiredformatter.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        return formated_date;
    }

    public static boolean isStringEmpty(String input) {
        Boolean valid = false;
        if (input == null) {
            valid = true;
        } else if (input.trim().length() == 0) {
            valid = true;
        }
        return valid;
    }

    public static boolean isPanCardValid(String pan_number) {

        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher matcher = pattern.matcher(pan_number);
        // Check if pattern matches
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Function to validate Aadhar number.
    public static boolean isValidAadharNumber(String str) {
        // Regex to check valid Aadhar number.
        // String regex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
        Pattern aadhaarPattern = Pattern.compile("^[2-9]{1}[0-9]{11}$");
        // Compile the ReGex

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }
        // Pattern class contains matcher() method
        // to find matching between given string
        // and regular expression.
        Matcher m = aadhaarPattern.matcher(str);
        // Return if the string
        // matched the ReGex
        return m.matches();
    }



    // Function to validate Aadhar number.
    public static boolean isValidPassBookNumber(String str) {
        // Regex to check valid Aadhar number.
        // String regex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
        Pattern aadhaarPattern = Pattern.compile("[0-9]{9,18}");
        // Compile the ReGex

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }
        // Pattern class contains matcher() method
        // to find matching between given string
        // and regular expression.
        Matcher m = aadhaarPattern.matcher(str);
        // Return if the string
        // matched the ReGex
        return m.matches();
    }


    // Function to validate the pin code of India.
    public static boolean isValidPinCode(String pinCode) {
        /*    Regex to check valid pin code of India.
       Exaples:  132103: true ,  201 305: true
       */
        String regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        // If the pin code is empty
        // return false
        if (pinCode == null) {
            return false;
        }
        // Pattern class contains matcher() method
        // to find matching between given pin code
        // and regular expression.
        Matcher m = p.matcher(pinCode);

        // Return if the pin code
        // matched the ReGex
        return m.matches();
    }


    // Function to validate image file extension .
    public static boolean isImageFile(String str)
    {
        // Regex to check valid image file extension.
        String regex = "([^\\s]+(\\.(?i)(jpe?g|png|gif|bmp))$)";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }
        // Pattern class contains matcher() method
        // to find matching between given string
        // and regular expression.
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }
    public static File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp;
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        Log.e("Utils","Storage directory set");

        if (!storageDirectory.exists()) storageDirectory.mkdir();

        // Here we create the file using a prefix, a suffix and a directory
        File image = new File(storageDirectory, imageFileName + ".jpg");
        // File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);

        // Here the location is saved into the string mImageFileLocation
        Log.e("Utils","File name and path set");

        ChatActivity.imagePath = image.getAbsolutePath();
        // fileUri = Uri.parse(mImageFileLocation);
        // The file is returned to the previous intent across the camera application
        return image;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "CHAT_IMAGE");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Utils", "Oops! Failed create "
                        + "CHAT_IMAGE" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }

}
