package com.android.tmall.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.tmall.R;
import com.android.tmall.pojo.Product;
import com.android.tmall.util.TmallUtil;
import com.android.tmall.util.VolleyUtil;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ProductListFragment extends Fragment {
    private List<Product> productList;
    private RecyclerView.Adapter adapter;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_product_list, container, false);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(RecyclerView view) {
        loadProductListData();
    }

    // 访问网络加载所有的宝贝
    private void loadProductListData() {
        JsonArrayRequest request = new JsonArrayRequest(
                VolleyUtil.getAbsoluteUrl("ProductServletJson"),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray ja) {
                        try {
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                Log.v(TmallUtil.TAG, jo.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        TmallUtil.toast(getActivity(), R.string.net_error);
                    }
                }
        );

        VolleyUtil.getInstance(getActivity()).addToRequestQueue(request);
    }


}
