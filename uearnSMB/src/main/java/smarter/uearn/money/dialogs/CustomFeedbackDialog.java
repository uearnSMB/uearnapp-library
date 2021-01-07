package smarter.uearn.money.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by dilipkumar4813 on 01/03/17.
 */

public class CustomFeedbackDialog {

    public static void feedbackDialog(final Activity activity) {
        final Dialog twoButtonDialog = new Dialog(activity);
        twoButtonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        twoButtonDialog.setContentView(R.layout.dialog_feedback);

        twoButtonDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText etTitle = twoButtonDialog.findViewById(R.id.etDialogMessage);

        TextView btnSubmit = twoButtonDialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String message = "eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + " ID : " +
                        ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + " " + etTitle.getText().toString();

                ServiceApplicationUsage.callErrorLog(message);
                Toast.makeText(activity, "Thank you for your feedback", Toast.LENGTH_SHORT).show();*/
//                activity.startActivity(new Intent(activity, FeedbackActivity.class));
//                twoButtonDialog.dismiss();
            }
        });
        twoButtonDialog.show();
    }
}
