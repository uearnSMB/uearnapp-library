package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 *
 * Created by Dilip on 1/12/2017.
 * Overriding the textview for custom application regular text
 *
 */

public class RegularTextView extends android.support.v7.widget.AppCompatTextView {

    public RegularTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_body));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
