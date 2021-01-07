package smarter.uearn.money.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import smarter.uearn.money.activities.DeviceCheckActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.views.events.CheckBoxInterface;

public class DeviceCheckAdapter extends ArrayAdapter<String> {
    private List<String> objects;
    private Context context;
    private SparseBooleanArray checkedPositions = new SparseBooleanArray();

    public CheckBoxInterface delegate;
    private HashMap<String, Boolean> checked = new HashMap<>();

    public DeviceCheckAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
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
            convertView = layoutInflater.inflate(R.layout.process_selection_list, parent, false);
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
                    DeviceCheckActivity.noOfCheckboxChecked++;
                    delegate.onCheckBoxSelected(position, objects.get(pos), true);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBoxHolder.rowIsDone.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.checkbox_green));
                    }

                    String rowTitle = checkBoxHolder.rowTitle.getText().toString();
                    if (rowTitle != null && !rowTitle.isEmpty()) {
                        DeviceCheckActivity.currentSelectedCheckBoxValue = rowTitle;
                    } else {
                        DeviceCheckActivity.currentSelectedCheckBoxValue = "";
                    }
                    if (rowTitle != null && !rowTitle.isEmpty() && rowTitle.equals("None of the above")) {
                        DeviceCheckActivity.noneOfTheAboveChecked++;
                    }
                } else {
                    DeviceCheckActivity.noOfCheckboxChecked--;
                    delegate.onCheckBoxSelected(position, objects.get(pos), false);
                    checkedPositions.put(pos, false);
                    ischecked(pos, false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBoxHolder.rowIsDone.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.checkbox_gray));
                    }

                    String rowTitle = checkBoxHolder.rowTitle.getText().toString();
                    if (rowTitle != null && !rowTitle.isEmpty() && rowTitle.equals("None of the above")) {
                        DeviceCheckActivity.noneOfTheAboveChecked--;
                    }

                    if (ApplicationSettings.containsPref(AppConstants.WIFI_CHECK_COMPLETED)) {
                        boolean wifiCheckCompleted = ApplicationSettings.getPref(AppConstants.WIFI_CHECK_COMPLETED, false);
                        if (wifiCheckCompleted) {
                            if(rowTitle != null && !rowTitle.isEmpty() && rowTitle.equalsIgnoreCase("Broadband WiFi Connection")) {
                                checkBoxHolder.rowIsDone.setChecked(true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    checkBoxHolder.rowIsDone.setButtonTintList(ContextCompat.getColorStateList(getContext(), R.color.checkbox_green));
                                }
                            } else {
                                checkBoxHolder.rowIsDone.setChecked(false);
                            }
                        } else {
                            checkBoxHolder.rowIsDone.setChecked(false);
                        }
                    }
                }
            }
        });
        String object = objects.get(position);
        checkBoxHolder.rowTitle.setText(object);
        if (checked.get(object) != null) {
            if (ApplicationSettings.containsPref(AppConstants.WIFI_CHECK_COMPLETED)) {
                boolean wifiCheckCompleted = ApplicationSettings.getPref(AppConstants.WIFI_CHECK_COMPLETED, false);
                if (wifiCheckCompleted) {
                    String checkBoxTitle = checkBoxHolder.rowTitle.getText().toString();
                    if(checkBoxTitle != null && !checkBoxTitle.isEmpty() && checkBoxTitle.equalsIgnoreCase("Broadband WiFi Connection")) {
                        checkBoxHolder.rowIsDone.setChecked(true);
                    } else {
                        checkBoxHolder.rowIsDone.setChecked(checked.get(object));
                    }
                } else {
                    checkBoxHolder.rowIsDone.setChecked(checked.get(object));
                }
            } else {
                checkBoxHolder.rowIsDone.setChecked(checked.get(object));
            }
        } else {
            if (ApplicationSettings.containsPref(AppConstants.WIFI_CHECK_COMPLETED)) {
                boolean wifiCheckCompleted = ApplicationSettings.getPref(AppConstants.WIFI_CHECK_COMPLETED, false);
                if (wifiCheckCompleted) {
                    String checkBoxTitle = checkBoxHolder.rowTitle.getText().toString();
                    if(checkBoxTitle != null && !checkBoxTitle.isEmpty() && checkBoxTitle.equalsIgnoreCase("Broadband WiFi Connection")) {
                        checkBoxHolder.rowIsDone.setChecked(true);
                    } else {
                        checkBoxHolder.rowIsDone.setChecked(false);
                    }
                } else {
                    checkBoxHolder.rowIsDone.setChecked(false);
                }
            } else {
                checkBoxHolder.rowIsDone.setChecked(false);
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