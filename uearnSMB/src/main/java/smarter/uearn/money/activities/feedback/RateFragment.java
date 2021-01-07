package smarter.uearn.money.activities.feedback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.feedback.interfaces.OtherInterface;
import smarter.uearn.money.activities.feedback.interfaces.RateInterface;
import smarter.uearn.money.activities.feedback.interfaces.SuggestionsInterface;


public class RateFragment extends Fragment {

    public RateFragment() {
        // Required empty public constructor
    }

    View view;
    RateInterface sendMessage;
    private RatingBar ratingView=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            sendMessage = (RateInterface) getActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_rate, container, false);


        ratingView=view.findViewById(R.id.ratingBar);
        ratingView.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // Called when the user swipes the RatingBar
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                sendMessage.sendRatingInfo(String.valueOf(rating));
            }
        });
        return view;
    }
}