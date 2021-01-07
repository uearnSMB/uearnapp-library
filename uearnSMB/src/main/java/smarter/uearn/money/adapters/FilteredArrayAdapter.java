package smarter.uearn.money.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import smarter.uearn.money.models.GroupsUserInfo;
import smarter.uearn.money.R;
import smarter.uearn.money.views.events.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Simplified custom filtered ArrayAdapter
 * override keepObject with your test for filtering
 * <p>
 * Based on gist <a href="https://gist.github.com/tobiasschuerg/3554252/raw/30634bf9341311ac6ad6739ef094222fc5f07fa8/FilteredArrayAdapter.java">
 * FilteredArrayAdapter</a> by Tobias Schürg
 * <p>
 * Created on 9/17/13.
 * @author mgod
 */

abstract public class FilteredArrayAdapter<T> extends ArrayAdapter<GroupsUserInfo> {

    private List<GroupsUserInfo> originalObjects;
    private Filter filter;
    OnItemSelectedListener onItemSelectedListener;
    Toast toast;
    Handler handler;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    public FilteredArrayAdapter(Context context, int resource, GroupsUserInfo[] objects) {
        this(context, resource, 0, objects);
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public FilteredArrayAdapter(Context context, int resource, int textViewResourceId, GroupsUserInfo[] objects) {
        this(context, resource, textViewResourceId, new ArrayList<GroupsUserInfo>(Arrays.asList(objects)));
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects The objects to represent in the ListView.
     */
    @SuppressWarnings("unused")
    public FilteredArrayAdapter(Context context, int resource, List<GroupsUserInfo> objects, OnItemSelectedListener onItemSelectedListener) {
        this(context, resource, 0, objects);
        this.onItemSelectedListener = onItemSelectedListener;
        handler = new Handler(context.getMainLooper());
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param resource The resource ID for a layout file containing a layout to use when
     *                 instantiating views.
     * @param textViewResourceId The id of the TextView within the layout resource to be populated
     * @param objects The objects to represent in the ListView.
     */
    public FilteredArrayAdapter(Context context, int resource, int textViewResourceId, List<GroupsUserInfo> objects) {
        super(context, resource, textViewResourceId, new ArrayList<GroupsUserInfo>(objects));
        this.originalObjects = objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyDataSetChanged() {
        ((AppFilter)getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyDataSetInvalidated(){
        ((AppFilter)getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetInvalidated();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter(originalObjects);
        return filter;
    }

    /**
     * Filter method used by the adapter. Return true if the object should remain in the list
     *
     * @param obj object we are checking for inclusion in the adapter
     * @param mask current text in the edit text we are completing against
     * @return true if we should keep the item in the adapter
     */
    abstract protected boolean keepObject(GroupsUserInfo obj, String mask);

    /**
     * Class for filtering Adapter, relies on keepObject in FilteredArrayAdapter
     *
     * based on gist by Tobias Schürg
     * in turn inspired by inspired by Alxandr
     *         (http://stackoverflow.com/a/2726348/570168)
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = l.inflate(R.layout.customfor_teammembers_autocomplete, parent, false);
            LinearLayout linearLayout = convertView.findViewById(R.id.mainLinearLayout);

            GroupsUserInfo p = getItem(position);
            ((TextView)convertView.findViewById(R.id.nameText)).setText(p.name);
            ((TextView)convertView.findViewById(R.id.emailText)).setText(p.email);
            ((TextView) convertView.findViewById(R.id.phoneNumberText)).setText(p.phone);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelectedListener.onItemSelected(getItem(position));
                }
            });
        }

        return convertView;
    }

    private class AppFilter extends Filter {

        private ArrayList<GroupsUserInfo> sourceObjects;

        public AppFilter(List<GroupsUserInfo> objects) {
            setSourceObjects(objects);
        }

        public void setSourceObjects(List<GroupsUserInfo> objects) {
            synchronized (this) {
                sourceObjects = new ArrayList<GroupsUserInfo>(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            FilterResults result = new FilterResults();
            if (chars != null && chars.length() > 0) {
                String mask = chars.toString();
                ArrayList<GroupsUserInfo> keptObjects = new ArrayList<GroupsUserInfo>();

                for (GroupsUserInfo object : sourceObjects) {
                    if (keepObject(object, mask))
                        keptObjects.add(object);
                }


                if (keptObjects.size() == 0 && chars.length() > 3) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            toast = Toast.makeText(getContext(), "Not a team member!!!", Toast.LENGTH_SHORT);
                            toast.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 250);
                        }
                    });
                }
                result.count = keptObjects.size();
                result.values = keptObjects;
            } else {
                // add all objects
                result.values = sourceObjects;
                result.count = sourceObjects.size();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.count > 0) {
                FilteredArrayAdapter.this.addAll((Collection)results.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}