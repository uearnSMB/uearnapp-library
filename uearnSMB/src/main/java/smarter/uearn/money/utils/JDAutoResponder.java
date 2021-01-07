package smarter.uearn.money.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import smarter.uearn.money.activities.appointment.AppointmentCreateActivity;
import smarter.uearn.money.dialogs.JDSMSDialog;

public class JDAutoResponder
{
	private Context context;
	public static String incomingTemplate;
	public static String incomingMessageNumber;
	public String Pno;
	private static final String PHONE_REGEX = "^[+]?[0-9]+[-]?[0-9]+[-]?[0-9]+[[-]?[0-9]+]*$";
	String email = "", phone = "", website = "";
	public JDAutoResponder(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public  void ProcessIncomingMSG(String JD_No, String ExtractedNum, String inSmsTemplate, String outMsgTemplate, String jdurl11,String inSmsNum)
	{
		if(validate(inSmsNum,JD_No)) 
		 {
			ExtractedNum= ExtractNumber(ExtractedNum , inSmsTemplate);// extarcted method... for the number..
	    	incomingMessageNumber=ExtractedNum;
	    	incomingTemplate=inSmsTemplate;
			//sendMsg(ExtractedNum,jdurl11,outMsgTemplate);

			 //Added By Srinath.k
			String[] strings = inSmsTemplate.split(",");
			String nameAndNumber = strings[1];
			String[] nameAndNumberArr = nameAndNumber.split(" ");
			Intent intent = new Intent(context, AppointmentCreateActivity.class);
			intent.putExtra("createFeomMessageBox", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("messageContent",nameAndNumberArr);
			context.startActivity(intent);
		 } else {

		  }
			
	}
	
	// this method is meant for the excatration of number
	public  String ExtractNumber(String ExtractedNum ,String inSmsTemplate)
	{
	 /*   Pattern p = Pattern.compile("(\\d+)(.*)");
	    Matcher m = p.matcher(inSmsTemplate); 
	  if (m.find()) 
	  {
		Pattern p1 = Pattern.compile("(.*\\+)(\\d+)(.*)"); // this pattern is meant for extartion of the 
		ExtractedNum = (m.group(1)).toString();
		

	  }  
	  return ExtractedNum;*/
		if(inSmsTemplate.contains("+91"))
		{
			    Pattern p = Pattern.compile("\\d{12}");
			    Matcher m = p.matcher(inSmsTemplate); 
			  if (m.find()) {
				  ExtractedNum = m.group().trim();
			  }
		}
		else {
			    Pattern p = Pattern.compile("(\\D*)(\\d+)(\\D*) (\\d+)");
			    Matcher m = p.matcher(inSmsTemplate); 
			  if (m.find()) {
				  ExtractedNum = m.group(4).trim();
			  }  
		}
		return ExtractedNum;

        /*Pattern p1 = Pattern.compile("(.*\\+)(\\d+)(.*)");
        Matcher m1 = p1.matcher(m.group(2));
		if (m1.find())
		{
			ExtractedNum = (m1.group(2)).toString();
		}
		return ExtractedNum;*/
		/*
		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(inSmsTemplate);
		while (m.find()) {
		    ExtractedNum = m.group();
		    // append n to list
		}
		return ExtractedNum;*/
		/*if(ExtractedNum.contains("+"))
		{
			Log.e("Inside contains Method:::::::::::::::::::::::", "True");
			return ExtractedNum;
		}
		else
		{
			ExtractedNum="+"+ExtractedNum;
			Log.e("A Method:::::::::::::::::::::::", "True");
			return ExtractedNum;
		}*/

		/*Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(inSmsTemplate);
		while (m.find()) {
			ExtractedNum= m.group().toString();
		    // append n to list
		}*/
		// con

	 }

	 public  void sendMsg(String ExtractedNum,String jdurl, String outMsgTemplate) 
     {
  	   try {
  	         if(ExtractedNum!=null && !ExtractedNum.equalsIgnoreCase("")) {
  	          sendSMS(ExtractedNum, outMsgTemplate);
  	         } else {
  	        	 ExtractedNum=ExtractNumber(ExtractedNum, incomingTemplate);
  	         }
  	    } catch (Exception ex) {
  	             ex.printStackTrace();
  	    }
     }
	 
	 // thi smethod is meant for validating whether the JD_num and the num frm which msg is coming are same or not
	 public  boolean validate(String inSmsNum, String JD_No1)
	 {
		 // here JD_num is JD_number and inSmsNum is number frm which sms is coming
         return inSmsNum.equals(JD_No1);
	 }
	@SuppressWarnings("deprecation")
	public  void sendSMS(final String phoneNumber,final String message) {
			TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		    int simState = telMgr.getSimState();
		            switch (simState) {
		                case TelephonyManager.SIM_STATE_ABSENT:
		                	Toast.makeText(context, "SIM_STATE_ABSENT", Toast.LENGTH_SHORT).show();
		                    // do something
		                    break;
		                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
		                    // do something
		                	Toast.makeText(context, "SIM_STATE_NETWORK_LOCKED.", Toast.LENGTH_SHORT).show();
		                    break;
		                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
		                    // do something
		                	Toast.makeText(context, "SIM_STATE_PIN_REQUIRED.", Toast.LENGTH_SHORT).show();
		                    break;
		                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
		                	Toast.makeText(context, "SIM_STATE_PUK_REQUIRED.", Toast.LENGTH_SHORT).show();
		                    break;
		                case TelephonyManager.SIM_STATE_READY:
		                    // do something
		                {
		                	
		                	new Handler().post(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									SmsManager smsManager = SmsManager.getDefault();
									if(phoneNumber.startsWith("91") && phoneNumber.length()>10) {
										Pno="+"+phoneNumber;
									}
									else {
										Pno=phoneNumber;
									}

									if (sendMessageText() != null
											&& sendMessageText().length() > 0) {
										if (Pno.length() > 0 ) {
											if(isValidEmailorPhoneCustomer(Pno))
												{
													//customDialogSMS(context, Pno, messagetoUser);
													if(isValidEmailorPhoneCustomer(Pno)) {
													
														ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, false);
														Intent intent = new Intent(
															context,
															JDSMSDialog.class);
														intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
														intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
														intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
														intent.putExtra("phonenumber", Pno);
														intent.putExtra("message", sendMessageText());
														context.startActivity(intent);
													}
												}
										}
										} else {
											Toast.makeText(context,
													"SMS Not Delivered!",
													Toast.LENGTH_LONG).show();
										}
								}
							});
		                }
		                    break;
		                case TelephonyManager.SIM_STATE_UNKNOWN:
		                    // do something
		                	Toast.makeText(context, "SIM_STATE_UNKNOWN.", Toast.LENGTH_SHORT).show();
		                    break;
		            }
		            
			
		            
			
		}
	/*private boolean isNetworkAvailable() {
		boolean isConnectedWifi = false;
		boolean isConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	}
*/
	public String sendMessageText() {
		String text = null;
		if (ApplicationSettings.getPref(AppConstants.USERINFO_JD_SMS_TEMPLATE,
				"") != null) {
			text = ApplicationSettings.getPref(
					AppConstants.USERINFO_JD_SMS_TEMPLATE, "");
			
			if(ApplicationSettings.getPref(
					AppConstants.USERINFO_JD_URL, "")!=null)
				text=text+" "+ApplicationSettings.getPref(
						AppConstants.USERINFO_JD_URL, "");
		}
		return text;
	}
	
	
	/*private void sendSMSDataToServer(String number, String message)
			throws Exception {
		try {
			if (number != null && UrlContent.isNetworkAvailable()) {
				JSONObject json = new JSONObject();
				if(ApplicationSettings.getPref(AppConstants.FROM_JD_SMS, true))
				{
					json.put(AppConstants.EVENT_TYPE, "jd_sms");
					json.put("subject", "JD SMS");
				}
				json.put("message", message);
				//json.put("from", number);
				json.put("to", number);
				if(ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "")!=null &&!ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, "").equalsIgnoreCase(""))
				//json.put("to", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
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
					parentChainId=String.valueOf(response.getInt("id"));
					//Log.e("Value:::::::::::::::::::::::::::::", ""+ parentID);
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
						//json1.put("to", ApplicationSettings.getPref(AppConstants.USERINFO_PHONE, ""));
					//json1.put("from", number);
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
					
					if(ApplicationSettings.getPref(AppConstants.FROM_JD_SMS, true))
					{*//*
					try {
						if (responseChild != null) {

							String id = responseChild.getString("id");
							String password = responseChild.getString("password");
							Log.e("Id Value is ::::::::::::::::::::::::::::::::::::",
									id);

								if (sendMessageText() != null
										&& sendMessageText().length() > 0)
								{
									if (Pno
											.length() > 0 ) {
										String messagetoUser = sendMessageText()
												+ "URL : "
												+ "http://smarter-biz.com/cmail_mails/"
												+ id
												+ "\n"
												+ "Password : "
												+ password;
										SmsManager smsManager = SmsManager
												.getDefault();
											smsManager.sendTextMessage(Pno, null,
													messagetoUser, null, null);

										Toast.makeText(context,
												"SMS Sent!", Toast.LENGTH_LONG)
												.show();

										if(isValidEmailorPhoneCustomer(Pno))
											{

												ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, false);
												Intent intent = new Intent(
														context,
														LiveMeetingSMSDialog.class);
												intent.setFlags(Intent.FLAG_FROM_BACKGROUND);
												intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
												intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												intent.putExtra("phonenumber", Pno);
												intent.putExtra("message", messagetoUser);
												intent.putExtra("parentid", parentChainId);
												context.startActivity(intent);
											}
										}
									} else {
										Toast.makeText(context,
												"SMS Not Delivered!",
												Toast.LENGTH_LONG).show();
									}

								}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					*//*}
					
					}
				}
			} else
				Toast.makeText(context, "Check your network setting.",
						Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/
	public boolean isValidEmailorPhoneCustomer (CharSequence target) {

		if (target == null || TextUtils.isEmpty(target)) {
			return false;
		}
        return Pattern.matches(PHONE_REGEX, target);
	}


	 /*public  void customDialogSMS(final Context context,final String number,final String Message)
		{
			// custom dialog
			final SmsManager smsManager = SmsManager.getDefault();
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.customdialog_sms);
						dialog.setTitle("Do you want to send SMS ?");

						// set the custom dialog components - text, image and button
						TextView text = (TextView) dialog.findViewById(R.id.text);
						TextView message = (TextView) dialog.findViewById(R.id.message);
						String contactName=getContactName(context,number);
						if(contactName!=null && !contactName.equalsIgnoreCase(""))
							text.setText("To : "+contactName);
						else
						text.setText("To : "+number);
						message.setText("Message : \n\n"+Message);
						Button dialogokButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
						Button dialogcancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);

						// if button is clicked, close the custom dialog
						dialogokButton.setOnClickListener(new OnClickListener() {
							@SuppressWarnings("deprecation")
							@Override
							public void onClick(View v) {
								//dialog.dismiss();
								dialog.dismiss();
								if(number!=null && !number.equalsIgnoreCase(""))
								{
									if(Message.length()>160)
									{
										ArrayList<String> parts = smsManager.divideMessage(Message);
										smsManager.sendMultipartTextMessage(number, null,
												parts, null, null);


										if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
												&& !ApplicationSettings.getPref(
														AppConstants.USERINFO_ID, "0").equals("0")) {
											if (isNetworkAvailable()) {
												try {
													ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, true);
													new uploadCallToServer().execute(Pno, Message);
													//sendSMSDataToServer(Pno, Message);
												} catch (Exception e) {
													// TODO Auto-generated catch block
												}
											} else {
												Toast.makeText(context,
														"Network Not Available!!!", Toast.LENGTH_SHORT)
														.show();
											}
									}

										context.startActivity(new Intent(context,
												HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

									}
									else
									{
										smsManager.sendTextMessage(number, null,
												Message, null, null);



										if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
												&& !ApplicationSettings.getPref(
														AppConstants.USERINFO_ID, "0").equals("0")) {
											if (isNetworkAvailable()) {
												try {
													ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, true);
													new uploadCallToServer().execute(Pno, Message);
													//sendSMSDataToServer(Pno, Message);
												} catch (Exception e) {
													// TODO Auto-generated catch block
												}
											} else {
												Toast.makeText(context,
														"Network Not Available!!!", Toast.LENGTH_SHORT)
														.show();
											}
									}
										context.startActivity(new Intent(context,
												HomeScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


									}
								}
								Toast.makeText(context,
										"SMS Sent!", Toast.LENGTH_LONG)
										.show();

							}
						});

						dialogcancelButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();


								if (ApplicationSettings.getPref(AppConstants.USERINFO_ID, "0") != null
										&& !ApplicationSettings.getPref(
												AppConstants.USERINFO_ID, "0").equals("0")) {
									if (isNetworkAvailable()) {
										try {
											ApplicationSettings.putPref(AppConstants.FROM_JD_SMS, true);
											new uploadCallToServer().execute(Pno, Message);
											//sendSMSDataToServer(Pno, Message);
										} catch (Exception e) {
											// TODO Auto-generated catch block
										}
									} else {
										Toast.makeText(context,
												"Network Not Available!!!", Toast.LENGTH_SHORT)
												.show();
									}
							}

							}
						});

						dialog.show();
		}

	 class uploadCallToServer extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... params) {
			
			String phone=params[0];
			String msg=params[1];
			
			try {
				sendSMSDataToServer(phone, msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		 
	 }*/
	 
}
