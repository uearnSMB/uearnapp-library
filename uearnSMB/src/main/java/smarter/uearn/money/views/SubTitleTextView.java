package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import smarter.uearn.money.R;


/**
 * Created on 1/12/2017.
 *
 * @author dilip
 */

public class SubTitleTextView extends android.support.v7.widget.AppCompatTextView {

    public SubTitleTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_sub_heading));
    }

    public SubTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_sub_heading));
    }

    public SubTitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_sub_heading));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}