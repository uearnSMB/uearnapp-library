package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by Dilip on 1/12/2017.
 * Overriding the basic text layout for title text in the application
 *
 */

public class TitleTextView extends TextView {

    public TitleTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_title));
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_title));
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
        this.setTypeface(face);
        this.setTextSize(context.getResources().getDimension(R.dimen.font_title));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
