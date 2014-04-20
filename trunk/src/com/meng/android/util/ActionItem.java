package com.meng.android.util;

public class ActionItem {
	public int id = -1;
	public String title;
	public String description;
	public int image;

	public ActionItem(int id, String title) {
		this(id, title, null, -1);
	}

	public ActionItem(int id, String title, String description, int image) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.image = image;
	}
}