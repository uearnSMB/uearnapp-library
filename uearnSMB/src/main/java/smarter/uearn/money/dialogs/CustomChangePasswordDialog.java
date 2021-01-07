package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import smarter.uearn.money.models.ChangePasswordInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Validations;

/**
 * Created on 1/9/2017.
 *
 * @author Dilip
 * @version 1.5
 */

public class CustomChangePasswordDialog {

    private FirebaseAnalytics mFirebaseAnalytics;

    private String oldPassword, newPassword;
    private boolean mViewInput1 = true;
    private boolean mViewInput2 = true;
    private boolean mViewInput3 = true;

    public AlertDialog buildChangePasswordDialog(final Context context, final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);

        final EditText oldPasswordEditText, newPasswordEditText, reTypePasswordEditText;
        oldPasswordEditText = (EditText) dialogView.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = (EditText) dialogView.findViewById(R.id.newPasswordEditText);
        reTypePasswordEditText = (EditText) dialogView.findViewById(R.id.reTypePasswordEditText);

        final TextInputLayout tilOldPassword, tilNewPassword, tilRetypePassword;

        tilOldPassword = (TextInputLayout) dialogView.findViewById(R.id.tilOldPassword);
        tilOldPassword.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                tilOldPassword.requestFocus();
                tilOldPassword.requestFocusFromTouch();
                oldPasswordEditText.setCursorVisible(true);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tilOldPassword.getEditText().getRight() - tilOldPassword.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (mViewInput1) {
                            tilOldPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                            tilOldPassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_invisible, 0);
                            mViewInput1 = false;
                        } else {
                            tilOldPassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_show_password, 0);
                            tilOldPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mViewInput1 = true;
                        }

                        return true;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    oldPasswordEditText.requestFocus();
                    imm.showSoftInput(oldPasswordEditText, InputMethodManager.SHOW_FORCED);
                    return true;
                }
                return false;
            }
        });

        tilNewPassword = (TextInputLayout) dialogView.findViewById(R.id.tilNewPassword);
        tilNewPassword.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                tilNewPassword.requestFocus();
                tilNewPassword.requestFocusFromTouch();
                newPasswordEditText.setCursorVisible(true);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tilNewPassword.getEditText().getRight() - tilNewPassword.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (mViewInput3) {
                            tilNewPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                            tilNewPassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_invisible, 0);
                            mViewInput3 = false;
                        } else {
                            tilNewPassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_show_password, 0);
                            tilNewPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mViewInput3 = true;
                        }

                        return true;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    newPasswordEditText.requestFocus();
                    imm.showSoftInput(newPasswordEditText, InputMethodManager.SHOW_FORCED);
                    return true;
                }
                return false;
            }
        });

        tilRetypePassword = (TextInputLayout) dialogView.findViewById(R.id.tilRetypePassword);
        tilRetypePassword.getEditText().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                tilRetypePassword.requestFocus();
                tilRetypePassword.requestFocusFromTouch();
                reTypePasswordEditText.setCursorVisible(true);

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tilRetypePassword.getEditText().getRight() - tilRetypePassword.getEditText().getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (mViewInput2) {
                            tilRetypePassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                            tilRetypePassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_invisible, 0);
                            mViewInput2 = false;
                        } else {
                            tilRetypePassword.getEditText().setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_show_password, 0);
                            tilRetypePassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            mViewInput2 = true;
                        }

                        return true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    reTypePasswordEditText.requestFocus();
                    imm.showSoftInput(reTypePasswordEditText, InputMethodManager.SHOW_FORCED);
                    return true;
                }
                return false;
            }
        });

        TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logAnalytics("Cancel_Change_Password", "TextView");
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                alertDialog.dismiss();
            }
        });

        TextView btnUpdate = (TextView) dialogView.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logAnalytics("Update_Change_Password", "TextView");
                String retypePassword = null;
                try {
                    oldPassword = tilOldPassword.getEditText().getText().toString();
                    newPassword = tilNewPassword.getEditText().getText().toString();
                    retypePassword = tilRetypePassword.getEditText().getText().toString();

                    if (oldPassword != null &&  oldPassword.isEmpty()) {
                        tilOldPassword.setError("Please provide password");
                        tilOldPassword.setErrorEnabled(true);
                        return;
                    }

                    if (newPassword != null && ((newPassword.length() < 8) || newPassword.isEmpty())) {
                        tilNewPassword.setError("Please provide password with 8 characters");
                        tilNewPassword.setErrorEnabled(true);
                        return;
                    }

                    if (retypePassword != null && ((retypePassword.length() < 8) || retypePassword.isEmpty())) {
                        tilRetypePassword.setError("Please provide password with 8 characters");
                        tilRetypePassword.setErrorEnabled(true);
                        return;
                    }

                    if (!newPassword.equals(retypePassword)) {
                        tilRetypePassword.setError(activity.getString(R.string.password_mismatch));
                        tilRetypePassword.setErrorEnabled(true);
                        return;
                    }

                    if (newPassword != null && (newPassword.length() >= 8) && (retypePassword.length() >= 8)) {
                        changePassword(activity, oldPassword, newPassword, retypePassword);
                    }

                } catch (NullPointerException e) {

                }
                if(oldPassword.isEmpty() || retypePassword.isEmpty() || newPassword.isEmpty()) {

                } else  if (!Validations.validateTextLayout(tilOldPassword, 2) && !Validations.validateTextLayout(tilNewPassword, 2) && !Validations.validateTextLayout(tilRetypePassword, 2)) {
                    if (!newPassword.equals(retypePassword)) {
                        tilRetypePassword.setError(activity.getString(R.string.password_mismatch));
                        tilRetypePassword.setErrorEnabled(true);
                        return;
                    } else {
                        tilOldPassword.setErrorEnabled(false);
                        tilNewPassword.setErrorEnabled(false);
                        tilRetypePassword.setErrorEnabled(false);
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        alertDialog.dismiss();
                    }
                }
            }
        });

        return alertDialog;
    }

    private static void changePassword(final Activity activity, String oldpassword, String newPassword, String confirmPassword) {
        ChangePasswordInfo changePasswordInfo = new ChangePasswordInfo(SmarterSMBApplication.SmartUser.getId(), oldpassword, newPassword, confirmPassword);
        APIProvider.ChangePassword forgotPassword = new APIProvider.ChangePassword(changePasswordInfo, 1, activity, "Sending Values..", new API_Response_Listener<Void>() {
            @Override
            public void onComplete(Void data, long request_code, int failure_code) {
                if (failure_code == -1) {
                    CustomSingleButtonDialog.buildPasswordButtonDialog("Change password", "Password Reset Successful", activity, true);
                } else if (failure_code == APIProvider.INCORRECT_PASSWORD) {
                    CustomSingleButtonDialog.buildSingleButtonDialog("Change password", "In Correct Password, please try again", activity, true);
                    //failure
                } else if (failure_code == APIProvider.ERROR_READING_USER) {
                    CustomSingleButtonDialog.buildSingleButtonDialog("Change password", "Error reading user, please try again", activity, true);
                }
            }
        });
        forgotPassword.call();
    }

    private void logAnalytics(String eventName, String eventType) {
        Bundle AnalyticsBundle = new Bundle();
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_ID, AppConstants.FIREBASE_ID_9);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.ITEM_NAME, eventName);
        AnalyticsBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, eventType);
        mFirebaseAnalytics.logEvent(AppConstants.FIREBASE_ID_9, AnalyticsBundle);
    }
}
