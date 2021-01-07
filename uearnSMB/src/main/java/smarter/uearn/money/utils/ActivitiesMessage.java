package smarter.uearn.money.utils;

/**
 * Created by Dilip on 1/16/2017.
 */

public class ActivitiesMessage {

    String imageLeft, message, itemId;

    public ActivitiesMessage(String imageLeft, String message, String itemId) {
        this.imageLeft = imageLeft;
        this.message = message;
        this.itemId = itemId;
    }

    public String getImageLeft() {
        return imageLeft;
    }

    public String getMessage() {
        return message;
    }

    public String getItemId() {
        return itemId;
    }
}
