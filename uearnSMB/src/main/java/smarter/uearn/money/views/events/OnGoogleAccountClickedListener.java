package smarter.uearn.money.views.events;

/**
 * Created by Admin on 07-07-2015.
 */
public interface OnGoogleAccountClickedListener {
    void onButtonClicked(String id, boolean isChecked);
    void onCalendarButtonClicked(String id, boolean isChecked);
    void onLongClick(String id);
}
