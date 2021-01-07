package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebStorage;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.broadcastReceiver.Alarm_Receiver;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.MySql;
import smarter.uearn.money.utils.SmarterSMBApplication;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Dilip on 1/9/2017.
 */

public class CustomTwoButtonDialog {

    public static Dialog buildOneButtonDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.dialog_common_one_button);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(title);

        TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return twoButtonDialog;
    }


    public static Dialog buildTwoButtonDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.dialog_common_two_button);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(title);

        TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return twoButtonDialog;
    }

    public static Dialog buildTwoButtonDialog(Context context, String title, String message) {
        if(context != null) {
            final Dialog twoButtonDialog = new Dialog(context);
            twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            twoButtonDialog.setContentView(R.layout.dialog_common_two_button);

            twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
            tvTitle.setText(title);

            TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
            tvMessage.setText(message);

            return twoButtonDialog;
        } else {
            return null;
        }
    }

    private static Dialog dailogObject(Context context) {
        final Dialog twoButtonDialog = new Dialog(context);
        return twoButtonDialog;
    }

    private static Dialog dailogObject(Activity activity) {
        final Dialog twoButtonDialog = new Dialog(activity);
        return twoButtonDialog;
    }

    private static void deleteAppData(Activity activity) {
        //activity.deleteDatabase("mydb");
    }

    public static Dialog exitApplicationDialog(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildTwoButtonDialog(activity, title, message);

        TextView btnNo = exitDialog.findViewById(R.id.btn_no);
        btnNo.setText("CANCEL");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
        btnYes.setText("YES");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
                deleteAppData(activity);
                SmarterSMBApplication.currentAppState = "Logged Out";
                NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                WebStorage webstorage = WebStorage.getInstance();
                webstorage.deleteAllData();
                SmarterSMBApplication.appSettings.setLogout(true);

                ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, "");
                ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, "");
                ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
                ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
                ApplicationSettings.putPref(AppConstants.APP_UPDATE, false);
                ApplicationSettings.putLongPref(AppConstants.notificationCount, 0);
                ApplicationSettings.putPref(AppConstants.MY_PROFILE_PROJECT_TYPE, "");

                String remoteAutoEnabled = ApplicationSettings.getPref(AppConstants.REMOTE_AUTO_DIALLING, "");
                if (remoteAutoEnabled != null && !remoteAutoEnabled.isEmpty()) {
                    ApplicationSettings.putPref(AppConstants.RAD_MESSAGE_VALUE, "");
                    ApplicationSettings.putPref(AppConstants.REMOTE_AUTO_DIALLING,"");
                }

                if (UearnHome.remoteAutoDialingStartClicked) {
                    UearnHome.remoteAutoDialingStartClicked = false;
                }

                ApplicationSettings.putPref(AppConstants.TOTAL_LOGGED_IN_TIME, 0l);
                ApplicationSettings.putPref(AppConstants.TOTAL_ACTIVE_TIME, 0l);
                SmarterSMBApplication.totalLoggedInTime = "";
                SmarterSMBApplication.totalActiveTime = "";

                String userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase("On Board") || userStatus.equalsIgnoreCase("OJT") || userStatus.equalsIgnoreCase("Production") || userStatus.equalsIgnoreCase("Project"))) {
                    Intent intent3 = new Intent(activity, UearnHome.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.putExtra("EXIT", true);
                    activity.startActivity(intent3);
                    activity.finish();
                } else {
                    String userEmail = "";
                    String accessToken = "";
                    String after_call_screen = "";
                    String userId = "";
                    String userGender = "";
                    String userDOB = "";
                    String userPhone = "";

                    if (ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") != null) {
                        userEmail = ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "");
                    }

                    if (ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "") != null) {
                        userPhone = ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "");
                    }

                    if (ApplicationSettings.getPref(AppConstants.USERINFO_GENDER, "") != null) {
                        userGender = ApplicationSettings.getPref(AppConstants.USERINFO_GENDER, "");
                    }

                    if (ApplicationSettings.getPref(AppConstants.AGENT_DOB, "") != null) {
                        userDOB = ApplicationSettings.getPref(AppConstants.AGENT_DOB, "");
                    }

                    if (ApplicationSettings.getPref("accessToken", "") != null) {
                        accessToken = ApplicationSettings.getPref("accessToken", "");
                    }

                    if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") != null) {
                        userId = ApplicationSettings.getPref(AppConstants.USERINFO_ID, "");
                    }

                    if (ApplicationSettings.getPref(AppConstants.USER_STATUS, "") != null) {
                        userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
                    }

                    SharedPreferences.Editor editor = ApplicationSettings.getEditor();
                    if (editor != null) {
                        editor.clear().commit();
                    }

                    ApplicationSettings.putPref(AppConstants.USERINFO_ID, userId);
                    ApplicationSettings.putPref(AppConstants.USERINFO_EMAIL, userEmail);
//                    ApplicationSettings.putPref(AppConstants.USERINFO_PHONE, userPhone);
//                    ApplicationSettings.putPref(AppConstants.USERINFO_GENDER, userGender);
//                    ApplicationSettings.putPref(AppConstants.AGENT_DOB, userDOB);
//                    ApplicationSettings.putPref("accessToken", accessToken);
                    ApplicationSettings.putPref(AppConstants.APP_LOGOUT, true);
//                    ApplicationSettings.putPref(AppConstants.USER_STATUS, userStatus);
                    activity.finishAffinity();
                }
            }
        });
        return exitDialog;
    }

    public static Dialog stopCallingAndMovetoHomeScreenDialog(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildTwoButtonDialog(activity, title, message);

        TextView btnNo = exitDialog.findViewById(R.id.btn_no);
        btnNo.setText("CANCEL");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
        btnYes.setText("YES");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.cancel();

            }
        });
        return exitDialog;
    }




    public static Dialog capturePhotoType(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildTwoButtonDialog(activity, title, message);

        TextView btnNo = exitDialog.findViewById(R.id.btn_no);
        btnNo.setText("CAMERA");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnYes = exitDialog.findViewById(R.id.btn_yes);
        btnYes.setText("GALLERY");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.cancel();
                System.exit(0);
            }
        });
        return exitDialog;
    }

    public static Dialog capturePhotoTypeWithRemove(final Activity activity, String title, String message) {

        final Dialog exitDialog = buildThreeButtonDialog(activity, title, message);

        TextView btnCamera = exitDialog.findViewById(R.id.btn_camera);
        btnCamera.setText("CAMERA");
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnGallery = exitDialog.findViewById(R.id.btn_gallery);
        btnGallery.setText("GALLERY");
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.dismiss();
            }
        });

        TextView btnRemovePhoto = exitDialog.findViewById(R.id.btn_removephoto);
        btnRemovePhoto.setText("REMOVE PHOTO");
        btnRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog.cancel();
                System.exit(0);
            }
        });
        return exitDialog;
    }


    public static Dialog cameraOptionsDialog(final Activity activity, String title, String message) {

        final Dialog cameraDialog = buildTwoButtonDialog(activity, title, message);

        TextView btnNo = cameraDialog.findViewById(R.id.btn_no);
        btnNo.setText(activity.getString(R.string.camera));

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent media = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivity(media);
                cameraDialog.dismiss();
            }
        });

        TextView btnYes = cameraDialog.findViewById(R.id.btn_yes);
        btnYes.setText(activity.getString(R.string.album));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(galleryIntent, 111);//RESULT_LOAD_IMG);
            }
        });
        return cameraDialog;
    }

    public static Dialog internetConnection(final Activity activity) {
        final Dialog networkDialog = buildTwoButtonDialog(activity, "No internet connection", "Please connect to the internet, now.");

        TextView btnNo = networkDialog.findViewById(R.id.btn_no);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        return networkDialog;
    }

//    public static Dialog showConnectionTimeOutDialog(final String message, final Activity activity) {
//        final Dialog connectionTimeOutDialog = buildOneButtonDialog(activity, "Connection status", message);
//        TextView btnOk = connectionTimeOutDialog.findViewById(R.id.btn_ok);
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //SmarterSMBApplication.showConnectionTimeOutStatusCalled = false;
//                connectionTimeOutDialog.dismiss();
//            }
//        });
//        return connectionTimeOutDialog;
//    }

    public static Dialog passwordChanged(final Activity activity) {
        final Dialog networkDialog = buildTwoButtonDialog(activity, "Password", "You have reset your password, would you like to change the password?");

        TextView btnNo = networkDialog.findViewById(R.id.btn_no);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                new CustomChangePasswordDialog().buildChangePasswordDialog(activity.getApplicationContext(), activity).show();
            }
        });
        return networkDialog;
    }

    public static Dialog chatOrHelp(final Activity activity) {
        final Dialog networkDialog = buildTwoButtonDialog(activity, "Help", "How would you like to get help?");

        TextView btnNo = networkDialog.findViewById(R.id.btn_no);
        btnNo.setText("Chat");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                //activity.startActivity(new Intent(activity, ZopimChatActivity.class));
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_yes);
        btnYes.setText("Tutorial");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                ApplicationSettings.putPref(AppConstants.NEW_USER, true);
                activity.finish();
                activity.startActivity(new Intent(activity, UearnHome.class));
            }
        });
        return networkDialog;
    }

    public static Dialog newTeam(final Activity activity) {
        final Dialog teamDialog = buildTwoButtonDialog(activity, "Get your team smarter", "Add your team onto Smarter BIZ now");

        TextView dismiss = teamDialog.findViewById(R.id.btn_no);
        dismiss.setText("Add Team");

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamDialog.dismiss();
            }
        });

        TextView createTeam = teamDialog.findViewById(R.id.btn_yes);
        createTeam.setText("Later");

        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamDialog.dismiss();
                activity.finish();

                if (ApplicationSettings.getPref(AppConstants.NOT_LOGGED_EVENT_TYPE, 0) != 0) {
                    activity.startActivity(new Intent(activity, UearnHome.class));
                }
            }
        });

        return teamDialog;
    }

    public static Dialog reviewSubmit(final Activity activity) {
        final Dialog networkDialog = buildTwoButtonDialog(activity, "Rating", "Do you like UEARN app?");

        TextView btnNo = networkDialog.findViewById(R.id.btn_yes);
        btnNo.setText("NOT REALLY");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                //activity.startActivity(new Intent(activity, FeedbackActivity.class));
                //CustomFeedbackDialog.feedbackDialog(activity);
            }
        });

        TextView btnYes = networkDialog.findViewById(R.id.btn_no);
        btnYes.setText("YES");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkDialog.dismiss();
                Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    activity.startActivity(goToMarket);

                } catch (ActivityNotFoundException e) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
            }
        });
        return networkDialog;
    }

    public static Dialog resetPassword(final Activity activity) {
        final Dialog editDialog = buildTwoButtonDialog(activity, "Reset password", "A reset password link will be sent to your registered email.");

        TextView btnNo = editDialog.findViewById(R.id.btn_no);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDialog.dismiss();
            }
        });
        return editDialog;
    }

    public static Dialog afterCallBack(final Activity activity) {
        final Dialog afterCallDialog = buildTwoButtonDialog(activity, "Empty number", "Would you like to upload anyway?");

        TextView btnNo = afterCallDialog.findViewById(R.id.btn_no);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterCallDialog.dismiss();
            }
        });

        return afterCallDialog;
    }

    public static Dialog deleteTeamMember(final Activity activity) {

        final Dialog deleteMember = buildTwoButtonDialog(activity,
                "Delete Member",
                "Are you sure you want to delete this team member?");

        TextView btnYes = deleteMember.findViewById(R.id.btn_no);
        btnYes.setText("No");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMember.cancel();
            }
        });

        return deleteMember;
    }

    public static Dialog updateApplication(final Activity activity) {

        final Dialog updateDialog = buildTwoButtonDialog(activity,
                "Update Application",
                "You are not on the latest version. Would you like to update?");

        TextView btnNo = updateDialog.findViewById(R.id.btn_yes);
        btnNo.setText("NO");
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.cancel();
            }
        });

        TextView btnYes = updateDialog.findViewById(R.id.btn_no);
        btnYes.setText("YES");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDialog.dismiss();
                Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    activity.startActivity(goToMarket);

                } catch (ActivityNotFoundException e) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
            }
        });

        return updateDialog;
    }

    public static void displyCallRecordings(final Activity activity) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        //builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Hardik");
        arrayAdapter.add("Archit");
        arrayAdapter.add("Jignesh");
        arrayAdapter.add("Umang");
        arrayAdapter.add("Gatti");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(activity);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();

    }

    public static void signOut(final Activity activity) {
        activity.finish();
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        WebStorage webstorage = WebStorage.getInstance();
        webstorage.deleteAllData();
        SmarterSMBApplication.appSettings.setLogout(true);

        ApplicationSettings.putPref(AppConstants.SUPERVISOR_EMAIL, "");
        ApplicationSettings.putPref(AppConstants.CLOUD_TELEPHONY, "");
        ApplicationSettings.putPref(AppConstants.SETTING_CALL_RECORDING_STATE, false);
        ApplicationSettings.putPref(AppConstants.DISABLE_CALL_REC_BUTTONS, false);
        ApplicationSettings.putPref(AppConstants.APP_UPDATE, false);

        Intent intent3 = new Intent(activity, UearnHome.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("EXIT", true);
        activity.startActivity(intent3);
    }

    public static void deleteExistingAlarm(Activity activity) {
        Intent intent = new Intent(activity, Alarm_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm1 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        alarm1.cancel(pendingIntent);
    }

    public static void deleteAllLocalDB(Activity activity) {
        MySql dbHelper = MySql.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("remindertbNew", null, null);
        db.delete("WorkOrder", null, null);
        db.delete("TeamMembers", null, null);
        //Log.d("DataLoading", "db removed");
        db.delete("Ameyo_Push_Notifications", null, null);
        db.delete("ameyo_entries", null, null);
        db.delete("FirstCall", null, null);
        db.delete("ameyo_entries", null, null);
        db.close();
    }

    public static Dialog buildThreeButtonDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.dialog_common_three_button);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = twoButtonDialog.findViewById(R.id.tvDialogTitle);
        tvTitle.setText(title);

        TextView tvMessage = twoButtonDialog.findViewById(R.id.tvDialogMessage);
        tvMessage.setText(message);

        return twoButtonDialog;
    }



    public static Dialog buildChatAttachmentDialog(Activity activity, String title, String message) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.alert_chat_attachment);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);



        Button cancelButton = twoButtonDialog.findViewById(R.id.cancleBtn);

        return twoButtonDialog;
    }
}
