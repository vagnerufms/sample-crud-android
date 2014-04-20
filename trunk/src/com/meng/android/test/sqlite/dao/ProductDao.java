package com.meng.android.test.sqlite.dao;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;
import com.meng.android.test.sqlite.AppConstants;
import com.meng.android.test.sqlite.domain.Product;
import com.meng.android.util.sqlite.DaoBase;

public class ProductDao extends DaoBase<Product, Long> {

	public ProductDao(Context context) {
		super(context, Product.class);
	}

	public boolean existsCode(String code) {
		Product entity = getByCode(code);
		return entity == null ? false : true;
	}

	public Product getByCode(String code) {
		try {
			QueryBuilder<Product, Long> builder = getSimpleDataDao()
					.queryBuilder();
			builder.where().eq(Product.F_CODE, code);
			Log.d(AppConstants.LOG_CAT,
					"Query: " + builder.prepareStatementString());
			List<Product> listFind = builder.query();
			if (listFind != null && !listFind.isEmpty()) {
				return listFind.get(0);
			} else {
				return null;
			}
		} catch (SQLException e) {
			Log.d(AppConstants.LOG_CAT, "error find by code ", e);
		}
		return null;
	}

}