package smarter.uearn.money.utils;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import smarter.uearn.money.R;

import java.util.regex.Pattern;

/**
 * Created by Dilip on 1/11/2017.
 */

public final class Validations {

    private final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final static String PHONE_REGEX = "^[+]?[0-9]+[-]?[0-9]+[-]?[0-9]+[[-]?[0-9]+]*$";
    private final static String ALPHANUMERIC_REGEX = "[A-Za-z0-9]+";
    private final static String WEBSITE_VALIDATION = "^(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";

    /*
     * Method to validate text input layout
     * Set the mode as follows
     * 1. Email 2. Password 3. Phone 4. Alphanumeric
     *
     * @Params TextInputLayout
     * @Return boolean
     *
     */
    public final static boolean validateTextLayout(TextInputLayout textInputLayout, int mode) {


        switch (mode) {
            case 1:
                if (!isValidEmail(textInputLayout.getEditText().getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_email));
                   //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 2:
                if (!isValidPassword(textInputLayout.getEditText().getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_password));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 3:
                if (!isValidNumber(textInputLayout.getEditText().getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_phone));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 4:
                if (!isValidNumber(textInputLayout.getEditText().getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_alphanumeric));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
        }


        return true;
    }




    public final static boolean validateTextLayout(EditText textInputLayout, int mode) {


        switch (mode) {
            case 1:
                if (!isValidEmail(textInputLayout.getText().toString())) {
                    //textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_email));
                    //textInputLayout.getText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 2:
                if (!isValidPassword(textInputLayout.getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_password));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 3:
                if (!isValidNumber(textInputLayout.getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_phone));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
            case 4:
                if (!isValidNumber(textInputLayout.getText().toString())) {
                    textInputLayout.setError(textInputLayout.getContext().getString(R.string.invalid_alphanumeric));
                    //textInputLayout.getEditText().setBackgroundResource(R.drawable.error_drawable);
                    return false;
                }
                break;
        }


        return true;
    }

    /*
     * Method to validate email address
     * using regex
     *
     * @Param String
     * @Return boolean
     *
     */
    public final static boolean isValidEmail(String email) {

        if (!email.isEmpty()) {

            if (Character.isLetter(email.charAt(0)) || Character.isDigit(email.charAt(0))) {
                return Pattern.matches(EMAIL_REGEX, email);
            }
        }
        return false;
    }

    public final static boolean isValidWebsite(String wurl) {

        if (!wurl.isEmpty()) {

            if(wurl.contains("www.")) {
                return Pattern.matches(WEBSITE_VALIDATION, wurl);
            }
        }
        return false;
    }

    /*
     * Method to validate phone number
     * using regex
     *
     * @Params String
     * @Return boolean
     *
     */
    public final static boolean isValidNumber(String number) {

        if (digitsCount(number) >= 8) {
            if (Pattern.matches(PHONE_REGEX, number)) {
                return Character.isDigit(number.charAt(number.length() - 1));
            }
        }

        return false;
    }

    /*
     * Method to validate password
     * check if string length is more than 8
     * Min password length size 8
     *
     * @Params String
     * @Return boolean
     *
     */
    public final static boolean isValidPassword(String data) {

        return data.length() >= 8;
    }

    /*
     * Method to validate alphanumeric
     * using regex
     *
     * @Params String
     * @Return boolean
     *
     */
    public final static boolean isAlphanumeric(String data) {

        return data.matches(ALPHANUMERIC_REGEX);
    }

    /**
     * Method to validate if the entire string contains only number
     * @param data
     * @return
     */
    public static boolean isDigits(String data){
        int digitsCnt = digitsCount(data);

        return digitsCnt == data.length();

    }

    /**
     * Method to count the number of digits in a string
     *
     * @params String
     * @Return int
     *
     */
    private final static int digitsCount(String data) {
        int count = 0;
        for (int i = 0, len = data.length(); i < len; i++) {
            if (Character.isDigit(data.charAt(i))) {
                count++;
            }
        }

        return count;
    }
}
