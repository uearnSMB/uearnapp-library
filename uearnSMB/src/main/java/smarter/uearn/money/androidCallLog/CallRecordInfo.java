package smarter.uearn.money.androidCallLog;

import java.util.Date;


public class CallRecordInfo {

	public long date;
	public long duration;
	public boolean New;
	public String number;
	public int Type;
	
	public String toString() {
		return ("Number " + number + " Date " + new Date(date).toString() + "Duration " + duration + "Type " + Type);
		
	}
	
}
