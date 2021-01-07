package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.SmarterAuthActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.SmarterSMBApplication;

/**
 * Created by Dilip on 1/11/2017.
 * Custom dialog for entire application
 */

public class CustomSingleButtonDialog {

    /*
     * Method to build a single button dialog window
     * The method can enable and disable the title of the dialog
     *
     * @Params String, String, Activity, boolean
     * @Return AlertDialog
     *
     */
    public static void buildSingleButtonDialog(String title, String message, final Activity activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);

        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        TextView btnYes = singleButtonDialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleButtonDialog.dismiss();
            }
        });

        singleButtonDialog.show();
    }

    public static void buildSingleButtonDialog(String title, String message, final Context activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(SmarterSMBApplication.applicationContext);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);

        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        TextView btnYes = singleButtonDialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleButtonDialog.dismiss();
            }
        });

        singleButtonDialog.show();
    }

    public static Dialog buildPermissionDialog(String title, String message, final Activity activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);
        TextView btn_yes = singleButtonDialog.findViewById(R.id.btn_yes);
        btn_yes.setVisibility(View.VISIBLE);
        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return singleButtonDialog;
    }

    public static Dialog multiDeviceSignOutDialog(final Activity activity) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("Signed Out");

        TextView btnYes = singleButtonDialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleButtonDialog.dismiss();
            }
        });

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText("You have signed in from another device," +
                " if not please change your password and contact the administrator");

        return singleButtonDialog;
    }

    public static void buildPasswordButtonDialog(String title, String message, final Activity activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);

        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        TextView btnYes = singleButtonDialog.findViewById(R.id.btn_yes);
        if(message.equalsIgnoreCase("Password Reset Successful")){
            btnYes.setVisibility(View.VISIBLE);
        }
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleButtonDialog.dismiss();
                SmarterSMBApplication.appSettings.setLogout(true);
                ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
                ApplicationSettings.putPref(AppConstants.CALL_RECORDING_OFF, true);
                ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
                Intent intent1 = new Intent(activity, SmarterAuthActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("screenType", 0);
                activity.startActivity(intent1);
                activity.finish();
            }
        });

        singleButtonDialog.show();
    }

    public static Dialog buildVersionDialog(String title, String message, final Context activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_single_button);

        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = singleButtonDialog.findViewById(R.id.tvDialogTitle);

        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        TextView tvMessage = singleButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return singleButtonDialog;
    }

    public static Dialog buildAlertDialog(String title, String message, final Activity activity, boolean withTitle) {
        final Dialog singleButtonDialog = new Dialog(activity);
        singleButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        singleButtonDialog.setContentView(R.layout.dialog_alert_button);
        singleButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 70);
        singleButtonDialog.getWindow().setBackgroundDrawable(inset);

        TextView tvTitle = (TextView) singleButtonDialog.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = (TextView) singleButtonDialog.findViewById(R.id.tvDialogMessage);
        TextView btn_yes = (TextView) singleButtonDialog.findViewById(R.id.btn_yes);

        Typeface titleFace = Typeface.createFromAsset(activity.getAssets(),"fonts/SF-UI-Display-Semibold.ttf");
        tvTitle.setTypeface(titleFace);
        tvMessage.setTypeface(titleFace);
        btn_yes.setTypeface(titleFace);

        btn_yes.setVisibility(View.VISIBLE);
        if (withTitle) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
        tvMessage.setText(message);
        return singleButtonDialog;
    }
}
