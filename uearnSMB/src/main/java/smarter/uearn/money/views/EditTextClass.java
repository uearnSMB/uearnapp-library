package smarter.uearn.money.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;

/**
 * Created by Kavya on 29-09-2016.
 */
public class EditTextClass extends BaseActivity {
    String number, name;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text);
        changeStatusBarColor(this);

        editText = findViewById(R.id.notesEdittext);
        Intent intent =  getIntent();
        if(intent.hasExtra("callerNumber")){
            number = intent.getStringExtra("callerNumber");
        }
        if(intent.hasExtra("callerName")) {
            name = intent.getStringExtra("callerName");
        }
        if (intent.hasExtra("notesString")) {
            editText.setText(intent.getStringExtra("notesString"));
        }
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quickaction_menu, menu);
        restoreActionBar("Smarter SMS");
        if(name != null) {
            restoreActionBar(name);
        }
        else if(number != null) {
            restoreActionBar(number);
        }

        MenuItem actionOne  = menu.findItem(R.id.action_one);
        MenuItem actionTwo =  menu.findItem(R.id.action_two);
        MenuItem attachIcon = menu.findItem(R.id.attach_icon);
        attachIcon.setVisible(false);
        actionOne.setVisible(false);
        actionTwo.setVisible(false);
        MenuItem viewBt  = menu.findItem(R.id.action_example);
        viewBt.setVisible(false);
        actionTwo.setTitle("Send");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("notesText", editText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //Log.i("EdittextFlow","IN on back pressed "+editText.getText().toString());
        Intent intent = new Intent();
        intent.putExtra("notesText", editText.getText().toString());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
