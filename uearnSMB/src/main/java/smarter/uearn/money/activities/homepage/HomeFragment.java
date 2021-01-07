package smarter.uearn.money.activities.homepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils;
import smarter.uearn.money.R;
import smarter.uearn.money.activities.DeviceCheckActivity;
import smarter.uearn.money.activities.OnboardingActivity;
import smarter.uearn.money.activities.UearnFAQActivity;
import smarter.uearn.money.activities.interview_process.InterViewProcessActivity;
import smarter.uearn.money.training.activity.TrainingDashboardActivity;
import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;
import smarter.uearn.money.utils.CommonUtils;


public class HomeFragment extends Fragment implements View.OnClickListener {

    View root = null;
    Context context;
    NavController navController;
    LinearLayout lyInterviewProcess, lyOnboardingProcess, lyHelp, lyTraining;
    String userStatus = "";

    TextView tvHelpOnboard, tvHelpTraining;
    LinearLayout lyHelpOnboard, lyHelpTraining;
    boolean voiceTest, chatTest, emailTest, isOnBoard, isInterview, isTraining;
    ImageView imgInterview, imgOnboard, imgTraining, imgHelp;
    TextView tvInterview, tvOnBoard, tvTraining, tvHelp;
    ImageView uearnBanner,nonUearnBanner;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        initView(view);
    }

    private void initView(View view) {
        lyInterviewProcess = view.findViewById(R.id.lyInterviewProcess);
        lyOnboardingProcess = view.findViewById(R.id.lyOnboardingProcess);
        lyHelp = view.findViewById(R.id.lyHelp);
        lyTraining = view.findViewById(R.id.lyTraining);

        tvHelpOnboard = view.findViewById(R.id.tvHelpOnboard);
        tvHelpTraining = view.findViewById(R.id.tvHelpTraining);
        lyHelpOnboard = view.findViewById(R.id.lyHelpOnboard);
        lyHelpTraining = view.findViewById(R.id.lyHelpTraining);

        uearnBanner= view.findViewById(R.id.ly_Banner1);
        nonUearnBanner= view.findViewById(R.id.ly_Banner2);

        lyTraining.setOnClickListener(this);
        lyInterviewProcess.setOnClickListener(this);
        lyHelp.setOnClickListener(this);
        lyOnboardingProcess.setOnClickListener(this);
        tvHelpOnboard.setOnClickListener(this);
        tvHelpTraining.setOnClickListener(this);

        imgInterview = view.findViewById(R.id.imgInterview);
        imgOnboard = view.findViewById(R.id.imgOnboard);
        imgTraining = view.findViewById(R.id.imgTraining);
        imgHelp = view.findViewById(R.id.imgHelp);

        imgInterview.setOnClickListener(this);
        imgOnboard.setOnClickListener(this);
        imgTraining.setOnClickListener(this);
        imgHelp.setOnClickListener(this);

        tvInterview = view.findViewById(R.id.tvInterview);
        tvOnBoard = view.findViewById(R.id.tvOnBoard);
        tvTraining = view.findViewById(R.id.tvTraining);
        tvHelp = view.findViewById(R.id.tvHelp);

        try{
            String profileProjectType= ApplicationSettings.getPref(AppConstants.MY_PROFILE_PROJECT_TYPE,"");
            if(profileProjectType.equalsIgnoreCase("UEARN") || profileProjectType.equalsIgnoreCase("")){
                uearnBanner.setVisibility(View.VISIBLE);
                nonUearnBanner.setVisibility(View.GONE);
            }else if(profileProjectType.contains("_NONSMBGIG")){
                uearnBanner.setVisibility(View.GONE);
                nonUearnBanner.setVisibility(View.VISIBLE);
            }else{
                uearnBanner.setVisibility(View.VISIBLE);
                nonUearnBanner.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        validateAppStatus();
        super.onResume();
    }

    private void validateAppStatus() {
        userStatus = ApplicationSettings.getPref(AppConstants.USER_STATUS, "");
        boolean registered = (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase(String.valueOf(AppConstants.USER_STATUS_REGISTERED)));
        boolean readyForTest = (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_READY_FOR_TESTS));
        boolean voiceTest = (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_VOICE_TEST));
        boolean emailTest = (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_EMAIL_TEST));
        boolean chatTest = (userStatus != null && !userStatus.isEmpty() && userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_CHAT_TEST));

        if (registered || readyForTest || voiceTest || emailTest || chatTest) {
            updateInterview(true);
            updateTraining(false);
            updateOnBoarding(false);
            isInterview = true;
        } else {
            updateInterview(false);
            isInterview = false;
        }

        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_ON_BOARDING))) {
            boolean onboardingProcessCompleted = ApplicationSettings.getPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, false);
            if (onboardingProcessCompleted) {
                updateOnBoarding(false);
                updateInterview(false);
                updateTraining(false);
                isOnBoard = true;
            } else {
                updateOnBoarding(true);
                updateInterview(false);
                updateTraining(false);
                isOnBoard = true;
            }
        } else {
            updateOnBoarding(false);
            isOnBoard = false;
        }

        if (userStatus != null && !userStatus.isEmpty() && (userStatus.equalsIgnoreCase(AppConstants.USER_STATUS_IN_TRAINING))) {
            updateTraining(true);
            updateInterview(false);
            updateOnBoarding(false);
            isTraining = true;
        } else {
            updateTraining(false);
            isTraining = false;
        }
    }

    private void updateTraining(boolean isTraining) {
        if (getActivity() != null) {
            if (isTraining) {
                lyTraining.setClickable(true);
                imgTraining.setClickable(true);
                lyHelpTraining.setVisibility(View.GONE);
                imgTraining.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_training));
                tvTraining.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.active_color));
            } else {
                lyTraining.setClickable(false);
                imgTraining.setClickable(false);
                lyHelpTraining.setVisibility(View.VISIBLE);
                imgTraining.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_training_inactive));
                tvTraining.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.inactive_color));
            }
        }
    }

    private void updateOnBoarding(boolean isOnBoarding) {
        if (getActivity() != null) {
            if (isOnBoarding) {
                lyOnboardingProcess.setClickable(true);
                imgOnboard.setClickable(true);
                lyHelpOnboard.setVisibility(View.GONE);
                DrawableCompat.setTint(imgOnboard.getDrawable(), ContextCompat.getColor(getActivity().getApplicationContext(), R.color.active_color));
                tvOnBoard.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.active_color));
            } else {
                lyOnboardingProcess.setClickable(false);
                imgOnboard.setClickable(false);
                tvHelpOnboard.setVisibility(View.VISIBLE);
                DrawableCompat.setTint(imgOnboard.getDrawable(), ContextCompat.getColor(getActivity().getApplicationContext(), R.color.inactive_color));
                tvOnBoard.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.inactive_color));
            }
        }
    }

    private void updateInterview(boolean isInterview) {
        if (getActivity() != null) {
            if (isInterview) {
                lyInterviewProcess.setClickable(true);
                imgInterview.setClickable(true);
                DrawableCompat.setTint(imgInterview.getDrawable(), ContextCompat.getColor(getActivity().getApplicationContext(), R.color.active_color));
                tvInterview.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.active_color));
            } else {
                lyInterviewProcess.setClickable(false);
                imgInterview.setClickable(false);
                DrawableCompat.setTint(imgInterview.getDrawable(), ContextCompat.getColor(getActivity().getApplicationContext(), R.color.inactive_color));
                tvInterview.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.inactive_color));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }

        int id = v.getId();
        if (id == R.id.lyTraining) {
            navigateToTrainingDashboardActivity();
        } else if (id == R.id.imgTraining) {
            navigateToTrainingDashboardActivity();
        } else if (id == R.id.lyInterviewProcess) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), InterViewProcessActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (id == R.id.imgInterview) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), InterViewProcessActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (id == R.id.lyHelp) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), UearnFAQActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (id == R.id.imgHelp) {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), UearnFAQActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (id == R.id.lyOnboardingProcess) {
            navigateToOnboardingActivity();
        } else if (id == R.id.imgOnboard) {
            navigateToOnboardingActivity();
        } else if (id == R.id.tvHelpOnboard) {
            showPopup(v);
        } else if (id == R.id.tvHelpTraining) {
            showPopup(v);
        }
    }

    private void navigateToOnboardingActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), OnboardingActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void navigateToTrainingDashboardActivity() {
        if (getActivity() != null) {
            if (CommonUtils.isNetworkAvailable(getActivity())) {
                Intent intent = new Intent(getActivity(), TrainingDashboardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                Toast.makeText(getActivity(), "Please check your network", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPopup(View view) {

        if (view==null){
            return;
        }
        int id = view.getId();
        int position = Gravity.TOP;
        if (id == R.id.tvHelpOnboard) {
            position = Gravity.TOP;
        } else if (id == R.id.tvHelpTraining) {
            position = Gravity.TOP;
        }

        final SimpleTooltip tooltip = new SimpleTooltip.Builder(getActivity())
                .anchorView(view)
                .gravity(position)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(false)
                .modal(true)
                .animated(true)
                .overlayMatchParent(false)
                .animationPadding(SimpleTooltipUtils.pxFromDp(4))
                .contentView(R.layout.item_help_dialog)
                .focusable(true)
                .maxWidth(R.dimen.simpletooltip_max_width)
                .build();

        final ImageView imgInterview = tooltip.findViewById(R.id.img1);
        final ImageView imgOnboard = tooltip.findViewById(R.id.img2);
        final ImageView imgTraining = tooltip.findViewById(R.id.img3);
        final TextView tvMessage = tooltip.findViewById(R.id.tvMessage);
        tvMessage.setText("The Interview process is not complete. Upon clearance this process will be initiated.");

        if (isInterview) {
            imgInterview.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_pink));
        } else {
            imgInterview.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle));
        }

        if (isOnBoard) {
            boolean onboardingProcessCompleted = ApplicationSettings.getPref(AppConstants.ONBOARDING_PROCESS_COMPLETED, false);
            if (onboardingProcessCompleted) {
                tvMessage.setText("Documents submitted is under review. Upon clearance training process will be initiated.");
                imgOnboard.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_pink));
            } else {
                tvMessage.setText("The Interview process is not complete. Upon clearance this process will be initiated.");
                imgOnboard.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_pink));
            }
        } else {
            imgOnboard.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle));
        }
        if (isTraining) {
            tvMessage.setText("The Onboarding process is not complete. Upon clearance this process will be initiated.");
            imgTraining.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle_pink));
        } else {
            imgTraining.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_circle));
        }
        tooltip.show();
    }
}

