package smarter.uearn.money.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import smarter.uearn.money.R;

/**
 * Created on 21/02/17.
 *
 * @author Dilip
 * @version 1.0
 */

public class FiveLevelProgress extends RelativeLayout {

    private View firstView, secondView, thirdView, fourthView;
    private Context cntxt;
    private RelativeLayout.LayoutParams params1, params2, params3, params4;

    public FiveLevelProgress(Context context) {
        super(context);
        this.cntxt = context;
        this.setBackgroundResource(R.drawable.custom_rounded_corners);
    }

    public FiveLevelProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.cntxt = context;
        this.setBackgroundResource(R.drawable.custom_rounded_corners);
    }

    public FiveLevelProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.cntxt = context;
        this.setBackgroundResource(R.drawable.custom_rounded_corners);
    }

    /**
     * Method to set the progress of the first level
     *
     * @param progress - percentage value for the progress
     */
    public void setFirstProgress(int progress,int pWidth) {
        int viewWidth = Math.round((float) (pWidth * progress / 100));
        //Log.d("five", "check " + viewWidth);

        firstView = new View(cntxt);
        firstView.setBackgroundResource(R.drawable.custom_progress_first);

        params1 = new RelativeLayout.LayoutParams(
                viewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        reinitializeViews();
    }

    /**
     * Method to set the progress of the second level
     *
     * @param progress - percentage value for the progress
     */
    public void setSecondProgress(int progress,int pWidth) {
        int viewWidth = Math.round((float) (pWidth * progress / 100));
        //Log.d("five", "check " + viewWidth);

        secondView = new View(cntxt);
        secondView.setBackgroundResource(R.drawable.custom_progress_second);

        params2 = new RelativeLayout.LayoutParams(
                viewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        reinitializeViews();
    }

    /**
     * Method to set the progress of the third level
     *
     * @param progress - percentage value for the progress
     */
    public void setThirdProgress(int progress,int pWidth) {
        int viewWidth = Math.round((float) (pWidth * progress / 100));
        //Log.d("five", "check " + viewWidth);

        thirdView = new View(cntxt);
        thirdView.setBackgroundResource(R.drawable.custom_progress_third);

        params3 = new RelativeLayout.LayoutParams(
                viewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        reinitializeViews();
    }

    /**
     * Method to set the progress of the fourth level
     *
     * @param progress - percentage value for the progress
     */
    public void setFourthProgress(int progress,int pWidth) {
        int viewWidth = Math.round((float) (pWidth * progress / 100));
        //Log.d("five", "check " + viewWidth);

        fourthView = new View(cntxt);
        fourthView.setBackgroundResource(R.drawable.custom_progress_fourth);

        params4 = new RelativeLayout.LayoutParams(
                viewWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        params4.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        reinitializeViews();
    }

    /**
     * Method to reinitialize the views every time the change has been made
     */
    private void reinitializeViews() {

        this.removeAllViews();

        if (params4 != null) {
            this.addView(fourthView, params4);
        }

        if (params3 != null) {
            this.addView(thirdView, params3);
        }

        if (params2 != null) {
            this.addView(secondView, params2);
        }

        if (params1 != null) {
            this.addView(firstView, params1);
        }
    }
}
