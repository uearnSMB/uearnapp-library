package smarter.uearn.money.dialogs;

import java.sql.Date;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.UrlContent;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.R;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.upload.DataUploadUtils;

// For justdial testing our institute is registered with this url http://www.justdial.com/Bangalore/SM-AC-%3Cnear%3E-Near-Eco-Space-Or-Passport-Office-Bellandur/080PXX80-XX80-140120151944-Y4L4_QmFuZ2Fsb3JlIENvbXB1dGVyIFRyYWluaW5nIEluc3RpdHV0ZXMgRm9yIEFuZHJvaWQ=_BZDET
public class JDSMSDialog extends Activity implements OnClickListener {

	private String phoneNumber;
	private String message;
	SmsManager smsManager;
	public  ProgressDialog waitDialog;
	Button dialogokButton, dialogcancelButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livemeetingsmsdialog);
		if(getIntent().hasExtra("phonenumber")){
			if(getIntent().getExtras().getString("phonenumber")!=null && !getIntent().getExtras().getString("phonenumber").equalsIgnoreCase(""))
			phoneNumber=getIntent().getExtras().getString("phonenumber");
		}
		if(getIntent().hasExtra("message")){
			if(getIntent().getExtras().getString("message")!=null && !getIntent().getExtras().getString("message").equalsIgnoreCase(""))
				message=getIntent().getExtras().getString("message");
			}
		smsManager = SmsManager.getDefault();
		this.setTitle("Do you want to send SMS ?");
		TextView text = findViewById(R.id.text);
		TextView messagetext = findViewById(R.id.message);
		text.setText("To : " + phoneNumber);
		messagetext.setText("Message : \n\n" + message);
		dialogokButton = findViewById(R.id.dialogButtonOK);
		dialogokButton.setOnClickListener(this);
		dialogcancelButton = findViewById(R.id.dialogButtonCancel);
		dialogcancelButton.setOnClickListener(this);
		}
	
	
	/*private boolean isNetworkAvailable() {
		boolean isConnectedWifi = false;
		boolean isConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equals(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase(ni.getTypeName()))
				if (ni.isConnected())
					isConnectedMobile = true;
		}
		return isConnectedWifi || isConnectedMobile;
	}*/

	
	public void sendMessage(String number,String message)
	{
		if(number!=null)
		{
		if(message.length()>160)
		{
			/*ArrayList<String> parts = (ArrayList<String>) CommonUtils.splitInChunks(message, 150);
			   for (String str : parts) {
			        smsManager.sendTextMessage(number, null, str, null, null);
			  }*/

			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> parts =sms.divideMessage(message);
					
			sms.sendMultipartTextMessage(number, null, parts, null, null);
			Toast.makeText(
					JDSMSDialog.this,
					"SMS Sent!",
					Toast.LENGTH_LONG).show();
			if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
					&& !ApplicationSettings.getPref(
							AppConstants.USERINFO_ID, "0").equals("0")) {
				if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
					try {
						ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, true);
						sendSMSDataToServer(number, message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				} else {
					Toast.makeText(this,
							"Data Network Not Available!!!", Toast.LENGTH_SHORT)
							.show();
				}
		}
		}
		else
		{
			smsManager.sendTextMessage(number, null,
					message, null, null);
			Toast.makeText(
					JDSMSDialog.this,
					"SMS Sent!",
					Toast.LENGTH_LONG).show();
			if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
					&& !ApplicationSettings.getPref(
							AppConstants.USERINFO_ID, "0").equals("0")) {
				if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
					try {
						ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, true);
						sendSMSDataToServer(number, message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
					}
				} else {
					Toast.makeText(this, "Network Not Available!!!", Toast.LENGTH_SHORT).show();
				}
		}
		}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dialogButtonOK:
			sendMessage(phoneNumber, message);
			break;

		case R.id.dialogButtonCancel:
			finish();
			break;

		default:
			break;
		}
	}
	
	private void sendSMSDataToServer(final String number,final String message) {
		
			if (number != null && UrlContent.isNetworkAvailable()) {
				
				new AsyncTask<Void, Void, Void>() {
					protected void onPreExecute() 
					{
						waitDialog = new ProgressDialog(JDSMSDialog.this);
						waitDialog.setMessage("Posting Data..");
						waitDialog.setCancelable(false);
						waitDialog.setOwnerActivity(JDSMSDialog.this);
						if (waitDialog != null)
							waitDialog.show();
					}


                    @Override
					protected Void doInBackground(Void... params) {
						// TODO Auto-generated method stub
						try {
				JSONObject json = new JSONObject();
				if(ApplicationSettings.getPref(AppConstants.FROM_JD_SMS, true))
				{
					json.put(AppConstants.EVENT_TYPE, "jd_sms");
					json.put("subject", "JD SMS");
				}
				json.put("message", message);
				json.put("to", number);
				if(ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")!=null &&!ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
				json.put("from", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
				json.put("unread", false);
				
				json.put("email", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));

				if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
						&& !ApplicationSettings.getPref(
								AppConstants.USERINFO_ID, "0").equals("0"))
					json.put("user_id", ApplicationSettings.getPref(
							AppConstants.USERINFO_ID, "0"));

				json.put("start_time",
						new Date(System.currentTimeMillis()).toGMTString());
				JSONObject response = DataUploadUtils.postEmailContactInfo(
						Urls.getCreateCmailUrl(), json);
				if (response != null) {
					int parentID = response.getInt("id");
					//Log.e("Value:::::::", ""+ parentID);
					if (parentID > 0) {
					JSONObject json1 = new JSONObject();
					
					if(ApplicationSettings.getPref(AppConstants.FROM_JD_SMS, true))
					{
						json1.put(AppConstants.EVENT_TYPE, "jd_sms");
						json1.put("subject", "JD SMS");
					}
					else
					{
						json1.put(AppConstants.EVENT_TYPE, AppConstants.SMS_EVENT_TYPE);
						json1.put("subject", "Incoming SMS");
					}
					json1.put("message", message);
					if(ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")!=null &&!ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
						json1.put("from", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
					json1.put("to", number);
					json1.put("unread", false);
					
					json1.put("email", ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, ""));
					json1.put("parent", parentID);
					if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
							&& !ApplicationSettings.getPref(
									AppConstants.USERINFO_ID, "0").equals("0"))
						json1.put("user_id", ApplicationSettings.getPref(
								AppConstants.USERINFO_ID, "0"));

					json1.put("start_time",
							new Date(System.currentTimeMillis()).toGMTString());
					JSONObject responseChild = DataUploadUtils.postEmailContactInfo(
							Urls.getCreateCmailUrl(), json1);
					ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, false);
					
					
					
					}
					
				}
						} catch (JSONException e) {
							e.printStackTrace();
						}
				return null;
					}
					protected void onPostExecute(Void result) {
						try{
							if (waitDialog != null && waitDialog.isShowing()) {
								waitDialog.dismiss();
								waitDialog = null;
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						finish();
					}
                }.execute();
			} else
				Toast.makeText(this, "Check your network setting.",
						Toast.LENGTH_SHORT).show();
		
	}
}