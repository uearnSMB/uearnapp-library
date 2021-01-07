package smarter.uearn.money.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * util class to resize bitmap as per required sizes
 */
public class ResizeBitmapUtil {

    public static Bitmap resizeBitmap(Bitmap originalBitmap, int requiredWidth, int requiredHeight) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        float scaleWidth = ((float) requiredWidth) / width;
        float scaleHeight = ((float) requiredHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                originalBitmap, 0, 0, width, height, matrix, false);
        originalBitmap.recycle();
        return resizedBitmap;
    }
}
