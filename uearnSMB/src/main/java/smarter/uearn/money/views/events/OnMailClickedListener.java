package smarter.uearn.money.views.events;

import smarter.uearn.money.models.HomeScreenMail;

/**
 * Created by Admin on 07-07-2015.
 */
public interface OnMailClickedListener {
    void onClick(HomeScreenMail mail);
    void onImageButtonClicked(HomeScreenMail mail);
}
