package smarter.uearn.money.activities.feedback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.feedback.interfaces.ErrorFeedBackInterface;


public class ErrorFragment extends Fragment {

    public ErrorFragment() {
        // Required empty public constructor
    }

    View view;
    EditText etError;
    ErrorFeedBackInterface sendMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            sendMessage = (ErrorFeedBackInterface) getActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_error, container, false);

        etError=view.findViewById(R.id.etError);

        etError.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()>0){
                    sendMessage.sendErrorFeedBack(etError.getText().toString().trim());
                }
                // myOutputBox.setText(s);

            }
        });
        return view;
    }
}