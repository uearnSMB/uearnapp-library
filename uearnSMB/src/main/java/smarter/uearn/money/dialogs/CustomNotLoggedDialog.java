package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;

/**
 * Created on 12/06/17.
 *
 * @author Dilip Kumar
 * @version 1.0
 */

public class CustomNotLoggedDialog {

    // private static String notLoggedString = "You have not signed into smarter biz would you like to add it as a lead?";

    public static void buildNotLoggedDialog(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_not_logged_in_lead, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        String number = ApplicationSettings.getPref(AppConstants.STORE_NUMBER_FOR_PROCESSING, "");

        String notLoggedString = activity.getString(R.string.not_logged_in_data);

        TextView displayInformation = dialogView.findViewById(R.id.tv_dialog_message);
        displayInformation.setText(number + "\n\n" + notLoggedString);

        // Load the sign in screen
        final Intent authenticationIntent = new Intent(activity, SmarterAuthActivity.class);
        authenticationIntent.putExtra("screenType", 0);

        // Set the event type to 1 such that the main screen will take care of adding the number
        // to personal sqlite database and sending the same number to the server
        TextView btnPersonal = dialogView.findViewById(R.id.btn_personal);
        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ApplicationSettings.putPref(AppConstants.NOT_LOGGED_EVENT_TYPE, 1);
                activity.startActivity(authenticationIntent);
            }
        });

        // Set the event type to 2 such that the main screen will take care of the loading part
        TextView btnLead = dialogView.findViewById(R.id.btn_lead);
        btnLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ApplicationSettings.putPref(AppConstants.NOT_LOGGED_EVENT_TYPE, 2);
                activity.startActivity(authenticationIntent);
            }
        });

        // If the user never wants to get bothered by the dialog again, then dismiss the dialog
        // Reset the preferences
        TextView btnNotAgain = dialogView.findViewById(R.id.btn_dont_remind);
        btnNotAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ApplicationSettings.putPref(AppConstants.STORE_NUMBER_FOR_PROCESSING, "");
                ApplicationSettings.putPref(AppConstants.NOT_LOGGED_EVENT_TYPE, 0);
                ApplicationSettings.putPref(AppConstants.DO_NOT_REMIND_NOT_LOGGED_IN, true);
            }
        });

        alertDialog.show();
    }
}
