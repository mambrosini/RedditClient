package com.example.maxi.redditclient.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.maxi.redditclient.R;
import com.example.maxi.redditclient.RoundedCornersImageView;
import com.example.maxi.redditclient.activities.ImageActivity;
import com.example.maxi.redditclient.model.Entry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Maxi on 17/9/2016.
 */
public class EntryArrayAdapter extends ArrayAdapter<Entry> {

    private Context context;

    public EntryArrayAdapter(Context context, ArrayList<Entry> entries) {
        super(context, 0, entries);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.post_list_item, null);
            holder = new ViewHolder();
            holder.position = position;
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.thumbnail = (RoundedCornersImageView) convertView.findViewById(R.id.image);
            holder.comments = (TextView) convertView.findViewById(R.id.numberOfComments);
            holder.container = (RelativeLayout) convertView.findViewById(R.id.entry_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Entry entry = getItem(position);

        holder.title.setText(entry.getTitle());
        holder.author.setText(entry.getAuthor());
        holder.date.setText(DateUtils.getRelativeTimeSpanString(entry.getDate().getTime(), new Date().getTime() ,DateUtils.MINUTE_IN_MILLIS));
        if (URLUtil.isHttpUrl(entry.getThumbUrl()) || URLUtil.isHttpsUrl(entry.getThumbUrl())) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            Picasso.with(context).load(entry.getThumbUrl()).into(holder.thumbnail);
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent imageIntent = new Intent(context, ImageActivity.class);
                    imageIntent.putExtra(ImageActivity.URL, entry.getImageUrl());
                    imageIntent.putExtra(ImageActivity.THUMB_URL, entry.getThumbUrl());
                    context.startActivity(imageIntent);
                }
            });

        } else {
            holder.thumbnail.setVisibility(View.GONE);
        }
        holder.comments.setText(String.valueOf(entry.getNumberOfComments()));

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getUrl()));
                context.startActivity(browserIntent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public int position;
        public TextView title;
        public TextView author;
        public TextView date;
        public RoundedCornersImageView thumbnail;
        public TextView comments;
        public RelativeLayout container;
    }

}