package smarter.uearn.money.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnActivity;
import smarter.uearn.money.views.events.CheckBoxInterface;

import static smarter.uearn.money.activities.UearnActivity.noneOfTheAboveChecked;

public class CheckboxAdapter extends ArrayAdapter<String> {
    private List<String> objects;
    private Context context;
    private SparseBooleanArray checkedPositions = new SparseBooleanArray();
    private HashMap<String, Boolean> currentSelectedCheckboxHashMap;

    public CheckBoxInterface delegate;
    private HashMap<String, Boolean> checked = new HashMap<>();

    public CheckboxAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects, HashMap<String, Boolean> currentSelectedCheckboxHashMap) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.currentSelectedCheckboxHashMap = currentSelectedCheckboxHashMap;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CheckBoxHolder checkBoxHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.checkbox_list, parent, false);
            convertView.setMinimumHeight(50);
            checkBoxHolder = new CheckBoxHolder();
            checkBoxHolder.rowTitle = (TextView) convertView.findViewById(R.id.tvContent);
            checkBoxHolder.rowIsDone = (CheckBox) convertView.findViewById(R.id.chbContent);
            convertView.setTag(checkBoxHolder);
        } else {
            checkBoxHolder = (CheckBoxHolder) convertView.getTag();
        }
        checkBoxHolder.rowTitle.setTag(position);
        checkBoxHolder.rowIsDone.setTag(convertView);
        checkBoxHolder.rowIsDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                View view = (View) compoundButton.getTag();
                TextView title = (TextView) view.findViewById(R.id.tvContent);
                int pos = (int) title.getTag();
                if (checked) {
                    checkedPositions.put(pos, true);
                    ischecked(pos, true);
                    UearnActivity.noOfCheckboxChecked++;
                    delegate.onCheckBoxSelected(position, objects.get(pos), true);

                    String rowTitle = checkBoxHolder.rowTitle.getText().toString();
                    if (rowTitle != null && !rowTitle.isEmpty()) {
                        UearnActivity.currentSelectedCheckBoxValue = rowTitle;
                    } else {
                        UearnActivity.currentSelectedCheckBoxValue = "";
                    }
                    if (rowTitle != null && !rowTitle.isEmpty() && rowTitle.equals("None of the above")) {
                        noneOfTheAboveChecked++;
                    }
                } else {
                    UearnActivity.noOfCheckboxChecked--;
                    delegate.onCheckBoxSelected(position, objects.get(pos), false);
                    checkedPositions.put(pos, false);
                    ischecked(pos, false);
                    view.setBackgroundColor(Color.WHITE);

                    String rowTitle = checkBoxHolder.rowTitle.getText().toString();
                    if (rowTitle != null && !rowTitle.isEmpty() && rowTitle.equals("None of the above")) {
                        noneOfTheAboveChecked--;
                    }
                }
            }
        });
        String object = objects.get(position);
        checkBoxHolder.rowTitle.setText(object);
        if (checked.get(object) != null) {
            checkBoxHolder.rowIsDone.setChecked(checked.get(object));
        } else {
            checkBoxHolder.rowIsDone.setChecked(false);
        }

        if(currentSelectedCheckboxHashMap != null && !currentSelectedCheckboxHashMap.isEmpty() && currentSelectedCheckboxHashMap.size() > 0){
            boolean value = currentSelectedCheckboxHashMap.get(object);
            if(value == true){
                checkBoxHolder.rowIsDone.setChecked(true);
            }
        }
        return convertView;
    }

    private class CheckBoxHolder {
        private TextView rowTitle;
        private CheckBox rowIsDone;
    }

    public void ischecked(int position, boolean flag) {
        checked.put(this.objects.get(position), flag);
    }
}