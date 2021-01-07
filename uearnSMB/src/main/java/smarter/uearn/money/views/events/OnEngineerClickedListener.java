package smarter.uearn.money.views.events;

import smarter.uearn.money.models.GroupsUserInfo;

/**
 * Created by Admin on 07-07-2015.
 */
public interface OnEngineerClickedListener {
    void onLongClick(GroupsUserInfo groupsUserInfo);
    void onButtonClicked(GroupsUserInfo groupsUserInfo);
    void onDeleteAcceptClicked(GroupsUserInfo groupsUserInfo);
}
