package smarter.uearn.money.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import smarter.uearn.money.R;

/**
 * Created by kavya on 7/29/2015.
 */
public class CustomItem extends LinearLayout {

    private ImageView imageview;
    private TextView textview;
    private EditText edittext;
    private String titleName;
    private Drawable src;

    public CustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrbs(context, attrs);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.custom_itemlayout, this);
        imageview= findViewById(R.id.myimage);
        imageview.setImageDrawable(src);
        textview= findViewById(R.id.mytext);
        textview.setText(titleName);
        edittext= findViewById(R.id.myEdit);
        edittext.setHint(titleName);
    }

    private void getAttrbs(Context context, AttributeSet attributeSet)
    {
        TypedArray a=context.obtainStyledAttributes(attributeSet, R.styleable.Options, 0, 0);
        titleName=a.getString(R.styleable.Options_titleText);
        src=a.getDrawable(R.styleable.Options_imageScr);
        a.recycle();

    }

    public String getText()
    {
        return edittext.getText().toString();
    }

    public EditText getEdittext()
    {
        return edittext;
    }

}
