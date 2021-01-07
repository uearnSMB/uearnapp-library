package com.tyczj.extendedcalendarview;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.BaseAdapter;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.MySql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Day{
	
	int startDay;
	int monthEndDay;
	int day;
	int year;
	int month;
	Context context;
	BaseAdapter adapter;
	ArrayList<Event> events = new ArrayList<Event>();
	
	Day(Context context,int day, int year, int month){
		this.day = day;
		this.year = year;
		this.month = month;
		this.context = context;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, day);
		int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(year, month, end);
		TimeZone tz = TimeZone.getDefault();
		monthEndDay = Time.getJulianDay(cal.getTimeInMillis(), TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}
	
//	public long getStartTime(){
//		return startTime;
//	}
//	
//	public long getEndTime(){
//		return endTime;
//	}
	
	public int getMonth(){
		return month;
	}
	
	public int getYear(){
		return year;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	
	public int getDay(){
		return day;
	}
	
	/**
	 * Add an event to the day
	 * 
	 * @param event
	 */
	public void addEvent(Event event){
		events.add(event);
	}
	
	/**
	 * Set the start day
	 * 
	 * @param startDay
	 */
	public void setStartDay(int startDay) {
		try{
			this.startDay = startDay;
			new GetEvents().execute();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public int getStartDay(){
		return startDay;
	}
	
	public int getNumOfEvenets(){
		return events.size();
	}
	
	/**
	 * Returns a list of all the colors on a day
	 * 
	 * @return list of colors
	 */
	public Set<Integer> getColors(){
		Set<Integer> colors = new HashSet<Integer>();
        try {
            for(Event event : events){
                colors.add(event.getColor());
            }
        }
        catch (ConcurrentModificationException ce) {
            ce.printStackTrace();
        }
		return colors;
	}
	
	/**
	 * Get all the events on the day
	 * 
	 * @return list of events
	 */
	public ArrayList<Event> getEvents(){
		return events;
	}
	
	public void setAdapter(BaseAdapter adapter){
		this.adapter = adapter;
	}
	
	private class GetEvents extends AsyncTask<String,Void,Void>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			/*if(prev!=null){
				prev.setClickable(false);
			}
			if(next!=null){
				next.setClickable(false);
			}*/
		}
		
		@Override
		protected Void doInBackground(String... params) {
			/*Cursor c = context.getContentResolver().query(CalendarProvider.CONTENT_URI,new String[] {CalendarProvider.ID,CalendarProvider.EVENT,
					CalendarProvider.DESCRIPTION,CalendarProvider.LOCATION,CalendarProvider.START,CalendarProvider.END,CalendarProvider.COLOR},"?>="+CalendarProvider.START_DAY+" AND "+ CalendarProvider.END_DAY+">=?",
					new String[] {String.valueOf(startDay),String.valueOf(startDay)}, null);*/
			//This method is called for each day and we have to query db for events of the current day
			MySql dbHelper = MySql.getInstance(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Calendar cal = Calendar.getInstance();
			//Log.i("Tag", "year= "+year+" month= "+month+" day= "+day);
			cal.set(year, month, day,0,0,0);
			long startOfTheDay=cal.getTimeInMillis();
			//Log.i("Tag", "month="+cal.get(Calendar.MONTH)+"day= "+cal.get(Calendar.DAY_OF_MONTH));
			//Log.i("Tag", "time= "+cal.getTime());
			cal.set(year, month, day,23,59,59);
			long endOfTheDay=cal.getTimeInMillis();
			//Log.i("Tag", "month="+cal.get(Calendar.MONTH)+"day= "+cal.get(Calendar.DAY_OF_MONTH));
			//Log.i("Tag", "start time= "+startOfTheDay+" end time= " + endOfTheDay);
			String selection="START_TIME"+">="+startOfTheDay+" AND "+"START_TIME"+"<="+endOfTheDay;
			Cursor cursor=db.query("remindertbNew", null, selection, null, null, null, null);
			if(cursor != null && cursor.moveToFirst()){
				do{
					Event event = new Event(cursor.getLong(cursor.getColumnIndex("_id")),cursor.getLong(cursor.getColumnIndex("START_TIME")),cursor.getLong(cursor.getColumnIndex("END_TIME")));
					event.setName(cursor.getString(cursor.getColumnIndex("SUBJECT")));
					event.setDescription(cursor.getString(cursor.getColumnIndex("NOTES")));
					event.setLocation(cursor.getString(cursor.getColumnIndex("LOCATION")));
					event.setColor(0);
					events.add(event);
				}while(cursor.moveToNext());	
				cursor.close();
			}
			db.close();
			return null;
		}
		
		protected void onPostExecute(Void par){
			adapter.notifyDataSetChanged();
		}
		
	}
	

}
