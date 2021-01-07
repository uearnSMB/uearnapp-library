package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import smarter.uearn.money.R;

/**
 * Created by dilipkumar4813 on 27/04/17.
 */

public class PrimaryTextView extends android.support.v7.widget.AppCompatTextView {

    public PrimaryTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.primary_text));
    }

    public PrimaryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.primary_text));
    }

    public PrimaryTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.primary_text));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
