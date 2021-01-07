package smarter.uearn.money.utils;

import android.util.Log;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

/**
 * Created on 15/05/17.
 *
 * @author Dilip Kumar
 * @version 1.0
 */

public class AzureMediaUploader {

    // Dev end points
    /*public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=mediafilessmartdev;"
            + "AccountKey=ZWFIvbsb98UFvN0oVNWwgdE4Y8opBGbrSM036YtUXYejSfDK4Y0geCif/jQtrB3oZcrC4h6KEmCPx5W6Wuaohw==";*/

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=mediafilessmart;"
            + "AccountKey=5zv2t33LzF0B7LTTT6821GoYGvXgRFXWGVrT8wvyzu2IQThc/MNL00hObhJ9+lIjnRbSr8Losv1TFTjwhPFdSw==";

    public static String ufileName;

    /**
     * Method to return the location where the file has to be stored
     *
     * @param location if 1 then store as audio else store as image
     * @return returns a CloudBlobContainer
     * @throws Exception throws a storage exception
     */
    private static CloudBlobContainer getContainer(int location) throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = null;
        if (location == 1) {
            container = blobClient.getContainerReference("audios2017");
        } else if (location == 2) {
            container = blobClient.getContainerReference("ssrecordings");
        } else {
            container = blobClient.getContainerReference("ssimages");
        }

        return container;
    }

    /**
     * Method to upload the image by creating the name
     *
     * @param image       - The image that has to be uploaded
     * @param imageLength - size of the image
     * @return - name of the image
     * @throws Exception - upload exception
     */
    public static String UploadImage(InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer(0);

        container.createIfNotExists();

        String imageName = fileNameGeneration() + ".png";
        //String imageName = randomString(10) + ".png";
        //Log.d("Media image name", "image file:" + imageName);

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageName;

    }

    /**
     * Method to upload the image by creating the name
     *
     * @param image       - The image that has to be uploaded
     * @param imageLength - size of the image
     * @return - name of the image
     * @throws Exception - upload exception
     */
    public static String UploadProfilePic(InputStream image, int imageLength) throws Exception {
        CloudBlobContainer container = getContainer(0);

        container.createIfNotExists();

        String profileUserID = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
        String imageName = profileUserID + "-profile-image.png";

        CloudBlockBlob imageBlob = container.getBlockBlobReference(imageName);
        imageBlob.upload(image, imageLength);

        return imageName;

    }

    /**
     * Method to upload the image by creating the name
     *
     * @param audio       - The audio that has to be uploaded
     * @param audioLength - size of the audio file
     * @return - name of the audio file
     * @throws Exception - upload exception
     */
    public static String UploadAudio(InputStream audio, int audioLength) throws Exception {
        CloudBlobContainer container = getContainer(1);

        container.createIfNotExists();

        String audioName = ufileName + ".mp4";
        //String audioName = randomString(10) + ".mp4";

        //Log.d("Media audio name", "audio file:" + audioName);

        CloudBlockBlob imageBlob = container.getBlockBlobReference(audioName);
        imageBlob.upload(audio, audioLength);

        return audioName;
    }

    /**
     * Method to upload the image by creating the name
     *
     * @param audio       - The audio that has to be uploaded
     * @param audioLength - size of the audio file
     * @return - name of the audio file
     * @throws Exception - upload exception
     */
    public static String UploadNotesAudio(InputStream audio, int audioLength) throws Exception {
        CloudBlobContainer container = getContainer(2);

        container.createIfNotExists();

        String audioName = fileNameGeneration() + ".mp4";
        //String audioName = randomString(10) + ".mp4";

        //Log.d("Media notes audio name", "audio file:" + audioName);

        CloudBlockBlob imageBlob = container.getBlockBlobReference(audioName);
        imageBlob.upload(audio, audioLength);

        return audioName;
    }

    /**
     * Method to download the audio file for viewing
     *
     * @param name        - name of the audio file that has to be downloaded
     * @param audioStream - the audio file stream
     * @param audioLength - length of the audio file
     * @throws Exception - blob storage exception
     */
    public static void getAudio(String name, OutputStream audioStream, long audioLength) throws Exception {
        CloudBlobContainer container = getContainer(0);

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if (blob.exists()) {
            blob.downloadAttributes();

            audioLength = blob.getProperties().getLength();

            blob.download(audioStream);
        }
    }

    /**
     * Method to download the image for application processing
     *
     * @param name        - name of the image to be downloaded
     * @param imageStream - the stream through which the output should be
     * @throws Exception - blob storage exception
     */
    public static void GetImage(String name, OutputStream imageStream) throws Exception {
        CloudBlobContainer container = getContainer(0);

        CloudBlockBlob blob = container.getBlockBlobReference(name);

        if (blob.exists()) {
            blob.downloadAttributes();
            blob.download(imageStream);
        }
    }

    static final String validChars = "abcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    /**
     * Method to create a random name
     *
     * @param len - length
     * @return - a string which is random
     */
    static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(validChars.charAt(rnd.nextInt(validChars.length())));
        return sb.toString();
    }

    public static String fileNameGeneration() {

        String user_id = "", date = "";

        if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
            user_id = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
            Calendar cal = Calendar.getInstance();
            Date d = new Date();
            cal.setTime(d);
            Date startTime = cal.getTime();
            String requiredStartTime = CommonUtils.getTimeFormatInISO(startTime);
            requiredStartTime = requiredStartTime.replace(":", "-");
            requiredStartTime = requiredStartTime.replace(".", "-");
            //Log.d("requiredStartTime" , requiredStartTime);
            date = requiredStartTime;
        }

        ufileName = user_id + date;
        return ufileName;
    }
}