package gov.va.contentlib.controllers;

import java.util.ArrayList;
import java.util.List;

import gov.va.contentlib.UserDBHelper;
import gov.va.contentlib.Util;
import gov.va.contentlib.activities.ManageNavigationController;
import gov.va.contentlib.content.Content;
import gov.va.contentlib.views.LoggingButton;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PickSafetyDestinationController extends BaseExerciseController {

	static final int ANOTHER_30_SECONDS = 701;
	
	public PickSafetyDestinationController(Context ctx) {
		super(ctx);
	}
/*
	@Override
	public boolean shouldAddButtonsInScroller() {
		return true;
	}
*/	
	@Override
	public void build() {
		super.build();
		
		List<Content> children = getContent().getChildren();
		final List<EditText> fields = new ArrayList<EditText>();

		final UserDBHelper db = UserDBHelper.instance(getNavigator());
//		lastWebView.setFocusable(true);
//		lastWebView.setFocusableInTouchMode(false);
//		lastWebView.setContentDescription("");
		
		for (Content child : children) {
			if ((child.getName() != null) && !child.getName().startsWith("@")) {
				
				WebView wv = createWebView(child.getMainText());
				wv.setFocusable(true);
				wv.setFocusableInTouchMode(false);
				clientView.addView(wv);
/*				
				TextView tv = new TextView(getContext());
				tv.setText(" "+child.getMainText());
				tv.setTextColor(0xFFFFFFFF);
				tv.setFocusable(true);
				tv.setFocusableInTouchMode(false);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.leftMargin = 10;
				lp.bottomMargin = 3;
				clientView.addView(tv);//,lp);
*/
				
				final String key = child.getName();
				String placeholder = child.getStringAttribute("exampleText");
				final String storeAs = child.getStringAttribute("storeAs");
				String value = (storeAs != null) ? db.getSetting(storeAs) : null;
				EditText field = new EditText(getContext());
				field.setMaxLines(4);
				field.setMinLines(4);
				field.setHint(placeholder);
				field.setGravity(Gravity.LEFT|Gravity.TOP);
				if (value != null) {
					field.setText(value);
				}
				field.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						if (storeAs != null) db.setSetting(storeAs, s.toString());
					}
					
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
					public void afterTextChanged(Editable s) {}
				});
								
				fields.add(field);
				clientView.addView(field);
			}
		}
		
		Content next = getContent().getNext();
		LoggingButton b = addButton(next.getDisplayName());
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (EditText field : fields) {
					Editable s = field.getText();
					if ((s == null) || (s.length() == 0)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
						builder.setTitle("Some Fields Empty");
						builder.setMessage("Please fill in the requested fields to continue.");
						builder.setPositiveButton("Ok", null);
						builder.show();
						return;
					}
				}

				Content next = getContent().getNext();
				getNavigator().pushViewForContent(next);
			}
		});
	}
}