package com.meng.android.util;

public interface ObserverTask {
    public void doInBackground(Object resultObject);
    public void onPostExecute();
}