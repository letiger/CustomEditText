package com.letiger.customedittext;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MyAdapter<T> extends BaseAdapter implements Filterable {

  //

  //private final LayoutInflater mInflater;

  //private int mResource;
  //private int mDropDownResource;
  //private int mFieldId = 0;
  //private boolean mNotifyOnChange = true;
  //private Context mContext;

  private final Object lock = new Object();
  private final LayoutInflater inflater;

  private int resource;
  private int textField = 0;
  private Context context;
  private List<T> objects;

  // A copy of the original mObjects array, initialized from and then used instead as soon as
  // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
  private ArrayList<T> originalValues;
  private ArrayFilter filter;
  private CharSequence prefix;

  public MyAdapter(Context context, int resource) {
    throw new RuntimeException("Stub!");
  }

  public MyAdapter(@NonNull Context context, @LayoutRes int resource,
      @IdRes int textViewResourceId) {
    this(context, resource, textViewResourceId, new ArrayList<T>());
  }

  public MyAdapter(Context context, int resource, T[] objects) {
    this(context, resource, 0, Arrays.asList(objects));
  }

  public MyAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId,
      @NonNull T[] objects) {
    this(context, resource, textViewResourceId, Arrays.asList(objects));
  }

  public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
    this(context, resource, 0, objects);
  }

  /**
   * Main constructor.
   *
   * @param context : The current context
   * @param resource : The resource id for the layout file of the list
   * @param textViewResourceId : The id of the text view residing in the layout.
   * @param objects : The objects represented in the drop down list
   */
  public MyAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId,
      @NonNull List<T> objects) {
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    this.resource = resource;
    this.textField = textViewResourceId;
    this.objects = objects;
  }

  @Override public int getCount() {
    return objects.size();
  }

  @Override public T getItem(int position) {
    return objects.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  public void addAll(Collection<? extends T> collection) {
    synchronized (lock) {
      if (originalValues != null) {
        originalValues.addAll(collection);
      } else {
        objects.addAll(collection);
      }
    }
    notifyDataSetChanged();
  }

  @Override public View getView(int position, View view, ViewGroup viewGroup) {
    View layout;
    TextView textView;

    if (view == null) {
      layout = inflater.inflate(resource, viewGroup, false);
    } else {
      layout = view;
    }

    // Assign the layout of the list
    if (textField == 0) {
      textView = (TextView) layout;
    } else {
      textView = layout.findViewById(textField);
    }

    T item = getItem(position);
    textView.setText(highlight(item.toString()));
    return layout;
  }

  @Override public Filter getFilter() {
    if (filter == null) {
      filter = new ArrayFilter();
    }

    return filter;
  }

  /**
   * The text from the user is used to constrain the content of the adapter.
   * If the item does not start with the text entered, it will be removed from the list.
   */
  private class ArrayFilter extends Filter {

    @Override protected FilterResults performFiltering(CharSequence s) {
      FilterResults results = new FilterResults();

      prefix = s;
      if (originalValues == null) {
        synchronized (lock) {
          originalValues = new ArrayList<T>(objects);
        }
      }

      if (s == null || s.length() == 0) {
        ArrayList<T> list;
        synchronized (lock) {
          list = new ArrayList<>(originalValues);
        }
        results.values = list;
        results.count = list.size();
      } else {
        String prefixString = s.toString().toLowerCase();

        ArrayList<T> values;
        synchronized (lock) {
          values = new ArrayList<T>(originalValues);
        }

        final int count = values.size();
        final ArrayList<T> newValues = new ArrayList<T>();

        for (int i = 0; i < count; i++) {
          final T value = values.get(i);
          final String valueText = value.toString().toLowerCase();

          // Match the prefix to the list of names
          if (valueText.startsWith(prefixString)) {
            newValues.add(value);
          }
        }

        results.values = newValues;
        results.count = newValues.size();
      }

      return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
      objects = (List<T>) filterResults.values;
      if (filterResults.count > 0) {
        notifyDataSetChanged();
      } else {
        notifyDataSetInvalidated();
      }
    }
  }

  public CharSequence highlight(String original) {
    if (prefix == null || prefix.toString().equals("")) {
      return original;
    }
    String search = prefix.toString().toLowerCase(Locale.getDefault());
    String text = original.toLowerCase(Locale.getDefault());
    int start = text.indexOf(search);
    if (start < 0) {
      return original;
    } else {
      Spannable highlighted = new SpannableString(original);
      while (start >= 0) {
        int spanStart = Math.min(start, original.length());
        int spanEnd = Math.min(start + search.length(), original.length());
        highlighted.setSpan(new ForegroundColorSpan(Color.BLUE), spanStart, spanEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = text.indexOf(search, spanEnd);
      }
      return highlighted;
    }
  }
}
