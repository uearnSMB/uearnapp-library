package smarter.uearn.money.views.events;

import android.widget.AdapterView;

import smarter.uearn.money.models.GroupsUserInfo;

/**
 * Created by Kavya on 22-09-2016.
 */
public interface OnItemSelectedListener {
    void onItemSelected(GroupsUserInfo mail);
    void getFilteredCount(int size);

    void onNothingSelected(AdapterView<?> parent);
}
