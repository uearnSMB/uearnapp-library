package smarter.uearn.money.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by Navine on 12/9/2015.
 */
public class FileCleanUp {
    private Context context;
    private File fileDir;
    private SQLiteDatabase db;
    private String dbName;

    public FileCleanUp(Context context, File fileDir, String dbName) {
        this.context = context;
        this.fileDir = fileDir;
        this.dbName = dbName;

    }

    public void traverseToCleanCallRecorder () {
        if (fileDir.exists()) {
            File[] files = fileDir.listFiles();

            // Fix for Android 6.0
            if (files == null) return;

            for (File file:files){
                //File file = files[i];
                deleteFileWithNoReference(file.toString());
            }

        }
    }

    private boolean deleteFileWithNoReference(String selectedFilePath) {
        boolean deleted = false;
        File file = new File(selectedFilePath);
        if (file.exists()) {
           String[] args={"FILE_PATH='"+selectedFilePath+"'"};
            //db Initialized By Srinath.k
            MySql dbHelper = MySql.getInstance(context);
            this.db = dbHelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM mytbl where FILE_PATH='" + selectedFilePath + "'", null);
            //Log.d("Files Deleted:","CursorCount"+c.getCount());
            if(c.getCount()==0){
                deleted = file.delete();
                //Log.d("Files Deleted:",""+deleted);

            }
            c.close();
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return deleted;
    }
}
