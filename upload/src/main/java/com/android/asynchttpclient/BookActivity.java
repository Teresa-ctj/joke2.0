package com.android.asynchttpclient;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * AsyncHttpClient 开发步骤:
 * 1.复制 butterknife / picasso / android-async-http-1.4.8 到 libs 后同步
 * 2.Book / BookAdapter / book_item.xml
 * 3.BookFragment / fragment_book.xml
 * 4.BookActivity / activity_book.xml
 * 5.访问网络加载数据:AndroidClient / getDataFromServer()
 */
public class BookActivity extends Activity {
    private ArrayList<Book> list;
    private BookFragment bookFragment;
    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        if (savedInstanceState == null) {
            list = new ArrayList<Book>();
            bookFragment = new BookFragment();

            getFragmentManager().beginTransaction().add(R.id.container, bookFragment)
                    .commit();
        }

        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    private void getDataFromServer() {
        AndroidClient.get("BookJsonServlet", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // 处理 JSON 对象
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray ja) {
                try {
                    // 处理 JSON 数组
                    list.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);

                        Book book = new Book();
                        book.setIsbn(jo.getString("isbn"));
                        book.setTitle(jo.getString("title"));
                        book.setImage(AndroidClient.BASE_URL + jo.getString("image"));
                        book.setAuthor(jo.getString("author"));
                        list.add(book);
                    }
                    Log.v("MainActivity", "..........." + list.size());
                    bookFragment.loadData(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // NetworkInfo info = cm.getActiveNetworkInfo();
                // if (info != null && info.isConnectedOrConnecting()) {

                getDataFromServer();

                // } else {
                // Toast.makeText(this, "网络不可用", Toast.LENGTH_LONG).show();
                // }
                break;
            case R.id.action_settings:
                break;
        }
        return true;
    }
}
