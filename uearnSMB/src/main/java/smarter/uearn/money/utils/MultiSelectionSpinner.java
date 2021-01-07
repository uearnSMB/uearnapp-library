package smarter.uearn.money.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import smarter.uearn.money.R;

public class MultiSelectionSpinner extends android.support.v7.widget.AppCompatSpinner implements OnMultiChoiceClickListener {

    public interface OnMultipleItemsSelectedListener {
        void selectedIndices(List<Integer> indices);
        void selectedStrings(List<String> strings);
    }

    private OnMultipleItemsSelectedListener listener;

    String[] _items = null;
    boolean[] mSelection = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;

    ArrayAdapter<String> simple_adapter;

    public MultiSelectionSpinner(final Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/poppins.medium.ttf");
                ((TextView) v).setTypeface(font);
                ((TextView) v).setTextColor(getResources().getColor(R.color.ques_text_color));
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/poppins.medium.ttf");
                ((TextView) v).setTypeface(font);
                ((TextView) v).setTextColor(getResources().getColor(R.color.ques_text_color));
                return v;
            }
        };
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(final Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/poppins.medium.ttf");
                ((TextView) v).setTypeface(font);
                ((TextView) v).setTextColor(getResources().getColor(R.color.ques_text_color));
                return v;
            }
            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);
                Typeface font=Typeface.createFromAsset(context.getAssets(), "fonts/poppins.medium.ttf");
                ((TextView) v).setTypeface(font);
                ((TextView) v).setTextColor(getResources().getColor(R.color.ques_text_color));
                return v;
            }
        };
        super.setAdapter(simple_adapter);
    }

    public void setListener(OnMultipleItemsSelectedListener listener) {
        this.listener = listener;
    }

    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            simple_adapter.clear();
            simple_adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please select");
        builder.setMultiChoiceItems(_items, mSelection, this);
        _itemsAtStart = getSelectedItemsAsString();
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mSelection != null && mSelection.length > 0) {
                    System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                    listener.selectedIndices(getSelectedIndices());
                    listener.selectedStrings(getSelectedStrings());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);
                if(mSelectionAtStart != null && mSelectionAtStart.length > 0) {
                    System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
                }
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
    }

    public void setSelection(String[] selection) {
        if(mSelection != null && mSelection.length > 0) {
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
            for (String cell : selection) {
                for (int j = 0; j < _items.length; ++j) {
                    if (_items[j].equals(cell)) {
                        mSelection[j] = true;
                        mSelectionAtStart[j] = true;
                    }
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(List<String> selection) {
        if(mSelection != null && mSelection.length > 0) {
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
            for (String sel : selection) {
                for (int j = 0; j < _items.length; ++j) {
                    if (_items[j].equals(sel)) {
                        mSelection[j] = true;
                        mSelectionAtStart[j] = true;
                    }
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        if(mSelection != null && mSelection.length > 0) {
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices) {
        if(mSelection != null && mSelection.length > 0) {
            for (int i = 0; i < mSelection.length; i++) {
                mSelection[i] = false;
                mSelectionAtStart[i] = false;
            }
            for (int index : selectedIndices) {
                if (index >= 0 && index < mSelection.length) {
                    mSelection[index] = true;
                    mSelectionAtStart[index] = true;
                } else {
                    throw new IllegalArgumentException("Index " + index
                            + " is out of bounds.");
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        if(_items != null && _items.length > 0) {
            for (int i = 0; i < _items.length; ++i) {
                if (mSelection[i]) {
                    selection.add(_items[i]);
                }
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        if(_items != null && _items.length > 0) {
            for (int i = 0; i < _items.length; ++i) {
                if (mSelection[i]) {
                    selection.add(i);
                }
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        if(_items != null && _items.length > 0) {
            for (int i = 0; i < _items.length; ++i) {
                if (mSelection[i]) {
                    if (foundOne) {
                        sb.append(", ");
                    }
                    foundOne = true;

                    sb.append(_items[i]);
                }
            }
        }
        return sb.toString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;
        if(_items != null && _items.length > 0) {
            for (int i = 0; i < _items.length; ++i) {
                if (mSelection[i]) {
                    if (foundOne) {
                        sb.append(", ");
                    }
                    foundOne = true;
                    sb.append(_items[i]);
                }
            }
        }
        return sb.toString();
    }
}
