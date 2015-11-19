package com.android.asynchttpclient;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class BookFragment extends Fragment {
    @InjectView(R.id.listView)
    ListView listView;
    private BookAdapter adapter;
    private ArrayList<Book> data;

    public BookFragment() {
        // Required empty public constructor
    }

    public void loadData(ArrayList<Book> list) {
        data = list;
        adapter = new BookAdapter(getActivity(), data);
        listView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


}
