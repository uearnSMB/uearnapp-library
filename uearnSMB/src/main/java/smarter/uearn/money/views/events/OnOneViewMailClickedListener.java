package smarter.uearn.money.views.events;

import smarter.uearn.money.models.OneViewScreenMail;

/**
 * Created by Admin on 07-07-2015.
 */
public interface OnOneViewMailClickedListener {
    void onClick(OneViewScreenMail mail);
    void onLongClick(OneViewScreenMail mail);
    void onImageButtonClicked(OneViewScreenMail mail);
}
