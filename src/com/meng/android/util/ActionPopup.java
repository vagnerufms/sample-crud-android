package com.meng.android.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meng.android.test.sqlite.AppConstants;
import com.meng.android.test.sqlite.R;

public class ActionPopup extends PopupWindow {

	public interface OnClickItem {
		public void onClickItem(int position, ActionItem item);
	}

	// constants for action callback
	public static final int ACTION_ADD = 1;
	public static final int ACTION_EDIT = 2;
	public static final int ACTION_DELETE = 3;

	protected Context mContext;
	protected WindowManager mWindowManager;
	protected View mRootView;
	private LayoutInflater inflater;
	private int layoutId;
	private ListView mListView;
	private int layoutMenuItem;
	private List<ActionItem> mObject = new ArrayList<ActionItem>();
	private OnClickItem mCallback;
	private ListAdapter mAdapter;

	public ActionPopup(Context context) {
		this(context, null);
	}

	public ActionPopup(Context context, Integer resourceId) {
		super(context);
		this.mContext = context;
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (resourceId == null) {
			layoutId = R.layout.action_popup_main;
		} else {
			layoutId = resourceId;
		}
		layoutMenuItem = R.layout.custome_list_item_single_text_with_image;
		initComponent();
	}

	public void setOnClickItem(OnClickItem mCallback) {
		this.mCallback = mCallback;
	}

	public void addItem(ActionItem item) {
		mObject.add(item);
	}

	private void initComponent() {

		mRootView = (ViewGroup) inflater.inflate(layoutId, null);
		mListView = (ListView) mRootView.findViewById(R.id.popup_list_view);

		// set adapter for list view
		mAdapter = new ListAdapter(mContext, layoutMenuItem, mObject);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCallback != null) {
					mCallback.onClickItem(position, mAdapter.getItem(position));
				}

			}

		});

		// setting window popup
		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(mRootView);

		// add listener
		setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
	}

	public void show(View view) {
		int[] location = new int[2];

		view.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ view.getWidth(), location[1] + view.getHeight());

		// mRootView.measure(MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setWidth(mListView.getMeasuredWidth() + 10);
		setHeight(mListView.getMeasuredHeight() * mObject.size()
				+ mListView.getDividerHeight() * mObject.size() + 7);

		int rootHeight = getHeight();

		int xPos = location[0];
		int yPos = anchorRect.top - rootHeight;

		// display on bottom of view
		if (rootHeight > view.getTop()) {
			yPos = anchorRect.bottom;
		}

		Log.d(AppConstants.LOG_CAT, "popup width: " + getWidth());
		Log.d(AppConstants.LOG_CAT, "popup height: " + getHeight());

		showAtLocation(view, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private class ListAdapter extends ArrayAdapter<ActionItem> {

		private int resourceId;

		public ListAdapter(Context context, int resourceId,
				List<ActionItem> listItem) {
			super(context, resourceId, listItem);
			this.resourceId = resourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ActionItem item = getItem(position);
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(resourceId, parent, false);
				holder.image = (ImageView) convertView
						.findViewById(R.id.image_icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.list_item_title);
				// holder.description = (TextView) convertView
				// .findViewById(R.id.list_item_desc);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(item.title);
			// holder.description.setText(item.description);
			if (item.image > 0) {
				holder.image.setImageResource(item.image);
			}

			return convertView;
		}

	}

	private static class ViewHolder {
		public ImageView image;
		public TextView title;
		// public TextView description;
	}
}