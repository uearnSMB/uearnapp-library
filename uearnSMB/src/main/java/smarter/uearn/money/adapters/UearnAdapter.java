package smarter.uearn.money.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import smarter.uearn.money.R;

import java.util.ArrayList;
import java.util.Map;

public class UearnAdapter extends BaseAdapter {
    private final ArrayList mData;

    public UearnAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.uearn_adapter_item, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, String> item = getItem(position);

        // TODO replace findViewById by ViewHolder
        ((TextView) result.findViewById(R.id.text1)).setText("Q: ");
        ((TextView) result.findViewById(R.id.text2)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.text3)).setText("A: ");
        ((TextView) result.findViewById(R.id.text4)).setText(item.getValue());

        return result;
    }
}
