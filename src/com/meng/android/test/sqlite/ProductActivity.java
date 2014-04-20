package com.meng.android.test.sqlite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Filter.FilterListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.meng.android.test.sqlite.adapter.ProductListViewAdapter;
import com.meng.android.test.sqlite.adapter.ProductListViewAdapter.OnClickListItemImage;
import com.meng.android.test.sqlite.dao.ProductDao;
import com.meng.android.test.sqlite.domain.Product;
import com.meng.android.util.ActionItem;
import com.meng.android.util.ActionPopup;
import com.meng.android.util.ActionPopup.OnClickItem;
import com.meng.android.util.Alert;
import com.meng.android.util.CollectionUtils;

public class ProductActivity extends Activity implements OnItemClickListener,
		SearchView.OnQueryTextListener, SearchView.OnCloseListener,
		OnClickListItemImage, OnItemLongClickListener {

	private static final String LOG_CAT = AppConstants.LOG_CAT;
	public static final int LIST_NOT_SELECTED = 0;
	public static final int LIST_SELECTED = 1;

	private int selectedItem = -1;
	private ProductListViewAdapter adapter;
	private ListView mListView;
	private ProductDao productDao;
	private int mStatus = LIST_NOT_SELECTED;
	private ActionPopup mPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(AppConstants.LOG_CAT, "start onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_main);
		initComponent();
	}

	@Override
	public void onClickListItemImage(int position, Object obj) {
		mStatus = adapter.hasSelectedItem() ? LIST_SELECTED : LIST_NOT_SELECTED;
		invalidateOptionsMenu();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		selectedItem = position;
		mPopup.show(view);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.product_menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		if (mStatus == LIST_SELECTED) {
			menu.removeItem(R.id.action_search);
			menu.removeItem(R.id.action_add);
		} else if (mStatus == LIST_NOT_SELECTED) {
			menu.removeItem(R.id.action_delete);
		}
		return true;
	}

	@Override
	public boolean onClose() {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		searchOnList(query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		searchOnList(newText);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		showAddForm((Product) parent.getItemAtPosition(position));
	}

	private void showAddForm(Product product) {
		Intent intent = new Intent(this, ProductForm.class);
		Bundle extras = new Bundle();
		extras.putLong(ProductForm.FORM_BEAN, product != null ? product.getId()
				: 0l);
		intent.putExtras(extras);
		startActivity(intent);
	}

	private void shortcutNavigation(String prefix) {
		int i = 0;
		for (Product item : adapter.getListItem()) {
			if (item.getCode().toLowerCase().startsWith(prefix.toLowerCase())) {
				mListView.smoothScrollToPosition(i);
				return;
			}
			i++;
		}
	}

	private void deleteChecked() {
		int countChecked = adapter.getCountChecked();
		new AlertDialog.Builder(this)
				.setTitle("Delete entry")
				.setMessage(
						String.format(
								"Are you sure you want to delete %d entry?",
								countChecked))
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								adapter.removeChecked();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			deleteChecked();
			return true;
		case R.id.action_add:
			showAddForm(null);
			return true;
		case R.id.action_refresh:
			refreshListView();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshListView() {
		adapter.setListItem(populateProduct());
		adapter.notifyDataSetInvalidated();
	}

	private void initComponent() {
		productDao = new ProductDao(getApplicationContext());

		// init component
		mListView = (ListView) findViewById(R.id.list_view_result);

		// populate data
		adapter = new ProductListViewAdapter(getApplicationContext(),
				R.layout.custome_list_item_with_image, R.id.list_item_title,
				populateProduct(), this);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// popup window
		mPopup = new ActionPopup(getApplicationContext());
		mPopup.addItem(new ActionItem(ActionPopup.ACTION_DELETE, "Delete Item"));
		mPopup.addItem(new ActionItem(ActionPopup.ACTION_EDIT, "Edit Item"));
		mPopup.setOnClickItem(new OnClickItem() {

			@Override
			public void onClickItem(int position, ActionItem item) {
				switch (item.id) {
				case ActionPopup.ACTION_DELETE:
					productDao.delete(adapter.getItemId(selectedItem));
					Alert.information(getApplicationContext(), "delete item: ");
					break;
				case ActionPopup.ACTION_EDIT:
					showAddForm(adapter.getItem(selectedItem));
					Alert.information(getApplicationContext(), "Edit item:");
					break;
				}
			}
		});

		// init navigation shorcut
		LinearLayout mLayout = (LinearLayout) findViewById(R.id.shorcut_navigation);
		Resources r = getResources();
		String[] alphabet = r.getStringArray(R.array.array_alphabet);
		for (int i = 0; i < alphabet.length; i++) {
			final String prefix = alphabet[i];
			TextView mTextView = new TextView(getApplicationContext());
			mTextView.setText(prefix);
			mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			mTextView.setBackgroundColor(Color.GRAY);
			mTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					shortcutNavigation(prefix);
				}
			});
			mLayout.addView(mTextView);
		}
	}

	private void searchOnList(String filter) {
		adapter.getFilter().filter(filter, new FilterListener() {

			@Override
			public void onFilterComplete(int count) {
				int count_ = adapter.getCount();
				if (count_ < 1) {
					Log.d(AppConstants.LOG_CAT, "list item empty");
				}
			}
		});
	}

	private List<Product> populateProduct() {
		Log.d(AppConstants.LOG_CAT, "+ populate product ");
		List<Product> listProduct = productDao.findAll();
		if (CollectionUtils.isNotEmpty(listProduct)) {
			Log.d(AppConstants.LOG_CAT, "Total product: " + listProduct.size());
			Collections.sort(listProduct, new Comparator<Product>() {

				@Override
				public int compare(Product obj1, Product obj2) {
					return obj1.getCode().compareTo(obj2.getCode());
				}

			});
			return listProduct;
		}
		return new ArrayList<Product>();
	}

}