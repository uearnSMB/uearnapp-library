package smarter.uearn.money.utils;
        import android.content.Context;
        import android.graphics.drawable.Drawable;
        import android.util.AttributeSet;
        import android.widget.EditText;

public class UearnEditText extends android.support.v7.widget.AppCompatEditText {

    public UearnEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }
}
