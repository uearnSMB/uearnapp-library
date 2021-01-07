package smarter.uearn.money.views;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
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
import android.widget.Toast;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.Validation;

/*
 * Created by Bala on 7/1/15.
 */
public class ContactPicker extends RelativeLayout implements View.OnClickListener {
    private AutoCompleteTextView textView;
    private Button phoneButton;
    private Button emailButton;
    private Context context;

    private static int PHONE_NUMBER_PICKER = 1;
    private static int EMAIL_PICKER = 2;

    public ContactPicker(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ContactPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ContactPicker(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public AutoCompleteTextView getTextView() { return textView; }

    public boolean validate() {
        return Validation.isValidEmailorPhone(textView);
    }

    public boolean isEmailAddress() { return  Validation.isEmailAddress(textView, true, "Invalid email Address"); }

    private void init() {
        inflate(getContext(), R.layout.contact_picker, this);
       // Log.i("Tag", "In It Function");
        textView = findViewById(R.id.textView1);
        phoneButton = findViewById(R.id.phone_button);
        emailButton = findViewById(R.id.email_button);
        phoneButton.setOnClickListener(this);
        emailButton.setOnClickListener(this);
        initAutoComplete(context, textView);
        CommonUtils.validateLive(textView);

    }

    public static void initAutoComplete(Context context, AutoCompleteTextView textView) {
        /* TODO replace with access to AppSettings Data */
        String contacts = ApplicationSettings.getPref(AppConstants.PREF_CONTACT_STRING, "");
        CommonUtils.process_contacts(context, textView, contacts);
        /*String[] contactArray = contacts.toString().split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, contactArray);
        textView.setThreshold(1);
        textView.setAdapter(adapter);*/
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
            if (view == phoneButton) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                fragmentClass.startActivityForResult(intent, PHONE_NUMBER_PICKER);
            } else if (view == emailButton) {
                Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                fragmentClass.startActivityForResult(intent, EMAIL_PICKER);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Email pick is not supported by phone", Toast.LENGTH_SHORT).show();
            fm.beginTransaction().remove(fragmentClass).commit();
        }

    }

    public static class ContactPickerFragment extends Fragment{
        static Context context;
        static FragmentManager fragmentManager;
        static AutoCompleteTextView textView;
        public ContactPickerFragment () {

        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data == null)
                return;
            Uri contactData = data.getData();
            if(contactData == null)
                return;
            Cursor cursor = context.getContentResolver().query(contactData, null, null, null, null);
            cursor.moveToFirst();
            String result = "";
            try {
                if (requestCode == PHONE_NUMBER_PICKER) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    result = CommonUtils.removePunctuations(result);

                } else if (requestCode == EMAIL_PICKER) {
                    contactPicked(context, data, textView);
                }
            } catch (Exception e) {
            }
           /* textView.setText(result);
            classResult = result;*/
            super.onActivityResult(requestCode, resultCode, data);
            fragmentManager.beginTransaction().remove(this).commit();
            super.onActivityResult(requestCode, resultCode, data);
        }

        public void setFragmentManager(FragmentManager fm) {
            fragmentManager = fm;
        }

        public void setContext(Context context) {
            ContactPickerFragment.context = context;
        }

        public void setTextView(AutoCompleteTextView textView) {
            ContactPickerFragment.textView = textView;
        }

    }

    private static void contactPicked(Context context, Intent data, AutoCompleteTextView textView) {
        ContentResolver cr = context.getContentResolver();
        try {
            Uri uri = data.getData();
            Cursor cur = context.getContentResolver().query(uri, null, null, null, null);
            cur.moveToFirst();
            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            textView.setText(name);        //print data
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                textView.setText(phone);         //print data
                return;
            }
            pCur.close();
            Cursor emailCur = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(
                        emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                textView.setText(email);         //print data
            }
            emailCur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
