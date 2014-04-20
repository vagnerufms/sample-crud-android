package com.meng.android.test.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meng.android.test.sqlite.dao.ProductDao;
import com.meng.android.test.sqlite.domain.Product;
import com.meng.android.util.Alert;
import com.meng.android.util.NumberUtils;
import com.meng.android.util.StringUtils;

public class ProductForm extends Activity implements OnClickListener {

	private static final String LOG_CAT = AppConstants.LOG_CAT;
	public static final String FORM_BEAN = "FORM_BEAN_PASSING";

	private Long idFormBean;
	private ProductDao productDao;
	private TextView mCode, mDesc, mPrice, mQuantity;
	private Product mFormBean = new Product();
	private Uri fileUri;
	private ImageView mImageView;
	private Drawable mImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(AppConstants.LOG_CAT, "start onCreate ProductForm");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_form);
		initComponent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.product_form, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			if (save()) {
				Alert.information(getApplicationContext(),
						R.string.message_success_save, "Product");
				finish();
			}
			return true;
		case R.id.action_cancel:
			finish();
			return true;
		case R.id.action_browse_file:
			getImagefromGallery();
			break;
		case R.id.action_camera:
			getImageFromCamera();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConstants.RESULT_SUCCESS_FROM_CAMERA) {
				if (resultCode == RESULT_OK) {
					previewCapturedImage();
				}
			} else if (requestCode == AppConstants.RESULT_SUCCESS_FROM_BROWSE_FILE) {
				fileUri = data.getData();
				try {
					InputStream stream = getContentResolver().openInputStream(
							data.getData());
					stream.close();
					final Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
							BitmapFactory.decodeStream(stream), 120, 120);
					mImage = new BitmapDrawable(getResources(), ThumbImage);
					mImageView.setImageDrawable(mImage);
				} catch (FileNotFoundException e) {
					Log.e(LOG_CAT, "error:", e);
				} catch (IOException e) {
					Log.e(LOG_CAT, "error:", e);
				}
			}
		} else if (resultCode == RESULT_CANCELED) {
			Alert.information(getApplicationContext(), "User cancelled image");
		} else {
			Alert.information(getApplicationContext(),
					"Sorry! Failed to get image");
		}

	}

	private String getRealPathFromURI(Uri contentUri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, // Which
																		// columns
																		// to
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ic_action_file_selector:
			getImagefromGallery();
			break;
		case R.id.ic_action_camera:
			getImageFromCamera();
			break;
		}
	}

	private void previewCapturedImage() {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
					options);
			final Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
					BitmapFactory.decodeFile(fileUri.getPath()), 120, 120);
			mImage = new BitmapDrawable(getResources(), ThumbImage);
			mImageView.setImageDrawable(mImage);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void initComponent() {
		Log.d(AppConstants.LOG_CAT, "idFormBean: " + idFormBean);
		productDao = new ProductDao(getApplicationContext());

		// action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mCode = (TextView) findViewById(R.id.product_form_code);
		mDesc = (TextView) findViewById(R.id.product_form_description);
		mPrice = (TextView) findViewById(R.id.product_form_price);
		mQuantity = (TextView) findViewById(R.id.product_form_quantity);
		mImageView = (ImageView) findViewById(R.id.image_preview);

		Button mButtonBrowse = (Button) findViewById(R.id.ic_action_file_selector);
		Button mButtonCamera = (Button) findViewById(R.id.ic_action_camera);
		mButtonBrowse.setOnClickListener(this);
		mButtonBrowse.setVisibility(View.INVISIBLE);
		mButtonCamera.setOnClickListener(this);
		mButtonCamera.setVisibility(View.INVISIBLE);

		idFormBean = getIntent().getExtras().getLong(FORM_BEAN);
		// check is update or add
		if (idFormBean != null && idFormBean > 0) {
			mFormBean = productDao.get(idFormBean);
			entityToBean();
			mCode.setEnabled(false);
		}
	}

	protected void beanToEntity() {
		mFormBean.setCode(mCode.getText().toString());
		mFormBean.setDescription(mDesc.getText().toString());
		mFormBean.setPrice(NumberUtils.bigDecimalValueOf(mPrice.getText()
				.toString()));
		mFormBean.setStock(NumberUtils.doubleValueOf(mQuantity.getText()
				.toString()));
		if (fileUri != null && StringUtils.isNotBlank(fileUri.getPath())) {
			mFormBean.setFile(fileUri.getPath());
		}
	}

	protected void entityToBean() {
		mCode.setText(mFormBean.getCode());
		mDesc.setText(mFormBean.getDescription());
		mPrice.setText(mFormBean.getPrice().toPlainString());
		mQuantity.setText(mFormBean.getStock().toString());
		Log.d(LOG_CAT, "file: " + mFormBean.getFile());
		try {
			fileUri = Uri.parse(mFormBean.getFile());
		} catch (Exception e) {
			Log.e(LOG_CAT, "Error: ", e);
		}
		previewCapturedImage();
	}

	private boolean validate() {
		beanToEntity();
		Log.d(AppConstants.LOG_CAT, "mFormBean.getId(): " + mFormBean.getId());
		if (StringUtils.isBlank(mFormBean.getCode())) {

			Alert.information(
					getApplicationContext(),
					String.format(
							getResources().getString(
									R.string.message_error_empty), "Code"));
			return false;
		}
		if (mFormBean.getId() == null) {
			if (productDao.existsCode(mFormBean.getCode())) {
				Alert.information(getApplicationContext(), String.format(
						getResources().getString(
								R.string.message_error_already_exists), "Code"));
				return false;
			}
		}
		if (StringUtils.isBlank(mFormBean.getDescription())) {
			Alert.information(getApplicationContext(), String.format(
					getResources().getString(R.string.message_error_empty),
					"Description"));
			return false;
		}
		return true;
	}

	private boolean save() {
		if (validate()) {
			Log.d(AppConstants.LOG_CAT,
					"Entity to save: " + mFormBean.toString());
			productDao.save(mFormBean);
			return true;
		}
		return false;
	}

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// if (productDao != null) {
	// OpenHelperManager.releaseHelper();
	// productDao = null;
	// }
	// }

	public void getImagefromGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				AppConstants.RESULT_SUCCESS_FROM_BROWSE_FILE);
	}

	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = Uri
				.fromFile(getOutputMediaFile(AppConstants.MEDIA_TYPE_IMAGE_JPG));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, AppConstants.RESULT_SUCCESS_FROM_CAMERA);
	}

	private static File getOutputMediaFile(int type) {
		// create instance file dir
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				AppConstants.IMAGE_DIRECTORY_NAME);
		// create firectory if not exists directory
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(AppConstants.IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ AppConstants.IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == AppConstants.MEDIA_TYPE_IMAGE_JPG) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}
}