package com.android.asynchttpclient;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

    private RequestQueue mQueue;
    private JsonArrayRequest jsonArrayRequest;

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
        // 1. 创建一个RequestQueue对象
        mQueue = Volley.newRequestQueue(this);

        // 2. 创建一个StringRequest/JSONObject/JSONArray对象
        // 构造方法(请求服务器的URL路径,响应成功时监听事件,响应失败时监听事件)
        jsonArrayRequest = new JsonArrayRequest(
                AndroidClient.getAbsoluteUrl("BookJsonServlet"),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ja) {
                        // 响应成功时处理返回的数据
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // 响应出错时处理的方法 (Toast)
            }
        });

        // 3. 将StringRequest对象添加到RequestQueue里面
        jsonArrayRequest.setTag(AndroidClient.VOLLEY_TAG);
        mQueue.add(jsonArrayRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // In your activity's onStop() method,
        // cancel all requests that have this tag.
        if (mQueue != null) {
            mQueue.cancelAll(AndroidClient.VOLLEY_TAG);
        }
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
