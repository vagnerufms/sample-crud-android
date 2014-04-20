package com.meng.android.test.sqlite;

import java.sql.SQLException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.meng.android.test.sqlite.dao.ProductDao;
import com.meng.android.test.sqlite.domain.Product;

public class MainActivity extends Activity {

	private ProductDao productDao;
	private Button mButtonProduct, mButtonCustomer, mButtonPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// initDatabase();
		setContentView(R.layout.main_activity);
		initComponents();
	}

	private void initComponents() {
		mButtonProduct = (Button) findViewById(R.id.action_btn_add_product);
		mButtonCustomer = (Button) findViewById(R.id.action_btn_add_customer);
		mButtonPos = (Button) findViewById(R.id.action_btn_add_pos);
		
		mButtonCustomer.setVisibility(View.INVISIBLE);
		mButtonPos.setVisibility(View.INVISIBLE);

		// add listener
		mButtonProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ProductActivity.class);
				startActivity(intent);
			}
		});
		mButtonCustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		mButtonPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	public void buttonClick(View view) {

	}

	private ProductDao getProductDao() {
		if (productDao == null) {
			productDao = new ProductDao(getApplicationContext());
		}
		return productDao;
	}

	private void initDatabase() {
		getProductDao();
		String[] alphabet = new String[] { "A", "B", "C", "D", "E", "F", "G",
				"H", "I", "J", "K", "K", "L", "M", "N", "O", "P", "Q", "R",
				"S", "T", "U", "V", "W", "X", "Y", "Z" };
		for (int i = 0; i < alphabet.length; i++) {
			try {
				String code = String.format("%s00", alphabet[i]);
				Product product = new Product();
				product.setCode(code);
				product.setDescription("description of " + code);
				productDao.save(product);

				code = String.format("%s01", alphabet[i]);
				Product product2 = new Product();
				product2.setCode(code);
				product2.setDescription("description of " + code);
				productDao.save(product2);

				code = String.format("%s02", alphabet[i]);
				Product product3 = new Product();
				product3.setCode(code);
				product3.setDescription("description of " + code);
				productDao.save(product3);

			} catch (Exception e) {
				Log.i(AppConstants.LOG_CAT, "error :" + e.getMessage());
			}
		}

		Log.d(AppConstants.LOG_CAT, "Find By ALL #1");
		List<Product> listProduct = productDao.findAll();
		Log.d(AppConstants.LOG_CAT, "Find All");
		for (Product row : listProduct) {
			Log.d(AppConstants.LOG_CAT, row.toString());
		}

		Log.d(AppConstants.LOG_CAT, "Find By ALL #2");
		List<Product> listProduct2 = productDao.findAll();
		Log.d(AppConstants.LOG_CAT, "Find All");
		for (Product row : listProduct2) {
			Log.d(AppConstants.LOG_CAT, row.toString());
		}

		Log.d(AppConstants.LOG_CAT, "Find By Id");
		Product testFind = productDao.get(1l);
		Log.d(AppConstants.LOG_CAT, "Result find by id: " + testFind);

		Log.d(AppConstants.LOG_CAT, "Find By Example");
		try {
			QueryBuilder<Product, Long> builder = productDao.getDao()
					.queryBuilder();
			builder.where().like("code", "%A1%");
			Log.d(AppConstants.LOG_CAT,
					"Query: " + builder.prepareStatementString());
			List<Product> listFind = builder.query();
			for (Product row : listFind) {
				Log.d(AppConstants.LOG_CAT, row.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (productDao != null) {
			OpenHelperManager.releaseHelper();
			productDao = null;
		}
	}

}