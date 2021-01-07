package smarter.uearn.money.views.events;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.MySql;

public class BlockCallStateListener extends PhoneStateListener {

    private Context context;

    public BlockCallStateListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                break;
            case PhoneStateListener.LISTEN_CALL_STATE:
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }
}
