package smarter.uearn.money.utils.socialmedia;

import android.content.Context;
import android.widget.Button;

public class SocialMediaManager {

	public static int REGISTRATION = 1;
	public static int LOGIN = 2;
    public static int SPLASH_REGISTRATION = 3;
    public static int LOGIN_REGISTRATION = 4;
	public Context context;
	private Button googlePlus, facebook;
	private int action;
//	private ProgressDialog waitDialog;

	public SocialMediaManager(Context context) {
		this.context = context;
	}

//	public static void initFacebooksdk(Context context){
//		FacebookSdk.sdkInitialize(context);
//	}
//
//	public void initButtons(Button googleplusButton, Button facebookButton) {
//        tracker = ((SmarterSMBApplication) context.getApplicationContext()).getTracker(SmarterSMBApplication.TrackerName.APP_TRACKER);
//        googlePlus=googleplusButton;
//		facebook=facebookButton;
//       	googlePlus.setOnClickListener(this);
//		facebook.setOnClickListener(this);
//        initClasses();
//	}
//
//    public void initButtons(Button googleplusButton) {
//        tracker = ((SmarterSMBApplication) context.getApplicationContext()).getTracker(SmarterSMBApplication.TrackerName.APP_TRACKER);
//        googlePlus = googleplusButton;
//        googlePlus.setOnClickListener(this);
//        googleplussignin=new GooglePlusSignIn(context);
//    }
//
//	public void initClasses() {
//		googleplussignin=new GooglePlusSignIn(context);
//		facebookSignIn=new FacebookSignIn(context);
//	}


//	public void showWaitDialog(){
//		if(context != null) {
//			waitDialog = new ProgressDialog(context);
//			waitDialog.setMessage("Connecting Google Server...");
//			waitDialog.setCancelable(false);
//			waitDialog.setOwnerActivity((Activity) context);
//			waitDialog.show();
//		}
//	}
//
//	public void dismissWaitDialog(){
//		if(waitDialog != null && waitDialog.isShowing()){
//			waitDialog.dismiss();
//		}
//	}
	
//	@Override
//	public void onClick(View v) {
//		int id=v.getId();
//
//        if(id == R.id.googlePlus_signup){
//			SocialMediaRegistrationActivity activity = (SocialMediaRegistrationActivity)context;
//			if(activity.phoneValidation()) {
//				action = REGISTRATION;
//				googleplussignin.setAction(REGISTRATION);
//				tracker.send(new HitBuilders.EventBuilder().setCategory("RegistrationScreen")
//						.setAction("Google plus SignUp").setLabel("ButtonClick").build());
//				if (CommonUtils.isNetworkAvailable(context)) {
//			//		showWaitDialog();
//					googleplussignin.callConnect();
//				} else {
//					Toast.makeText(context, "Data network not available!", Toast.LENGTH_SHORT).show();
//				}
//			}else{
//				Toast.makeText(context,"Please Enter Valid phone number", Toast.LENGTH_LONG).show();
//			}
//        }else if(id== R.id.googlePlus) {
//            action = LOGIN;
//            googleplussignin.setAction(LOGIN);
//            //SocialMediaSignIn socialmediasignin=new SocialMediaSignIn(this, SocialMediaSignIn.GOOGLEPLUS);
//			if(CommonUtils.isNetworkAvailable(context)) {
//		//		showWaitDialog();
//                googleplussignin.callConnect();
//			}else {
//				Toast.makeText(context, "Data network not available!", Toast.LENGTH_SHORT).show();
//			}
//		}else if(id==R.id.fb_login_button) {
//				action = LOGIN;
//				facebookSignIn.setAction(LOGIN);
//				tracker.send(new HitBuilders.EventBuilder().setCategory("RegistrationScreen")
//						.setAction("Facebook SignUp").setLabel("ButtonClick").build());
//				if (CommonUtils.isNetworkAvailable(context)) {
//		//			showWaitDialog();
//					facebookSignIn.callLogin();
//				} else {
//					Toast.makeText(context, "Data network not available!", Toast.LENGTH_SHORT).show();
//				}
//		}else if(id == R.id.fb_signup){
//		SocialMediaRegistrationActivity activity = (SocialMediaRegistrationActivity)context;
//		if(activity.phoneValidation()) {
//			action = REGISTRATION;
//			facebookSignIn.setAction(REGISTRATION);
//			if(CommonUtils.isNetworkAvailable(context)) {
//	//			showWaitDialog();
//				facebookSignIn.callLogin();
//			}else{
//				Toast.makeText(context, "Data network not available!", Toast.LENGTH_SHORT).show();
//			}
//		}else{
//			Toast.makeText(context,"Please Enter Valid phone number", Toast.LENGTH_LONG).show();
//		}
//		}
//		/*else if(id == R.id.signUpButton){
//            action = LOGIN_REGISTRATION;
//            googleplussignin.setAction(LOGIN_REGISTRATION);
//            if(CommonUtils.isNetworkAvailable(context)) {
//                googleplussignin.callConnect();
//            }else {
//                Toast.makeText(context, "Data network not available!", Toast.LENGTH_SHORT).show();
//            }
//        }*/
//	}
	
//	public void onActivityResult(int requestCode, int resultCode, Intent data){
//	//	dismissWaitDialog();
//		Log.i("Tag", "Social Media Manager Activity result " + resultCode);
//		switch (requestCode) {
//        case GooglePlusSignIn.GOOGLEPLUS_SIGN_IN:
//        		if(googleplussignin!=null){
//                    if(resultCode == Activity.RESULT_OK){
//        				googleplussignin.onactivityresult(true);
//        			}else{
//        				googleplussignin.onactivityresult(false);
//        			}
//        		}
//        		break;
//        case FacebookSignIn.facebookRequestCode:
//        		if(facebookSignIn!=null){
//        			facebookSignIn.onactivityresult(requestCode, resultCode, data);
//        		}
//
//        	}
//	}
	
//	public void disconnectGoogleClient(){
////		dismissWaitDialog();
//		if(googleplussignin!=null){
//			googleplussignin.callDisconnect();
//		}
//	}
}
