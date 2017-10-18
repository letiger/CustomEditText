package com.letiger.customedittext;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText
    implements AdapterView.OnItemClickListener {

  private static final Pattern pattern =
      Pattern.compile("(?<=\\s|^)@([a-z|A-Z|\\.|\\-|\\_|0-9]*)(?=\\s|$)");

  private ListPopupWindow listPopupWindow;
  private MyAdapter adapter;
  private int matchStart;
  private String userText;
  private boolean isSelected;
  private String prefix;

  public CustomEditText(Context context) {
    super(context);
  }

  public CustomEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    // Initialize list
    listPopupWindow = new ListPopupWindow(context, attrs);
    listPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    listPopupWindow.setPromptPosition(ListPopupWindow.POSITION_PROMPT_BELOW);
    listPopupWindow.setOnItemClickListener(this);

    addTextChangedListener(new MyWatcher());

    isSelected = true;
  }

  public void setAdapter(MyAdapter adapter) {
    this.adapter = adapter;
    listPopupWindow.setAdapter(adapter);
  }

  public void setDropDownAnimationStyle(int animationStyle) {
    listPopupWindow.setAnimationStyle(animationStyle);
  }

  public void ShowDropDownList() {

    if (listPopupWindow == null) {
      return;
    }

    listPopupWindow.setAnchorView(this);
    if (!listPopupWindow.isShowing()) {
      // Make sure the list does not obscure the IME when shown for the first time.
      listPopupWindow.setInputMethodMode(android.widget.ListPopupWindow.INPUT_METHOD_NEEDED);
    }

    requestFocus();
    listPopupWindow.show();
    listPopupWindow.getListView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
  }

  private String getUserText() {
    return userText;
  }

  private void setUserText(String userText) {
    this.userText = userText;
  }

  // make the selection
  @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
    if (isSelected) {
      return;
    }

    if (listPopupWindow.isShowing()) {
      Object selectedItem;

      if (position < 0) {
        selectedItem = listPopupWindow.getSelectedItem();
      } else {
        selectedItem = adapter.getItem(position);
      }

      if (selectedItem != null) {

        SpannableStringBuilder builder = new SpannableStringBuilder(getUserText());
        int userTextEnd = getUserText().trim().length() == 0 ? getUserText().trim().length()
            : getUserText().trim().length();
        builder.replace(matchStart, userTextEnd, selectedItem.toString() + " ");

        setText(builder);
        setSelection(builder.length());
        listPopupWindow.dismiss();
        isSelected = true;
      }
    }
  }

  private class MyWatcher implements TextWatcher {

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      if (listPopupWindow == null) {
        return;
      }

      // Match the input
      Matcher matcher = pattern.matcher(s.toString());
      while (matcher.find()) {
        if (matcher.start(1) <= start + count && start + count <= matcher.end(1)) {
          isSelected = false;
          matchStart = matcher.start(1);
          prefix = matcher.group(1);
          setUserText(s.toString());
          adapter.getFilter().filter(prefix);
          ShowDropDownList();

          return;
        }
      }

      // No match. Dismiss the list
      listPopupWindow.dismiss();
    }

    @Override public void afterTextChanged(Editable editable) {

    }
  }
}
