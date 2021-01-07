package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import smarter.uearn.money.R;

/**
 *
 * Created by Dilip on 1/12/2017.
 * Overriding the custom edit text for the application
 *
 */

public class SmartEditText extends EditText {

    public SmartEditText(Context context) {
        super(context);
        this.setBackgroundResource(R.drawable.edittext_drawable);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-light.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }

    public SmartEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundResource(R.drawable.edittext_drawable);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-light.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }

    public SmartEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setBackgroundResource(R.drawable.edittext_drawable);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-light.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }
}
