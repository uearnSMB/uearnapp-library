package smarter.uearn.money.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

public class Validation {

    // Regular Expression
    // you can change the expression based on your need
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String EMAIL_REGEX_1 = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
    //private static final String PHONE_REGEX = "^[+]?[0-9]{10,13}$";
    //private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // Error Messages
    private static final String REQUIRED_MSG = "required";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "##########";
    private static final String PHONE_REGEX = "^[+]?[0-9]+[-]?[0-9]+[-]?[0-9]+[[-]?[0-9]+]*$";

    public static final String MULTIPLE_EMAIL_REGEX = "^" + EMAIL_REGEX_1 + "([,; ]*" + EMAIL_REGEX_1 + ")*$";

    public static boolean isPhoneNumber(String numberString) {
        return Pattern.matches(PHONE_REGEX, numberString);
    }

    public static boolean isEmailAddress(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isMultipleEmailAddress(String email) {
        return Pattern.matches(MULTIPLE_EMAIL_REGEX, email);
    }


    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required, String msg) {


//        String email = editText.getText().toString();
//        boolean check;
//        Pattern p;
//        Matcher m;
//
//        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//
//        p = Pattern.compile(EMAIL_STRING);
//
//        m = p.matcher(email);
//        check = m.matches();
//
//        if(!check) {
//           // txtEmail.setError("Not Valid Email");
//        }
//        return check;

        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required, msg);
    }



    public static boolean isMultipleEmailAddress(EditText editText, boolean required, String msg) {
        return isValid(editText, MULTIPLE_EMAIL_REGEX, EMAIL_MSG, required, msg);
    }

    public static boolean isPhoneNumber(AutoCompleteTextView editText, boolean required, String msg) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required, msg);
    }

    public static boolean isPhoneNumber(EditText editText, boolean required, String msg) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required, msg);
    }

    public static boolean isEmailAddress(AutoCompleteTextView editText, boolean required, String msg) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required, msg);
    }

    // call this method when you need to check phone number validation
    /*public static boolean isPhoneNumber(EditText editText, boolean required,String msg) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required,msg);
	}*/
	
	/*public static boolean isPhoneNumber(AutoCompleteTextView editText, boolean required,String msg) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required,msg);
	}*/

    // return true if the input field is valid, based on the parameter passed
    public static boolean isValid(EditText editText, String regex,
                                  String errMsg, boolean required, String msg) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText, msg))
            return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            //editText.setError(msg);
            return false;
        }

        return true;
    }


    public static boolean isValid(AutoCompleteTextView editText, String regex,
                                  String errMsg, boolean required, String msg) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if (required && !hasText(editText, msg))
            return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(msg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText, String message) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                //editText.setError(Html.fromHtml("<font color='red'>"+message+"</font>"));
            } else {
                //editText.setError(message);
            }
            return false;
        }

        return true;
    }

    public static boolean haspwdText(EditText editText, String message) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() < 8) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                //editText.setError(Html.fromHtml("<font color='red'>"+message+"</font>"));
            } else {
                //editText.setError(message);
            }
            return false;
        }

        return true;
    }

    public static boolean hasText(AutoCompleteTextView editText, String message) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                editText.setError(Html.fromHtml("<font color='red'>Invalid Email or Phone No.</font>"));
            } else {
                editText.setError(message);
            }
            return false;
        }

        return true;
    }

    public static boolean isValidPhoneNumber(EditText editText, String abrevation) {
        String phone = editText.getText().toString();
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }

    public static boolean isValidEmailorPhone(AutoCompleteTextView textView) {

        String input = textView.getText().toString().replace(" ", "").trim();
        textView.setError(null);

        if (input == null || TextUtils.isEmpty(input)) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                textView.setError(Html.fromHtml("<font color='red'>Invalid Email or Phone No.</font>"));
            } else {
                textView.setError("Invalid Email or Phone No.");
            }
            return false;
        }

        if (isEmailAddress(input) == false && isPhoneNumber(input) == false) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                textView.setError(Html.fromHtml("<font color='red'>Invalid Email or Phone No.</font>"));
            } else {
                textView.setError("Invalid Email or Phone No.");
            }
            return false;
        }

        return true;
    }
}