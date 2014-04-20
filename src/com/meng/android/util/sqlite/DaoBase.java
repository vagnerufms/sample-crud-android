package com.meng.android.util.sqlite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.meng.android.test.sqlite.AppConstants;

/**
 * this class using library ormlite-core-4.48.jar ormliste-android-4.48.jar
 */

public abstract class DaoBase<T, I> extends OrmLiteSqliteOpenHelper {

	private static final String LOG_CAT = AppConstants.LOG_CAT;;
	private static final String DATABASE_NAME = AppConstants.DATABASE_NAME;
	private static final int DATABASE_VERSION = AppConstants.DATABASE_VERSION;

	/**
	 * properties
	 */
	// private Context mContext;
	private Dao<T, I> simpleDao = null;
	private RuntimeExceptionDao<T, I> simpleRuntimeDao = null;
	private Class<T> clazz;

	public DaoBase(Context context, Class<T> clazz) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// mContext = context;
		this.clazz = clazz;
	}

	public T get(I id) {
		return getSimpleDataDao().queryForId(id);
	}

	public void save(T entity) {
		merge(entity);
	}

	public void merge(T person) {
		getSimpleDataDao().createOrUpdate(person);
	}

	public List<T> findByExample(T filter) {
		QueryBuilder<T, I> builder;
		try {
			builder = getDao().queryBuilder();
			PreparedQuery<T> entitySearch = builder.prepare();

			Log.d(LOG_CAT, "execute: " + builder.prepareStatementString());
			return getDao().query(entitySearch);
		} catch (SQLException e) {
			Log.e(LOG_CAT, "Error find by example", e);
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	public List<T> findAll() {
		List<T> list = new ArrayList<T>();
		try {
			list = getDao().queryForAll();
		} catch (SQLException e) {
			Log.e(LOG_CAT, "error find all", e);
		}
		return list;
	}

	public void deleteAll() {
		RuntimeExceptionDao<T, I> dao = getSimpleDataDao();
		List<T> list = dao.queryForAll();
		dao.delete(list);
	}

	public void delete(I id) {
		getSimpleDataDao().deleteById(id);
	}

	public void delete(List<I> listId) {
		for (I id : listId) {
			delete(id);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(LOG_CAT, "onCreate");
			TableUtils.createTable(connectionSource, clazz);
		} catch (SQLException e) {
			Log.e(LOG_CAT, "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(LOG_CAT, "onUpgrade");
			TableUtils.dropTable(connectionSource, clazz, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(LOG_CAT, "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public RuntimeExceptionDao<T, I> getSimpleDataDao() {
		if (simpleRuntimeDao == null) {
			simpleRuntimeDao = getRuntimeExceptionDao(clazz);
		}
		return simpleRuntimeDao;
	}

	public Dao<T, I> getDao() throws SQLException {
		if (simpleDao == null) {
			simpleDao = getDao(clazz);
		}
		return simpleDao;
	}

	public void close() {
		// getDao().
		simpleDao = null;
		simpleRuntimeDao = null;

	}

}