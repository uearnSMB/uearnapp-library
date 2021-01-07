package smarter.uearn.money.activities.appointment;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.activities.UearnFollowupActivity;
import smarter.uearn.money.utils.CommonUtils;
import com.tyczj.extendedcalendarview.ExtendedCalendarView;

public class ViewCalendarActivity extends BaseActivity {

	private ExtendedCalendarView calendar;
	private String followUpsType = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(CommonUtils.allowScreenshot()){

		} else {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		}
		setContentView(R.layout.view_calendar);
		Intent intent = getIntent();
		followUpsType = intent.getStringExtra("FollowUpsType");
		changeStatusBarColor(this);
		getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
		if(getIntent().hasExtra("taskView")) {
			ExtendedCalendarView.check = true;
		} else if (getIntent().hasExtra("dashboard")) {
			ExtendedCalendarView.dashboardCheck = 1;
		} else if (getIntent().hasExtra("firstcall")) {
			ExtendedCalendarView.dashboardCheck = 2;
		} else if (getIntent().hasExtra("FollowUpType")) {
			int followUpType = getIntent().getIntExtra("FollowUpType", 0);
			if(followUpType != 0){
				ExtendedCalendarView.dashboardCheck = followUpType;
				ExtendedCalendarView.followUpType = followUpsType;
			}
		}
		else {
			ExtendedCalendarView.check = false;
		}
		calendar = findViewById(R.id.calendar);
		calendar.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.items, menu);
		restoreActionBar("Calendar");
		MenuItem sendButton  = menu.findItem(R.id.action_one);
		sendButton.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		int id = mi.getItemId();
		switch (id) {
			case android.R.id.home:
//				Intent intent = new Intent (this, UearnFollowupActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//				startActivity(intent);
				this.finish();
				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		Intent intent = new Intent (this, UearnFollowupActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//		startActivity(intent);
		this.finish();
	}
}