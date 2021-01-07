package smarter.uearn.money.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import smarter.uearn.money.models.CustomerDetails;
import smarter.uearn.money.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FilledFormsAdapter extends BaseAdapter {
    private List<CustomerDetails> customerDetails;

    public FilledFormsAdapter(List<CustomerDetails> customerDetails) {
        this.customerDetails = customerDetails;
    }

    @Override
    public int getCount() {
        return customerDetails.size();
    }

    @Override
    public CustomerDetails getItem(int position) {
        return customerDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.uearn_list_item, null);
        } else {
            result = convertView;
        }

        CustomerDetails customer = customerDetails.get(position);

        TextView customerName = result.findViewById(R.id.customerName);
        TextView time = result.findViewById(R.id.timeString);
        TextView date = result.findViewById(R.id.dateString);
        TextView leadSource = result.findViewById(R.id.leadSource);

        String updatedAt = customer.updatedAt;
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date showDate = null;
        try {
            showDate = sd1.parse(updatedAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String timeString = DateFormat.format("hh:mm a", showDate).toString();
        String dateString = DateFormat.format("dd MMM ", showDate).toString();

        customerName.setText(customer.customerName);
        time.setText(timeString);
        date.setText(dateString);
        leadSource.setText(customer.leadSource);
        return result;
    }
}
