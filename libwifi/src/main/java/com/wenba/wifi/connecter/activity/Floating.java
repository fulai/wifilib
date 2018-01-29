package com.wenba.wifi.connecter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.wenba.wifi.connecter.R;

import java.util.Locale;

/**
 * A dialog-like floating activity
 * @author Kevin Yuan
 *
 */
public class Floating extends Activity {
	
	private static final int[] BUTTONS = {R.id.button1, R.id.button2, R.id.button3};
	private Button saveButton;
	private View mView;
	private ViewGroup mContentViewContainer;
	private Content mContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// It will not work if we setTheme here.
		// Please add android:theme="@android:style/Theme.Dialog" to any descendant class in AndroidManifest.xml!
		// See http://code.google.com/p/android/issues/detail?id=4394
		// setTheme(android.R.style.Theme_Dialog);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		
		mView = View.inflate(this, R.layout.floating, null);
		final DisplayMetrics dm = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mView.setMinimumWidth(Math.min(dm.widthPixels, dm.heightPixels) - 20);
		setContentView(mView);
		saveButton = mView.findViewById(R.id.button2);
		mContentViewContainer = (ViewGroup) mView.findViewById(R.id.content);
	}

	public Button getSaveButton(){
		return saveButton;
	}
	
	private void setDialogContentView(final View contentView) {
		mContentViewContainer.removeAllViews();
		mContentViewContainer.addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void setContent(Content content) {
		mContent = content;
		refreshContent();
	}

	public void refreshContent() {
		setDialogContentView(mContent.getView());
		((TextView)findViewById(R.id.title)).setText(mContent.getTitle());
		
		final int btnCount = mContent.getButtonCount();
		if(btnCount > BUTTONS.length) {
			throw new RuntimeException(String.format(Locale.US, "%d exceeds maximum button count: %d!", btnCount, BUTTONS.length));
		}
		findViewById(R.id.buttons_view).setVisibility(btnCount > 0 ? View.VISIBLE : View.GONE);
		for(int buttonId:BUTTONS) {
			final Button btn = (Button) findViewById(buttonId);
			btn.setOnClickListener(null);
			btn.setVisibility(View.GONE);
		}

		for(int btnIndex = 0; btnIndex < btnCount; btnIndex++){
			final Button btn = (Button)findViewById(BUTTONS[btnIndex]);
			btn.setText(mContent.getButtonText(btnIndex));
			btn.setVisibility(View.VISIBLE);
			btn.setOnClickListener(mContent.getButtonOnClickListener(btnIndex));
		}
	}
	
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if(mContent != null) {
			mContent.onCreateContextMenu(menu, v, menuInfo);
		}
	}
	
	public boolean onContextItemSelected (MenuItem item) {
		if(mContent != null) {
			return mContent.onContextItemSelected(item);
		}
		return false;
	}
	
	
	public interface Content {
		CharSequence getTitle();
		View getView();
		int getButtonCount();
		CharSequence getButtonText(int index);
		OnClickListener getButtonOnClickListener(int index);
		void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
		boolean onContextItemSelected (MenuItem item);
	}
}
