package smarter.uearn.money.fragments.aftercall;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import smarter.uearn.money.R;
import smarter.uearn.money.activities.AfterCallActivity;
import smarter.uearn.money.utils.ApplicationSettings;

public class AfterCallNotesFragment extends Fragment {

    private static final String ARG_NOTES = "notes";
    private String mNotes;
    private static final String TAG = AfterCallNotesFragment.class.getSimpleName();
    EditText mEditNotes;


    public AfterCallNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param notes - the notes for the appointment
     * @return A new instance of fragment AfterCallNotesFragment.
     */
    public static AfterCallNotesFragment newInstance(String notes) {
        AfterCallNotesFragment fragment = new AfterCallNotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOTES, notes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNotes = getArguments().getString(ARG_NOTES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_after_call_notes, container, false);
        mEditNotes = view.findViewById(R.id.et_notes);
        mEditNotes.setText(mNotes);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ApplicationSettings.putPref(AfterCallActivity.AFTER_CALL_NOTES, mEditNotes.getText().toString());
        //Log.d(TAG, "detached" + mEditNotes.getText().toString());
    }
}
