package smarter.uearn.money.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.homepage.HomeActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;
import smarter.uearn.money.utils.NotificationData;
import smarter.uearn.money.utils.ServerAPIConnectors.APIProvider;
import smarter.uearn.money.utils.ServerAPIConnectors.API_Response_Listener;
import smarter.uearn.money.utils.ServerAPIConnectors.Urls;
import smarter.uearn.money.utils.SmarterSMBApplication;
import smarter.uearn.money.utils.upload.DataUploadUtils;
import smarter.uearn.money.utils.webservice.ServiceApplicationUsage;

public class GrammarSkillTestActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    List<String> checkboxValues = new ArrayList<>();
    private Button nextButton;
    private TextView quesTitle;

    JSONArray rootArrayData = null;
    private int questionCount = 0;

    String rootQ = "";
    String rootR = "";
    String rootA = "";
    String rootLastUpdate = "";
    String radiobuttonData = "";
    String hintText = "";
    String titleText = "";
    String answer = "";

    String mandatory = "";
    String currentObject = "";
    String currentQuestionnaire = "";
    String currentQuestionnaireId = "";

    private LinkedHashMap radiobuttonHash = new LinkedHashMap();
    private LinkedHashMap tempQnAHash = new LinkedHashMap();
    List<String> tempQuesList = new ArrayList<>();
    private LinkedHashMap titleQHash = new LinkedHashMap();
    private LinearLayout radiobuttonsLayout;
    private TextView radiobuttonsTextView;
    private WebView radiobuttonsWebView;
    private LinkedHashMap questionnaireHash = new LinkedHashMap();
    private RadioGroup rg = null;
    private String currentSelectedRadioButton;
    private LinkedHashMap questionAnswerHash = new LinkedHashMap();
    private int totalCorrectAnswer = 0;
    private int totalQuestionCount = 0;
    private int currentQuestionCount = 0;
    private String gradeLevel = "";
    private Activity activity = null;
    private ProgressDialog progressDialog = null;
    private JSONObject responsejsonObject = null;
    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_test);
        restoreActionBar("Interview");
        getSupportActionBar().setHomeAsUpIndicator(CommonUtils.getDrawable(this));
        changeStatusBarColor(this);
        activity = this;

        nextButton = findViewById(R.id.btnNext);
        nextButton.setOnClickListener(this);
        nextButton.setText("NEXT");
        nextButton.setTextSize(20);
        nextButton.setEnabled(false); // nextButton initially disabled, once any options selected from radio group nextButton enabled
        nextButton.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);

        quesTitle = findViewById(R.id.quesTitle);

        radiobuttonsLayout = findViewById(R.id.radiobuttonsLayout);
        radiobuttonsTextView = findViewById(R.id.radiobuttonsTextView);

        radiobuttonsWebView = findViewById(R.id.radiobuttonsWebView);
        radiobuttonsWebView.getSettings().setJavaScriptEnabled(true);
        radiobuttonsWebView.getSettings().setBlockNetworkLoads(false);
        if (Build.VERSION.SDK_INT >= 19) {
            radiobuttonsWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        getGrammarTestQuestions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmarterSMBApplication.setCurrentActivity(this);
    }

    public void restoreActionBar(String mTitle) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + mTitle + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#7030A0")));
    }

    public void changeStatusBarColor(AppCompatActivity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_blue));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnNext) {
            if (nextButton.getText().toString().equalsIgnoreCase("NEXT")) {
                nextButtonAction();
            } else if (nextButton.getText().toString().equalsIgnoreCase("SUBMIT")) {
                nextButtonAction();
            }
        }
    }

    private void navigateToGrammarSkillTestResultActivity() {
        Intent intent = new Intent(this, GrammarSkillTestResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void sendGrammarTestData() {
        try {
            JSONObject jsonObject = new JSONObject(NotificationData.grammarTestAnswers);
            jsonObject.put("time_taken", NotificationData.timeTakenForGrammarTest);
            if (jsonObject != null) {
                responsejsonObject = DataUploadUtils.postGrammarTestInfoData(Urls.getGrammarTestInfo(), jsonObject);
            }

            if (responsejsonObject != null) {
                data = responsejsonObject.toString();
            }
            //data = "{\"success\":{\"totalQuestions\":15,\"totalCorrectAnswer\":12,\"totalWrongAnswer\":3,\"gradeLevel\":\"B2 (Upper-Intermediate English)\"}}";
            if (data != null && !data.isEmpty()) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject.has("success")) {
                            try {
                                String successObj = jsonObject.getString("success");
                                if (successObj != null && !successObj.isEmpty()) {
                                    JSONObject successObject = new JSONObject(successObj);
                                    if (successObject.has("gradeLevel")) {
                                        gradeLevel = successObject.getString("gradeLevel");
                                    }
                                }
                                if (gradeLevel != null && !gradeLevel.isEmpty()) {
                                    ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_LEVEL, gradeLevel);
                                    navigateToGrammarSkillTestResultActivity();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (jsonObject.has("errors")) {
                            String msg = null;

                            try {
                                msg = jsonObject.getString("errors");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (msg != null && !msg.isEmpty()) {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    private void sendGrammarTestInfoToServer() {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (activity != null && !activity.isFinishing()) {
                Thread thread = new Thread() {
                    public void run() {
                        Looper.prepare();

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendGrammarTestData();
                                handler.removeCallbacks(this);
                                Looper.myLooper().quit();
                            }
                        }, 1000);

                        Looper.loop();
                    }
                };
                thread.start();
            }
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void totalTimeConsumedInTest() {

        long grammarTestEndTime = System.currentTimeMillis();
        ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_END_TIME, grammarTestEndTime);
        long grammarTestStartTime = ApplicationSettings.getPref(AppConstants.GRAMMAR_TEST_START_TIME, 0l);
        long totalTimeConsumedInTest = getTimeDifference(grammarTestStartTime, grammarTestEndTime);
        int totalTimeTaken = (int)(totalTimeConsumedInTest / 1000);
        NotificationData.timeTakenForGrammarTest = String.valueOf(totalTimeTaken);
        ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_TOTAL_TIME, totalTimeConsumedInTest);
        sendGrammarTestInfoToServer();
    }

    static int getTimeDifference(long grammarTestStartTime, long grammarTestEndTime) {
        if (grammarTestEndTime > 0 && grammarTestStartTime > 0) {
            int totalTime = (int) grammarTestEndTime - (int) grammarTestStartTime;
            return totalTime;
        }
        return 0;
    }

    private void handleQuestionnaire(String qanda) {

        //String qanda = "[{\"q\":\"Chicago is a large city, _____?\",\"at\":\"RB\",\"rb_data\":\"[' doesn't it', 'aren't it', 'won't it', 'isn't it']\",\"answer\":\"isn't it\"},{\"q\":\"Don't leave your books near the open fire. They might easily _____.\",\"at\":\"RB\",\"rb_data\":\"['catch to fire', 'catch the fire', 'catch on fire', 'catch with fire']\",\"answer\":\"catch on fire\"},{\"q\":\"I have trouble _____.\",\"at\":\"RB\",\"rb_data\":\"['to remember my password', 'to remembering my password', 'remember my password', 'remembering my password']\",\"answer\":\"remembering my password\"},{\"q\":\"When will the meeting _____\",\"at\":\"RB\",\"rb_data\":\"['hold on', 'hold place', 'take on', 'take place']\",\"answer\":\"take place\"},{\"q\":\"Tomorrow is Paul's birthday. Let's _____ it.\",\"at\":\"RB\",\"rb_data\":\"['celebrate', 'praise', 'honor', 'congratulate']\",\"answer\":\"celebrate\"},{\"q\":\"Excuse me. Do you know where the bus terminal is? It is _____ the large police station.\",\"at\":\"RB\",\"rb_data\":\"['opposite of', 'opposite at', 'opposite with', 'opposite to']\",\"answer\":\"opposite to\"},{\"q\":\"Long hours and unsociable shifts _____ take their toll on health, relationships and family life.\",\"at\":\"RB\",\"rb_data\":\"['can', 'must', 'are able to', 'shouldn't']\",\"answer\":\"can\"},{\"q\":\"The hospital is now discounting 10 standard operations and _____ , ranging from having a baby and treating a cataract to undergoing a heart bypass.\",\"at\":\"RB\",\"rb_data\":\"['equipment', 'visits', 'medication', 'procedures']\",\"answer\":\"procedures\"},{\"q\":\"_____ are poor observers of their child's behavior so deviant behavior reaches unmanageable proportions.\",\"at\":\"RB\",\"rb_data\":\"['Parents', 'Parents that', 'When parents', 'If parents']\",\"answer\":\"Parents\"},{\"q\":\"Apple Computer has unveiled its new desktop computer design, _____ all disk drives and processors into a flat display less than two inches thick\",\"at\":\"RB\",\"rb_data\":\"['which includes', 'enclosing', 'which contains', 'which integrates']\",\"answer\":\"which integrates\"},{\"q\":\"By far the most noticeable blemishes on the surface of the Sun _____ sunspots.\",\"at\":\"RB\",\"rb_data\":\"['the', 'that are', 'in the', 'are']\",\"answer\":\"are\"},{\"q\":\"The ocean provides a dependable and affordable source of _____ for many.\",\"at\":\"RB\",\"rb_data\":\"['entertainment', 'recreation', 'pollution', 'fishing']\",\"answer\":\"recreation\"},{\"q\":\"Motor vehicle collisions are _____ of death in infants and children.\",\"at\":\"RB\",\"rb_data\":\"['primary reason', 'frequently resulting', 'often blamed', 'a leading cause']\",\"answer\":\"a leading cause\"},{\"q\":\"For humans, running a given distance requires 50 to 80 percent more energy than walking _____ distance does.\",\"at\":\"RB\",\"rb_data\":\"['equivalent', 'equal', 'the same', 'identical']\",\"answer\":\"the same\"},{\"q\":\"If you don't understand the text, don't hesitate _____.\",\"at\":\"RB\",\"rb_data\":\"['ask a question', 'asking a question', 'to ask a question', 'to asking a question']\",\"answer\":\"to ask a question\"}]";
        if (qanda != null && !qanda.isEmpty() && !qanda.equals("null")) {

            if (!qanda.equals("null")) {
                try {
                    rootArrayData = new JSONArray(qanda);
                    totalQuestionCount = rootArrayData.length();
                    JSONObject jsonobject = rootArrayData.getJSONObject(questionCount);
                    if (jsonobject.has("question")) {
                        rootQ = jsonobject.getString("question");
                        currentQuestionCount++;
                    }
                    if (jsonobject.has("type")) {
                        rootR = jsonobject.getString("type");
                        currentObject = rootR;
                    }
                    if (jsonobject.has("a")) {
                        rootA = jsonobject.getString("a");
                    }

                    if (jsonobject.has("title")) {
                        titleText = jsonobject.getString("title");
                    }

                    if (jsonobject.has("answer")) {
                        answer = jsonobject.getString("answer");
                    }

                    if (jsonobject.has("id")) {
                        currentQuestionnaireId = jsonobject.getString("id");
                    }

                    if (jsonobject.has("options")) {
                        radiobuttonData = jsonobject.getString("options");
                        if (radiobuttonData != null && radiobuttonData.length() > 0) {
                            radiobuttonHash.put(rootQ, radiobuttonData);
                        }
                    }
                    questionnaireHash.put(currentQuestionnaireId, rootR);
                    tempQuesList.add(questionCount, currentQuestionnaireId);
                    tempQnAHash.put(currentQuestionnaireId, rootR);
                    if (titleText != null && !titleText.isEmpty()) {
                        titleQHash.put(rootQ, titleText);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (rootR.equalsIgnoreCase("RB")) {
                radiobuttonLayout();
            }
        }
    }

    private void radiobuttonLayout() {
        quesTitle.setText("Question " + currentQuestionCount + " of " + totalQuestionCount);
        if (currentQuestionCount == totalQuestionCount) {
            nextButton.setText("SUBMIT");
        }
        radiobuttonsLayout.setVisibility(View.VISIBLE);
        if (rootQ.startsWith("<html>")) {
            radiobuttonsTextView.setVisibility(View.GONE);
            radiobuttonsWebView.setVisibility(View.VISIBLE);
            radiobuttonsWebView.loadData(rootQ, "text/html", "UTF-8");
        } else {
            radiobuttonsWebView.setVisibility(View.GONE);
            radiobuttonsTextView.setVisibility(View.VISIBLE);
            radiobuttonsTextView.setText(rootQ);
        }
        createRadioButtons(rootQ);
    }

    private void createRadioButtons(String key) {

        final List<String> radiobuttonValues = new ArrayList<>();

        if (radiobuttonHash.containsKey(key)) {
            String radiobuttonData = radiobuttonHash.get(key).toString();
            if (radiobuttonData != null && !radiobuttonData.isEmpty()) {
                if (radiobuttonData.contains("[")) {
                    radiobuttonData = radiobuttonData.replace("[", "");
                }
                if (radiobuttonData.contains("]")) {
                    radiobuttonData = radiobuttonData.replace("]", "");
                }
            }
            String[] radiobuttonArray = radiobuttonData.split(",");
            for (int i = 0; i < radiobuttonArray.length; i++) {
                if (radiobuttonArray[i].contains("'")) {
                    radiobuttonArray[i] = radiobuttonArray[i].substring(1, radiobuttonArray[i].length() - 1);
                    if (radiobuttonArray[i].contains("'")) {
                        radiobuttonArray[i] = radiobuttonArray[i].substring(1, radiobuttonArray[i].length());
                    }
                    radiobuttonValues.add(radiobuttonArray[i]);
                }
            }
        }

        int num = radiobuttonValues.size();

        LinearLayout layout = (LinearLayout) findViewById(R.id.radiobuttonsLayout);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.removeAllViews();

        if (rootQ.startsWith("<html>")) {
            radiobuttonsTextView.setVisibility(View.GONE);
            radiobuttonsWebView.setVisibility(View.VISIBLE);
            radiobuttonsWebView.loadData(rootQ, "text/html", "UTF-8");
        } else {
            radiobuttonsWebView.setVisibility(View.GONE);
            radiobuttonsTextView.setVisibility(View.VISIBLE);
            radiobuttonsTextView.setText(rootQ);
        }

        layout.addView(radiobuttonsTextView);
        layout.addView(radiobuttonsWebView);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.setMargins(20, 25, 25, 25);
        relativeLayout.setLayoutParams(relativeLayoutParams);

        RadioButton[] rb = new RadioButton[num];
        rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setOnCheckedChangeListener(this);

        for (int i = 0; i < radiobuttonValues.size(); i++) {
            rb[i] = new RadioButton(this);
            rb[i].setTextSize(16);
            rb[i].setTextColor(Color.parseColor("#707070"));
            Typeface faceRegular = Typeface.createFromAsset(SmarterSMBApplication.getInstance().getAssets(), "fonts/roboto-medium.ttf");
            rb[i].setTypeface(faceRegular);
            rg.addView(rb[i]);
            rb[i].setText(radiobuttonValues.get(i));
        }
        relativeLayout.addView(rg);
        layout.addView(relativeLayout);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        RadioButton button = (RadioButton) findViewById(checkedId);
        currentSelectedRadioButton = button.getText().toString();
        nextButton.setEnabled(true); // Enabled nextButton when the any options selected form radiogroup .
        nextButton.setBackgroundResource(R.drawable.red_rounded_corner);
    }

    private void nextButtonAction() {
        nextButton.setEnabled(false); // Disable nextButton for next question, and Set disabled background color.
        nextButton.setBackgroundResource(R.drawable.disable_rounded_corner);
        rootA = "";

        JSONObject jsonobject = null;
        runOnUiThread(new Runnable() {
            public void run() {
                updateHash();
            }
        });

        if (currentSelectedRadioButton != null && !currentSelectedRadioButton.isEmpty()) {
            try {
                if (rootArrayData != null) {
                    jsonobject = rootArrayData.getJSONObject(questionCount);

                    if (jsonobject.has(currentSelectedRadioButton)) {
                        rootArrayData = jsonobject.getJSONArray(currentSelectedRadioButton);
                        currentQuestionnaire = rootArrayData.toString();
                        questionCount = 0;
                        if (rootArrayData.length() != questionCount) {
                            jsonobject = rootArrayData.getJSONObject(questionCount);

                            if (jsonobject != null) {
                                if (jsonobject.has("question")) {
                                    rootQ = jsonobject.getString("question");
                                    currentQuestionCount++;
                                }
                                if (jsonobject.has("type")) {
                                    rootR = jsonobject.getString("type");
                                    currentObject = rootR;
                                }

                                if (jsonobject.has("a")) {
                                    rootA = jsonobject.getString("a");
                                }

                                if (jsonobject.has("last-updated")) {
                                    rootLastUpdate = jsonobject.getString("last-updated");
                                }
                                if (jsonobject.has("mandatory")) {
                                    mandatory = jsonobject.getString("mandatory");
                                    if (mandatory != null && !mandatory.isEmpty() && mandatory.equalsIgnoreCase("y")) {
                                        SmarterSMBApplication.isCurrentQuesMandatory = true;
                                    } else {
                                        SmarterSMBApplication.isCurrentQuesMandatory = false;
                                    }
                                } else {
                                    SmarterSMBApplication.isCurrentQuesMandatory = false;
                                }
                                if (jsonobject.has("title")) {
                                    titleText = jsonobject.getString("title");
                                }

                                if (jsonobject.has("answer")) {
                                    answer = jsonobject.getString("answer");
                                }

                                if (jsonobject.has("id")) {
                                    currentQuestionnaireId = jsonobject.getString("id");
                                }

                                if (jsonobject.has("hint")) {
                                    hintText = jsonobject.getString("hint");
                                }

                                if (jsonobject.has("options")) {
                                    radiobuttonData = jsonobject.getString("options");
                                    if (radiobuttonData != null && radiobuttonData.length() > 0) {
                                        radiobuttonHash.put(rootQ, radiobuttonData);
                                    }
                                }
                            }
                            questionnaireHash.put(currentQuestionnaireId, rootR);
                            tempQuesList.add(questionCount, currentQuestionnaireId);
                            tempQnAHash.put(currentQuestionnaireId, rootR);
                            if (titleText != null && !titleText.isEmpty()) {
                                titleQHash.put(rootQ, titleText);
                            }

                            if (rootR.equalsIgnoreCase("RB")) {
                                radiobuttonLayout();
                            }
                        }
                    } else {
                        questionCount++;
                        if (rootArrayData.length() != questionCount) {
                            jsonobject = rootArrayData.getJSONObject(questionCount);
                            currentQuestionnaire = rootArrayData.toString();

                            if (jsonobject != null) {
                                if (jsonobject.has("question")) {
                                    rootQ = jsonobject.getString("question");
                                    currentQuestionCount++;
                                }
                                if (jsonobject.has("type")) {
                                    rootR = jsonobject.getString("type");
                                    currentObject = rootR;
                                }

                                if (jsonobject.has("a")) {
                                    rootA = jsonobject.getString("a");
                                }

                                if (jsonobject.has("last-updated")) {
                                    rootLastUpdate = jsonobject.getString("last-updated");
                                }
                                if (jsonobject.has("mandatory")) {
                                    mandatory = jsonobject.getString("mandatory");
                                    if (mandatory != null && !mandatory.isEmpty() && mandatory.equalsIgnoreCase("y")) {
                                        SmarterSMBApplication.isCurrentQuesMandatory = true;
                                    } else {
                                        SmarterSMBApplication.isCurrentQuesMandatory = false;
                                    }
                                } else {
                                    SmarterSMBApplication.isCurrentQuesMandatory = false;
                                }
                                if (jsonobject.has("title")) {
                                    titleText = jsonobject.getString("title");
                                }

                                if (jsonobject.has("answer")) {
                                    answer = jsonobject.getString("answer");
                                }

                                if (jsonobject.has("id")) {
                                    currentQuestionnaireId = jsonobject.getString("id");
                                }

                                if (jsonobject.has("hint")) {
                                    hintText = jsonobject.getString("hint");
                                }

                                if (jsonobject.has("options")) {
                                    radiobuttonData = jsonobject.getString("options");
                                    if (radiobuttonData != null && radiobuttonData.length() > 0) {
                                        radiobuttonHash.put(rootQ, radiobuttonData);
                                    }
                                }
                            }
                            questionnaireHash.put(currentQuestionnaireId, rootR);
                            tempQuesList.add(questionCount, currentQuestionnaireId);
                            tempQnAHash.put(currentQuestionnaireId, rootR);
                            if (titleText != null && !titleText.isEmpty()) {
                                titleQHash.put(rootQ, titleText);
                            }

                            if (rootR.equalsIgnoreCase("RB")) {
                                radiobuttonLayout();
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentSelectedRadioButton = "";
        } else {
            try {
                if (rootArrayData != null) {
                    questionCount++;
                    if (rootArrayData.length() != questionCount) {
                        jsonobject = rootArrayData.getJSONObject(questionCount);
                        currentQuestionnaire = rootArrayData.toString();
                        if (jsonobject != null) {
                            if (jsonobject.has("question")) {
                                rootQ = jsonobject.getString("question");
                                currentQuestionCount++;
                            }
                            if (jsonobject.has("type")) {
                                rootR = jsonobject.getString("type");
                                currentObject = rootR;
                            }

                            if (jsonobject.has("a")) {
                                rootA = jsonobject.getString("a");
                            }

                            if (jsonobject.has("last-updated")) {
                                rootLastUpdate = jsonobject.getString("last-updated");
                            }
                            if (jsonobject.has("mandatory")) {
                                mandatory = jsonobject.getString("mandatory");
                                if (mandatory != null && !mandatory.isEmpty() && mandatory.equalsIgnoreCase("y")) {
                                    SmarterSMBApplication.isCurrentQuesMandatory = true;
                                } else {
                                    SmarterSMBApplication.isCurrentQuesMandatory = false;
                                }
                            } else {
                                SmarterSMBApplication.isCurrentQuesMandatory = false;
                            }
                            if (jsonobject.has("title")) {
                                titleText = jsonobject.getString("title");
                            }

                            if (jsonobject.has("answer")) {
                                answer = jsonobject.getString("answer");
                            }

                            if (jsonobject.has("id")) {
                                currentQuestionnaireId = jsonobject.getString("id");
                            }

                            if (jsonobject.has("hint")) {
                                hintText = jsonobject.getString("hint");
                            }

                            if (jsonobject.has("options")) {
                                radiobuttonData = jsonobject.getString("options");
                                if (radiobuttonData != null && radiobuttonData.length() > 0) {
                                    radiobuttonHash.put(rootQ, radiobuttonData);
                                }
                            }
                        }
                        questionnaireHash.put(currentQuestionnaireId, rootR);
                        tempQuesList.add(questionCount, currentQuestionnaireId);
                        tempQnAHash.put(currentQuestionnaireId, rootR);
                        if (titleText != null && !titleText.isEmpty()) {
                            titleQHash.put(rootQ, titleText);
                        }

                        if (rootR.equalsIgnoreCase("RB")) {
                            radiobuttonLayout();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (questionAnswerHash != null && questionAnswerHash.size() == 15) {
            totalTimeConsumedInTest();
        }
    }

    private void updateHash() {
        String radioButtonText = String.valueOf(currentSelectedRadioButton);
        if (tempQuesList != null && tempQuesList.size() > 0) {
            for (int k = 0; k < tempQuesList.size(); k++) {
                Set set = tempQnAHash.entrySet();
                Iterator i = set.iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    String value = "";
                    String key = (String) me.getKey();
                    if (tempQuesList.contains(key)) {
                        value = (String) me.getValue();
                    }
                    if (value != null && !value.isEmpty()) {
                        if (value.equalsIgnoreCase("RB")) {
                            if (!questionAnswerHash.containsKey(key)) {
                                questionAnswerHash.put(key, radioButtonText);
                                if (radioButtonText != null && !radioButtonText.isEmpty() && radioButtonText.equalsIgnoreCase(answer)) {
                                    totalCorrectAnswer++;
                                    answer = "";
                                }
                            }
                        }
                    }
                }
            }
        }
        Set set = questionAnswerHash.entrySet();
        Iterator i = set.iterator();
        JSONObject bankFeedbackFormObj = null;
        JSONObject actionObj = null;
        JSONArray jsonArray = new JSONArray();
        while (i.hasNext()) {

            Map.Entry me = (Map.Entry) i.next();
            String question = (String) me.getKey();
            String answer = (String) me.getValue();

            bankFeedbackFormObj = new JSONObject();
            try {
                bankFeedbackFormObj.put("id", question);
                if (answer != null && !answer.isEmpty()) {
                    bankFeedbackFormObj.put("answer", answer);
                }
                jsonArray.put(bankFeedbackFormObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                int version_code = CommonUtils.getVersionCode(this);
                String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                        ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnActivity - updateHash()-1: " + e.getMessage();
                ServiceApplicationUsage.callErrorLog(message);
            }
        }

        JSONObject experienceObj = new JSONObject();
        try {
            experienceObj.put("answersList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
            int version_code = CommonUtils.getVersionCode(this);
            String message = "<br/><br/>eMail : " + ApplicationSettings.getPref(AppConstants.USERINFO_EMAIL, "") + "<br/>ID : " +
                    ApplicationSettings.getPref(AppConstants.USERINFO_ID, "") + "<br/><br/>App Version: " + version_code + "<br/><br/>UearnActivity - updateHash()-2: " + e.getMessage();
            ServiceApplicationUsage.callErrorLog(message);
        }
        NotificationData.grammarTestAnswers = experienceObj.toString();
    }

    private void getGrammarTestQuestions() {
        if (CommonUtils.isNetworkAvailable(this)) {
            if (activity != null && !activity.isFinishing()) {
                new APIProvider.GetQuestionsApi("", 0, activity, "Please wait..", new API_Response_Listener<String>() {
                    @Override
                    public void onComplete(String data, long request_code, int failure_code) {
                        if (data != null && !data.isEmpty()) {
                            try {
                                handleQuestionnaire(data);
                                long grammarTestStartTime = System.currentTimeMillis();
                                ApplicationSettings.putPref(AppConstants.GRAMMAR_TEST_START_TIME, grammarTestStartTime);
                            } catch (Exception e) {

                            }
                        } else {
                            Toast.makeText(GrammarSkillTestActivity.this, "No valid response from server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).requestForDataCall();
            }
        } else {
            Toast.makeText(this, "You have no internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
}
