package smarter.uearn.money.views.events;

import smarter.uearn.money.models.WorkOrderEntryInfo;

/**
 * Created by Admin on 07-07-2015.
 */
public interface OnWorkOrderClickedListener {
    void onRightSwipe(WorkOrderEntryInfo mail);
    void onClick(WorkOrderEntryInfo mail);
    void onLongClick(WorkOrderEntryInfo mail);
}
