package smarter.uearn.money.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import smarter.uearn.money.adapters.ScanLogDialogAdapter;
import smarter.uearn.money.models.ScanLog;
import smarter.uearn.money.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created on 22/05/17.
 *
 * @author Dilip Kumar
 */

public class CustomLogPicker implements ScanLogDialogAdapter.OnCallLogClicked {

    private RecyclerView rvCallLogs;
    public static String selectedNumber = "";
    private AlertDialog alertDialog;
    private TextView callReadError;

    public AlertDialog buildChangePasswordDialog(final Context context, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_call_logs, null);
        builder.setView(dialogView);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        rvCallLogs = dialogView.findViewById(R.id.rvCallLogs);

        callReadError = dialogView.findViewById(R.id.call_read_error);

        //getCallInformation(context, activity);

        return alertDialog;
    }

    @Override
    public void onLogItemClicked(String number) {
        selectedNumber = number;
        alertDialog.dismiss();
    }
}
