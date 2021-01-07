package smarter.uearn.money.training.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.BaseActivity;
import smarter.uearn.money.training.adapter.TrainingHelpChartAdapter;
import smarter.uearn.money.training.model.HelpDeskInfoData;
import smarter.uearn.money.training.model.HelpDeskInfoDataObject;
import smarter.uearn.money.training.model.TrainingDataResponse;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;

public class ChartHelpActivity extends BaseActivity implements TrainingHelpChartAdapter.onClickAnswerButton {

    LinearLayout ll_notification, ll_sync;
    RecyclerView recylerChart;
    TrainingHelpChartAdapter trainingHelpChartAdapter;
    LinearLayoutManager linearLayoutManager;
    ImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_help);
        recylerChart=(RecyclerView)findViewById(R.id.recylerChart);
       // String  stringData=getIntent().getStringExtra("str");
//        ArrayList arrayListData=new ArrayList();
//        arrayListData.add(stringData);
        trainingHelpChartAdapter=new TrainingHelpChartAdapter(this,new ArrayList<>(),this);
        linearLayoutManager =new LinearLayoutManager(this);
        recylerChart.setLayoutManager(linearLayoutManager);
        recylerChart.setAdapter(trainingHelpChartAdapter);
        ll_notification = (LinearLayout) findViewById(R.id.ll_notification);
        ll_sync = (LinearLayout) findViewById(R.id.ll_sync);
        ll_sync.setVisibility(View.GONE);
        ll_notification.setVisibility(View.GONE);
        profile_image = (ImageView) findViewById(R.id.profile_image);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        changeStatusBarColor(this);
        getHelpDeskQuestion();
    }

    public void changeStatusBarColor(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }
    @Override
    public void onAnswerQuesion(HelpDeskInfoData strAnswer, int position) {
        Log.e("ChatHelp","Response :: "+strAnswer.getQuestionID());
        //After Api response
        submitAnswer(strAnswer,position);

    }
    private void getHelpDeskQuestion(){
        try {

            new APIProvider.GetHelpDeskQuestion(getIntent().getStringExtra("str"), 1, ChartHelpActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {
//                        JSONObject jsonObject;
//                        String successData = null;
//                        try {
//                            jsonObject = new JSONObject(data);
//                            successData = jsonObject.getString("success");
//                        }catch (Exception e){
//
//                        }
                        Gson g = new Gson();
                        HelpDeskInfoData helpDeskInfoDataObject = g.fromJson(data, HelpDeskInfoData.class);
                        trainingHelpChartAdapter.addItem(helpDeskInfoDataObject);
                     //   initUI(trainingDataResponse);
                    }
                }
            }).call();
        } catch (Exception ex) {
        }
    }
    private void submitAnswer(HelpDeskInfoData strAnswer, final int position){
        try {

            new APIProvider.SubmitHelpDeskAnswer(strAnswer, 1, ChartHelpActivity.this, "Processing your request.", new API_Response_Listener<String>() {

                @Override
                public void onComplete(String data, long request_code, int failure_code) {
                    if (data != null) {

                        Gson g = new Gson();
                        HelpDeskInfoData helpDeskInfoDataObject = g.fromJson(data, HelpDeskInfoData.class);
                     //  if(null != helpDeskInfoDataObject.getQuestionId()) {
                            trainingHelpChartAdapter.addItem(helpDeskInfoDataObject);
                            linearLayoutManager.scrollToPositionWithOffset(position - 2, position);
                     //   }
                        //   initUI(trainingDataResponse);
                    }
                }
            }).call();
        } catch (Exception ex) {
        }
    }
}