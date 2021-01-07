package smarter.uearn.money.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.UearnHome;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.NetworkInformation;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.Utils;

import static smarter.uearn.money.activities.UearnHome.internet_status;
import static smarter.uearn.money.activities.UearnHome.pingInProgress;
import static smarter.uearn.money.activities.UearnHome.pingResponse;
import static smarter.uearn.money.activities.UearnHome.pingResponselist;
import static smarter.uearn.money.activities.UearnHome.pingTimer;
import static smarter.uearn.money.activities.UearnHome.swipe_to_start;
import static smarter.uearn.money.activities.UearnHome.timerStopped;
import static smarter.uearn.money.activities.UearnHome.internet_status_goal_before;

public class NetworkReceiver extends BroadcastReceiver {
    NetworkInformation networkInformation = new NetworkInformation();
    @Override
    public void onReceive(Context context, Intent intent) {
        internet_status.setText(networkInformation.ConnectionQuality(context));
        internet_status_goal_before.setText(networkInformation.ConnectionQuality(context));
        getNetworkSpeed(context);
        Log.e("Network","status :: "+networkInformation.ConnectionQuality(context));
        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
            if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")){
                internet_status.setTextColor(context.getApplicationContext().getResources().getColor(R.color.uearn_red_color));
                internet_status_goal_before.setTextColor(context.getApplicationContext().getResources().getColor(R.color.uearn_red_color));
            }
            swipe_to_start.setEnabled(false);
            swipe_to_start.setClickable(false);
            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
            swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
        }else{
            internet_status.setTextColor(context.getApplicationContext().getResources().getColor(R.color.internet_status_color));
            internet_status_goal_before.setTextColor(context.getApplicationContext().getResources().getColor(R.color.internet_status_color));
            swipe_to_start.setEnabled(true);
            swipe_to_start.setClickable(true);
            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
            swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
        }
        //new NetworkSpeedTest().execute();
    }

    public void getNetworkSpeed(Context context){
        AsyncTaskPing asyncTask=new AsyncTaskPing(context);
        asyncTask.execute("");
    }

    class AsyncTaskPing extends AsyncTask<String, Void, ArrayList<Double>> {
        boolean isMobiledata = false;
        Context context;
        public AsyncTaskPing(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected ArrayList<Double>  doInBackground(String... strings) {

            try {
                pingInProgress  = true;
                Log.e("UearnHome","Response receiver :: 11 ");
                String response = Utils.ping("www.google.com");
                Log.e("UearnHome","Response ::receiver 44 "+response);
                if (response != null && !response.isEmpty()) {
                    if(response.contains(".")) {
                        int index = response.indexOf(".") + 2;
                        if (index > 0) {
                            String str = response.substring(0, index);
                            if (str != null && !str.isEmpty()) {
                                Double d = Double.valueOf(str);
                                pingResponselist.add(d);
                            }
                        }
                    } else {
                        if (response.contains(" ")) {
                            response = response.substring(0, response.indexOf(" "));
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        } else {
                            Double d = Double.valueOf(response);
                            pingResponselist.add(d);
                        }
                    }
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
            return pingResponselist;
        }
        @Override
        protected void onPostExecute(ArrayList<Double> pingResponselist) {
            super.onPostExecute(pingResponselist);
            int result = 0;
            if(pingTimer != null) {
                pingTimer.cancel();
                pingTimer = null;
            }
            timerStopped = true;
            double total = 0;
            if(pingResponselist != null && pingResponselist.size() > 0){
                for(int i=0; i<pingResponselist.size(); i++){
                    total = total + pingResponselist.get(i);
                }
                double average = total / pingResponselist.size();
                average = Math.round(average * 100.0) / 100.0;
                pingResponse = Double.toString(average);
            }
            pingInProgress  = false;
            if(timerStopped) {
                String str = "";
                timerStopped = false;
                if (pingResponse != null && !pingResponse.isEmpty() && pingResponse.contains(".")) {
                    str = pingResponse.split("\\.")[0];
                } else {
                    str = pingResponse;
                }

                if (str != null && !str.isEmpty()) {
                    str = str.replaceAll("\\s", "");
                    str = Utils.getNumericString(str);
                    result = Integer.parseInt(str);
                    SmarterSMBApplication.pingStatusResponse = str;
                    SmarterSMBApplication.pingStatusResponse = str;

                    if (result > 0 && result < 50) {
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            swipe_to_start.setEnabled(false);
                            swipe_to_start.setClickable(false);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                        }else{
                            internet_status.setText("GOOD");
                            internet_status_goal_before.setText("GOOD");
                            swipe_to_start.setEnabled(true);
                            swipe_to_start.setClickable(true);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                        }
                    }
                    else if (result >= 50 && result <= 500) {
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            swipe_to_start.setEnabled(false);
                            swipe_to_start.setClickable(false);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                        }else{
                            internet_status.setText("MODERATE");
                            internet_status_goal_before.setText("MODERATE");
                            swipe_to_start.setEnabled(true);
                            swipe_to_start.setClickable(true);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                        }
                    }
                    else if (result >= 500 && result <= 750) {
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            swipe_to_start.setEnabled(false);
                            swipe_to_start.setClickable(false);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                        }else{
                            internet_status.setText("AVERAGE");
                            internet_status_goal_before.setText("AVERAGE");
                            swipe_to_start.setEnabled(true);
                            swipe_to_start.setClickable(true);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                        }
                    }
                    else if (result > 750) {
                        internet_status.setText("POOR");
                        internet_status_goal_before.setText("POOR");
                        internet_status.setTextColor(context.getResources().getColor(R.color.no_connection));
                        internet_status_goal_before.setTextColor(context.getResources().getColor(R.color.no_connection));
                        if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                                || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                                || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                            swipe_to_start.setEnabled(false);
                            swipe_to_start.setClickable(false);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                        }else{
                            swipe_to_start.setEnabled(true);
                            swipe_to_start.setClickable(true);
                            swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                            swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                        }
                    }
                }else {
                    internet_status.setText("NO INTERNET");
                    internet_status_goal_before.setText("NO INTERNET");
                    internet_status.setTextColor(context.getResources().getColor(R.color.no_connection));
                    internet_status_goal_before.setTextColor(context.getResources().getColor(R.color.no_connection));
                    if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                            || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                            || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                        swipe_to_start.setEnabled(false);
                        swipe_to_start.setClickable(false);
                        swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                        swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                    }else{
                        swipe_to_start.setEnabled(true);
                        swipe_to_start.setClickable(true);
                        swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                        swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                    }
                }
            }else {
                if(internet_status.getText().toString().equalsIgnoreCase("Please connect to WIFI")
                        || internet_status.getText().toString().equalsIgnoreCase("NO INTERNET")
                        || internet_status.getText().toString().equalsIgnoreCase("POOR")) {
                    swipe_to_start.setEnabled(false);
                    swipe_to_start.setClickable(false);
                    swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                    swipe_to_start.setBackgroundResource(R.drawable.grey_rounded_corner);
                }else{
                    internet_status.setText("GOOD");
                    internet_status_goal_before.setText("GOOD");
                    swipe_to_start.setEnabled(true);
                    swipe_to_start.setClickable(true);
                    swipe_to_start.setTextColor(context.getApplicationContext().getResources().getColor(R.color.white));
                    swipe_to_start.setBackgroundResource(R.drawable.green_rounded_corner);
                }
            }
        }
    }
}
