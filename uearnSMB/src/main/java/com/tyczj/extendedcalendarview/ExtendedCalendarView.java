package com.tyczj.extendedcalendarview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import smarter.uearn.money.activities.DashboardAgent;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnFollowupActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static smarter.uearn.money.R.id.prev_cal;

public class ExtendedCalendarView extends RelativeLayout implements OnItemClickListener,
		OnClickListener{

	private Context context;
	private OnDayClickListener dayListener;
	private GridView calendar;
	private CalendarAdapter mAdapter;
	private Calendar cal;
	private TextView month;
	private RelativeLayout base;
	private ImageView next,prev;
	private int gestureType = 0;
	private final GestureDetector calendarGesture = new GestureDetector(context,new GestureListener());
	public ProgressDialog waitDialog;

	public static final int NO_GESTURE = 0;
	public static final int LEFT_RIGHT_GESTURE = 1;
	public static final int UP_DOWN_GESTURE = 2;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private int monthOfYear, year;
	private long startdate = 0, enddate = 0;
	public static boolean check = false;
	public static int dashboardCheck = 0;
	public static String followUpType = "";
	public static String currentActivity = "";
	public interface OnDayClickListener{
		void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day);
	}

	public ExtendedCalendarView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init(){

		String str = ApplicationSettings.getPref(AppConstants.SELECTED_DATE_FROM_CALENDAR_VIEW, "");

		if(str != null && !str.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date currentDate = null;
			try {
				currentDate = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			cal = Calendar.getInstance();
			cal.setTime(currentDate);
		} else {
			cal = Calendar.getInstance();
		}

		base = new RelativeLayout(context);
		base.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		base.setMinimumHeight(15);
		base.setBackgroundColor(Color.parseColor("#2E6FFD"));

		base.setId(R.id.base_id_cale);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		prev = new ImageView(context);
		prev.setId(R.id.prev_cal);
		prev.setLayoutParams(params);
		prev.setImageResource(R.drawable.navigation_previous_item);
		prev.setOnClickListener(this);
		base.addView(prev);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		month = new TextView(context);
		month.setId(R.id.month_cal);
		month.setAllCaps(true);
		month.setLayoutParams(params);
		month.setTextAppearance(context, android.R.style.TextAppearance_Holo_Medium);
		month.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+cal.get(Calendar.YEAR));
		month.setTextColor(Color.WHITE);
		month.setTextSize(15);
		base.addView(month);
		monthOfYear=cal.get(Calendar.MONTH);
		year=cal.get(Calendar.YEAR);

		params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		next = new ImageView(context);
		next.setImageResource(R.drawable.navigation_next_item);
		next.setLayoutParams(params);
		next.setId(R.id.next_cal);
		next.setOnClickListener(this);

		base.addView(next);

		addView(base);

		params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.BELOW, base.getId());

		calendar = new GridView(context);
		calendar.setLayoutParams(params);
		calendar.setNumColumns(7);
		calendar.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		calendar.setDrawSelectorOnTop(true);
		calendar.setBackgroundColor(Color.parseColor("#FAFAFA"));

		mAdapter = new CalendarAdapter(context,cal);
		calendar.setAdapter(mAdapter);
		calendar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return calendarGesture.onTouchEvent(event);
			}
		});

		addView(calendar);
		dayListener=new DayListener();
	}

	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {

			if(gestureType == LEFT_RIGHT_GESTURE){
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					nextMonth();
					return true; // Right to left
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					previousMonth();
					return true; // Left to right
				}
			}else if(gestureType == UP_DOWN_GESTURE){
				if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					nextMonth();
					return true; // Bottom to top
				} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
					previousMonth();
					return true; // Top to bottom
				}
			}
			return false;
		}
	}

	private class DayListener implements OnDayClickListener{

		public DayListener() {
			setOnDayClickListener(this);
		}

		@Override
		public void onDayClicked(AdapterView<?> adapter, View view,
								 int position, long id, Day day) {
			Calendar c=Calendar.getInstance();
			c.set(year, monthOfYear, day.getDay(), 0, 0, 0);
			long start=c.getTimeInMillis();
			String startTime = CommonUtils.getTimeFormatInISO(c.getTime());

			c.set(year, monthOfYear, day.getDay(), 23, 59, 59);
			long end=c.getTimeInMillis();
			String endTime = CommonUtils.getTimeFormatInISO(c.getTime());
			startdate = start;
			enddate = end;
			//Log.i("Tag", "In Extended Calendar Start time ansd end " + startdate + "," + enddate);
            /*Intent intent=new Intent(context, New_AppointmentList.class);*/
			/*Intent intent=new Intent(context, SmarterMainActivity.class);*/

			Intent intent = null;
			if(currentActivity != null && !currentActivity.isEmpty() && currentActivity.equalsIgnoreCase("DashboardAgent")){
				ApplicationSettings.putPref(AppConstants.CALL_FROM_CALENDAR_VIEW, true);
				intent = new Intent(context, DashboardAgent.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			if (dashboardCheck == 1) {
				dashboardCheck = 0;
				intent.putExtra("dashboard", true);
			} else if (dashboardCheck == 2) {
				dashboardCheck = 0;
				intent.putExtra("firstcall", true);
			} else if (dashboardCheck == 3) {
				intent = new Intent(context, UearnFollowupActivity.class);
				//FirstCall  FollowUpToDo
				intent.putExtra("FollowUpType", followUpType);
				intent.putExtra("followup", dashboardCheck);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			} else {
				if (!check) {
					intent.putExtra("appointment", true);
				} else {
					intent.putExtra("tasks", true);
				}
			}
			intent.putExtra("start", start);
			intent.putExtra("end", end);
			intent.putExtra("start_time", startTime);
			intent.putExtra("end_time", endTime);
			context.startActivity(intent);
			((Activity)context).finish();
			//Toast.makeText(getContext(), "clicked day"+c.getTime(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(dayListener != null){
			Day d = (Day) mAdapter.getItem(arg2);
			if(d.getDay() != 0){
				dayListener.onDayClicked(arg0, arg1, arg2, arg3,d);
			}
		}
	}

	/**
	 *
	 * @param listener
	 *
	 * Set a listener for when you press on a day in the month
	 */
	public void setOnDayClickListener(OnDayClickListener listener){
		if(calendar != null){
			dayListener = listener;
			calendar.setOnItemClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.prev_cal) {
			previousMonth();
		} else if (id == R.id.next_cal) {
			nextMonth();
		}
	}

	private void previousMonth(){
		enableWait();
		if(cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set((cal.get(Calendar.YEAR)-1),cal.getActualMaximum(Calendar.MONTH),1);
		} else {
			cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)-1);
		}
		monthOfYear=cal.get(Calendar.MONTH);
		year=cal.get(Calendar.YEAR);
		rebuildCalendar();

	}

	private void nextMonth(){
		enableWait();
		if(cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
			cal.set((cal.get(Calendar.YEAR)+1),cal.getActualMinimum(Calendar.MONTH),1);
		} else {
			cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
		}
		monthOfYear=cal.get(Calendar.MONTH);
		year=cal.get(Calendar.YEAR);
		rebuildCalendar();
	}

	public void enableWait(){
		if(waitDialog==null){
			waitDialog= new ProgressDialog(getContext());
			waitDialog.setMessage("Loading..");
		}
		waitDialog.show();
	}

	public void rebuildCalendar(){
		if(month != null){
			month.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+cal.get(Calendar.YEAR));
			refreshCalendar();
		}
	}

	/**
	 * Refreshes the month
	 */
	public void refreshCalendar(){
		mAdapter.refreshDays(waitDialog);
		mAdapter.notifyDataSetChanged();
	}

	public long getStartdate() {
		return startdate;
	}
	public long getEnddate(){
		return enddate;
	}

	/**
	 *
	 * @param color
	 *
	 * Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundColor(int color){
		base.setBackgroundColor(color);
	}

	@SuppressLint("NewApi")
	/**
	 *
	 * @param drawable
	 *
	 * Sets the background color of the month bar. Requires at least API level 16
	 */
	public void setMonthTextBackgroundDrawable(Drawable drawable){
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
			base.setBackground(drawable);
		}

	}

	/**
	 *
	 * @param resource
	 *
	 * Sets the background color of the month bar
	 */
	public void setMonehtTextBackgroundResource(int resource){
		base.setBackgroundResource(resource);
	}

	/**
	 *
	 * @param recource
	 *
	 * change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageResource(int recource){
		prev.setImageResource(recource);
	}

	/**
	 *
	 * @param bitmap
	 *
	 * change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageBitmap(Bitmap bitmap){
		prev.setImageBitmap(bitmap);
	}

	/**
	 *
	 * @param drawable
	 *
	 * change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageDrawable(Drawable drawable){
		prev.setImageDrawable(drawable);
	}

	/**
	 *
	 * @param recource
	 *
	 * change the image of the next month button
	 */
	public void setNextMonthButtonImageResource(int recource){
		next.setImageResource(recource);
	}

	/**
	 *
	 * @param bitmap
	 *
	 * change the image of the next month button
	 */
	public void setNextMonthButtonImageBitmap(Bitmap bitmap){
		next.setImageBitmap(bitmap);
	}

	/**
	 *
	 * @param drawable
	 *
	 * change the image of the next month button
	 */
	public void setNextMonthButtonImageDrawable(Drawable drawable){
		next.setImageDrawable(drawable);
	}

	/**
	 *
	 * @param gestureType
	 *
	 * Allow swiping the calendar left/right or up/down to change the month. 
	 *
	 * Default value no gesture
	 */
	public void setGesture(int gestureType){
		this.gestureType = gestureType;
	}

}
