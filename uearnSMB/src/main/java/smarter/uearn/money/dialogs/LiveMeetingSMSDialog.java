package smarter.uearn.money.dialogs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.R;

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

public class LiveMeetingSMSDialog extends Activity implements OnClickListener {

	private String phoneNumber;
	private String message;
	private String parentID;
	SmsManager smsManager;
	public  ProgressDialog waitDialog;
	Button dialogokButton, dialogcancelButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livemeetingsmsdialog);
		if(getIntent().getExtras().getString("phonenumber")!=null && !getIntent().getExtras().getString("phonenumber").equalsIgnoreCase(""))
		phoneNumber=getIntent().getExtras().getString("phonenumber");
		if(getIntent().getExtras().getString("message")!=null && !getIntent().getExtras().getString("message").equalsIgnoreCase(""))
			message=getIntent().getExtras().getString("message");
		
		if(getIntent().getExtras().getString("parentid")!=null && !getIntent().getExtras().getString("parentid").equalsIgnoreCase(""))
			parentID=getIntent().getExtras().getString("parentid");
		
		
		smsManager = SmsManager.getDefault();
		
		
		//setContentView(R.layout.customdialog_sms);
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
		public void sendMessage(String number,String message)
		{
		if(phoneNumber!=null)
		{
			
		
		if(message.length()>160)
		{

			SmsManager sms = SmsManager.getDefault();
			ArrayList<String> parts =sms.divideMessage(message);
			sms.sendMultipartTextMessage(number, null, parts, null, null);
		}
		else
		{
			smsManager.sendTextMessage(number, null,
					message, null, null);
		}
		Toast.makeText(LiveMeetingSMSDialog.this, "SMS Sent!", Toast.LENGTH_LONG).show();
			finish();
		//startActivity(new Intent(LiveMeetingSMSDialog.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
			if(parentID!=null && !parentID.equalsIgnoreCase(""))
			callDelete(parentID);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(parentID!=null && !parentID.equalsIgnoreCase(""))
			callDelete(parentID);
		//super.onBackPressed();
	}
	
	public  void callDelete(final String id)
	{
		
		final class DeleteMail extends AsyncTask<String, Void, String> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				waitDialog = new ProgressDialog(LiveMeetingSMSDialog.this);
				waitDialog.setMessage("Deleting");
				waitDialog.setCancelable(false);
				if (waitDialog != null)
					waitDialog.show();
			}

			protected String doInBackground(String... urls) {

					String url1 = Urls.getCmailUrl(id);//HomeScreen.SERVER_ADDRESS+"/cmail_mails/" + id + ".json";
					
					URL url = null;
					try {
					    url = new URL(url1);
					} catch (MalformedURLException exception) {
					    exception.printStackTrace();
					}
					HttpURLConnection httpURLConnection = null;
					try {
					    httpURLConnection = (HttpURLConnection) url.openConnection();
					    httpURLConnection.setRequestProperty("Content-Type",
					                "application/x-www-form-urlencoded");
					    httpURLConnection.setRequestMethod("DELETE");
					} catch (IOException exception) {
					    exception.printStackTrace();
					} finally {         
					    if (httpURLConnection != null) {
					        httpURLConnection.disconnect();
					    }
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				if (waitDialog != null) {
					waitDialog.dismiss();
					waitDialog = null;
				}
				//startActivity(new Intent(LiveMeetingSMSDialog.this, HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				finish();
			}
		}
		DeleteMail l = new DeleteMail();
		l.execute();
	}
}