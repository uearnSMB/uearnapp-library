package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

import smarter.uearn.money.R;

/**
 * Created by dilipkumar4813 on 22/02/17.
 */

public class CustomCheckBox extends CheckBox {

    public CustomCheckBox(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }
}
