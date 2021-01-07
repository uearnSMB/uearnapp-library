package smarter.uearn.money.views;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;

/*
 * Created by Bala on 7/1/15.
 */
public class EmailPicker extends RelativeLayout implements View.OnClickListener {
    private AutoCompleteTextView textView;
    private Button emailButton;
    private Context context;


    public EmailPicker(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public EmailPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public EmailPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public String getText() {
        return textView.getText().toString();
    }


    private void init() {
        inflate(getContext(), R.layout.email_picker, this);
        //Log.i("Tag","In It Function");
        textView = findViewById(R.id.textView1);
        emailButton = findViewById(R.id.email_button);
        emailButton.setOnClickListener(this);

        initAutoComplete(context, textView);
    }

    public static void initAutoComplete(Context context, AutoCompleteTextView textView) {
        /* TODO replace with access to AppSettings Data */
        String contacts = ApplicationSettings.getPref(AppConstants.PREF_CONTACT_STRING, "");
        CommonUtils.process_contacts(context, textView, contacts);
    }

    @Override
    public void onClick(View view) {

        final FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
        ContactPickerFragment  fragmentClass = new ContactPickerFragment();
        fragmentClass.setFragmentManager(fm);
        //Log.i("ContactPicker", "Context in on click is " + context);
        fragmentClass.setContext(context);
        fragmentClass.setTextView(textView);

        fm.beginTransaction().add(fragmentClass, "FRAGMENT_TAG").commit();
        fm.executePendingTransactions();

        try {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setData(ContactsContract.CommonDataKinds.Email.CONTENT_URI);
            fragmentClass.startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Email pick is not supported by phone", Toast.LENGTH_SHORT).show();
            fm.beginTransaction().remove(fragmentClass).commit();
        }
    }

    public static class ContactPickerFragment extends Fragment{
        static Context context;
       /* static String classResult;*/
        static FragmentManager fragmentManager;
        static TextView textView;
        public ContactPickerFragment () {
            //Log.i("EmailPicker","In on ContactPickerFragment");
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Log.i("EmailPicker","Data in oncomplete is "+data.getData());
            if (data == null)
                return;
            Uri contactData = data.getData();
            if(contactData == null)
                return;
            Cursor cursor = context.getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();

            String result = "";

            result = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS));
            result = CommonUtils.replaceBrackets(result);

            String currentText = textView.getText().toString();
            if (currentText != null && currentText.length() > 0) {
                currentText += "; " + result;
            } else {
                currentText = result;
            }
            textView.setText(currentText);
            super.onActivityResult(requestCode, resultCode, data);
            fragmentManager.beginTransaction().remove(this).commit();
        }

        public void setFragmentManager(FragmentManager fm) {
            fragmentManager = fm;
        }

        public void setContext(Context context) {
            ContactPickerFragment.context = context;
        }

        public void setTextView(TextView textView) {
            ContactPickerFragment.textView = textView;
        }
    }

}
