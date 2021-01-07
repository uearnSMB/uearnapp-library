package smarter.uearn.money.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by kavya on 3/17/2016.
 */
public class CustomRadioButton extends RadioButton {
    public CustomRadioButton(Context context, AttributeSet attributeSet) {
        super(context);
    }

    @Override
    public void toggle() {
        if(isChecked()) {
            if(getParent() instanceof RadioGroup) {
                ((RadioGroup)getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }
}
