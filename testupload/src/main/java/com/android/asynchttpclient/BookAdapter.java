package com.android.asynchttpclient;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BookAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Book> list;
    private LayoutInflater inflater;

    public BookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Book getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.book_item, null);
            // 通过黄油刀实例化控件
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Book book = list.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.isbn.setText(book.getIsbn());

        // 使用 Picasso 加载图片
        // Picasso.with(context).load(book.getImage()).into(holder.image);

        //1. 创建一个RequestQueue对象。
        RequestQueue mQueue = Volley.newRequestQueue(context);

        //2. 创建一个ImageLoader对象。
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        //3. 获取一个ImageListener对象。
        // 第一个参数指定用于显示图片的ImageView控件，
        // 第二个参数指定加载图片的过程中显示的图片，
        // 第三个参数指定加载图片失败的情况下显示的图片
        ImageLoader.ImageListener listener =
                ImageLoader.getImageListener(holder.image,
                R.drawable.empty_photo, R.mipmap.ic_launcher);
        //4. 调用ImageLoader的get()方法加载网络上的图片
        imageLoader.get(book.getImage(), listener);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.author)
        TextView author;
        @InjectView(R.id.isbn)
        TextView isbn;
        @InjectView(R.id.image)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
