package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import smarter.uearn.money.R;

/**
 * Created by Dilip on 1/12/2017.
 * Custom regular edit text for the entire application
 */

public class RegularEditText extends android.support.v7.widget.AppCompatEditText {

    public RegularEditText(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_edit_text));
        this.setPadding(12, 12, 12, 12);
    }
}
