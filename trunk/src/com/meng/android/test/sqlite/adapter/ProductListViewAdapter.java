package com.meng.android.test.sqlite.adapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.meng.android.test.sqlite.AppConstants;
import com.meng.android.test.sqlite.R;
import com.meng.android.test.sqlite.domain.Product;
import com.meng.android.util.StringUtils;

public class ProductListViewAdapter extends BaseAdapter {

	private static final String LOG_CAT = AppConstants.LOG_CAT;

	/**
	 * Interface using for callback when image on list menu item clicked. Can
	 * using to repaint action bar
	 */
	public interface OnClickListItemImage {
		public void onClickListItemImage(int position, Object obj);
	}

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayFilter mFilter;
	private final Object mLock = new Object();
	private List<Product> mOriginalValues;
	private List<Product> mObjects;
	private int menuItemResourceId;
	private int mFieldId;
	private OnClickListItemImage mCallback;
	private String keyword = "";

	public ProductListViewAdapter(Context context, int menuItemResourceId,
			int filterResourceId, List<Product> listProduct) {
		init(context, menuItemResourceId, filterResourceId, listProduct);
	}

	public ProductListViewAdapter(Context context, int menuItemResourceId,
			int filterResourceId, List<Product> listProduct,
			OnClickListItemImage mCallback) {
		init(context, menuItemResourceId, filterResourceId, listProduct);
		this.mCallback = mCallback;
	}

	private void init(Context context, int menuItemResourceId,
			int filterResourceId, List<Product> listProduct) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.menuItemResourceId = menuItemResourceId;
		mObjects = listProduct;
		mFieldId = filterResourceId;
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public Product getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	public List<Product> getListItem() {
		return mObjects;
	}

	public void setListItem(List<Product> listItem) {
		this.mObjects = listItem;
	}

	public int getCountChecked() {
		int count = 0;
		for (Product item : mObjects) {
			if (item.isSelected()) {
				count++;
			}
		}

		return count;
	}

	public List<Product> getCheckedItem() {
		List<Product> result = new ArrayList<Product>();
		for (Product item : mObjects) {
			if (item.isSelected()) {
				result.add(item);
			}
		}
		return result;
	}

	public boolean hasSelectedItem() {
		for (Product item : mObjects) {
			if (item.isSelected()) {
				return true;
			}
		}
		return false;
	}

	public List<Product> removeChecked() {
		List<Product> listRemove = new ArrayList<Product>();
		Iterator<Product> iterator = mObjects.iterator();
		while (iterator.hasNext()) {
			Product item = iterator.next();
			if (item.isSelected()) {
				iterator.remove();
				listRemove.add(item);
			}
		}
		if (!listRemove.isEmpty()) {
			notifyDataSetChanged();
		}
		return listRemove;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		Product selected = getItem(position);
		String code = selected.getCode();

		if (convertView == null) {
			convertView = mInflater.inflate(menuItemResourceId, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.image_icon);
			holder.title = (TextView) convertView
					.findViewById(R.id.list_item_title);
			holder.description = (TextView) convertView
					.findViewById(R.id.list_item_desc);

			// set image from product picture thumbnail
			holder.image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getItem(position).toogleSelected();
					notifyDataSetInvalidated();
					if (mCallback != null) {
						mCallback.onClickListItemImage(position,
								getItem(position));
					}
				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SpannableString text = new SpannableString(code);
		text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, code.length(), 0);
		if (StringUtils.isNotBlank(keyword)) {
			int index;
			if ((index = code.toLowerCase().indexOf(keyword)) != -1) {
				try {
					text.setSpan(new ForegroundColorSpan(Color.RED), index,
							index + keyword.length(), 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		holder.title.setText(text, BufferType.SPANNABLE);
		holder.description.setText(selected.getDescription());
		if (getItem(position).isSelected()) {
			holder.image.setImageResource(R.drawable.ic_action_accept);
		} else {
			try {
				// get file with file path
				final Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
						BitmapFactory.decodeFile(selected.getFile()), 40, 40);
				holder.image.setImageBitmap(ThumbImage);
			} catch (Exception e) {
				Log.e(LOG_CAT, "error:", e);
			}
		}

		return convertView;
	}

	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	private class ArrayFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();

			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<Product>(mObjects);
				}
			}

			if (prefix == null || prefix.length() == 0) {
				keyword = "";
				ArrayList<Product> list;
				synchronized (mLock) {
					list = new ArrayList<Product>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();
			} else {
				String prefixString = prefix.toString().toLowerCase();
				keyword = prefixString;

				ArrayList<Product> values;
				synchronized (mLock) {
					values = new ArrayList<Product>(mOriginalValues);
				}

				final int count = values.size();
				final ArrayList<Product> newValues = new ArrayList<Product>();

				for (int i = 0; i < count; i++) {
					final Product value = values.get(i);
					final String valueText = value.getCode().toLowerCase();

					// First match against the whole, non-splitted value
					if (valueText.contains(prefixString)) {
						newValues.add(value);
					}
				}

				results.values = newValues;
				results.count = newValues.size();
			}

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked
			mObjects = (List<Product>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}

	private static class ViewHolder {
		ImageView image;
		TextView title;
		TextView description;
	}

}