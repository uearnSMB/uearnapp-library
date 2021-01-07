package smarter.uearn.money.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import smarter.uearn.money.R;

/**
 * Created by dilipkumar4813 on 27/04/17.
 */

public class SecondaryTextView extends android.support.v7.widget.AppCompatTextView {

    public SecondaryTextView(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face,Typeface.BOLD);
        this.setTextColor(context.getResources().getColor(R.color.smb_darkblue));
        this.setTextSize(context.getResources().getDimension(R.dimen.secondary_text));
    }

    public SecondaryTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face,Typeface.BOLD);
        this.setTextColor(context.getResources().getColor(R.color.smb_darkblue));
        this.setTextSize(context.getResources().getDimension(R.dimen.secondary_text));
    }

    public SecondaryTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        //this.setTypeface(face,Typeface.BOLD);
        this.setTextColor(context.getResources().getColor(R.color.smb_darkblue));
        this.setTextSize(context.getResources().getDimension(R.dimen.secondary_text));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
