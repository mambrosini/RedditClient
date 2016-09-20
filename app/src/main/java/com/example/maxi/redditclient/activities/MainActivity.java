package com.example.maxi.redditclient.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.ListView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.maxi.redditclient.R;
import com.example.maxi.redditclient.adapters.EntryArrayAdapter;
import com.example.maxi.redditclient.dao.EntryDAO;
import com.example.maxi.redditclient.model.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Entry> entries;
    private ArrayList<Entry> downloadingEntries;
    private EntryArrayAdapter entryArrayAdapter;
    private ConnectionAsyncTask connectionAsyncTask;
    private ListView postListView;
    private ProgressDialog progressDialog;
    private MaterialRefreshLayout materialRefreshLayout;
    private static final int POST_LIMIT = 50;
    private static final String REDDIT_TOP_URL = "https://www.reddit.com/top/.json";
    private static final String REDDIT_LIMIT_FUNCTION = "?limit=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EntryDAO entryDAO = new EntryDAO(getApplicationContext());
        entryDAO.open();
        entries = entryDAO.getAllEntries();
        entryDAO.close();

        downloadingEntries = new ArrayList<>();

        postListView = (ListView) findViewById(R.id.post_list);

        entryArrayAdapter = new EntryArrayAdapter(this, entries);
        postListView.setAdapter(entryArrayAdapter);

        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                reloadList();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            }
        });

        if (entries.isEmpty()){
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.updating_top_entries));
            progressDialog.show();

            connectionAsyncTask = new ConnectionAsyncTask(getApplicationContext());
            connectionAsyncTask.execute();
        }

    }

    public class ConnectionAsyncTask extends AsyncTask<Void, String, Void> {
        Context mContext;

        public ConnectionAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(REDDIT_TOP_URL + REDDIT_LIMIT_FUNCTION + POST_LIMIT);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String stream = readStream(in);

                downloadingEntries.clear();

                if (stream != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(stream);
                        JSONObject data = jsonObj.getJSONObject("data");
                        JSONArray topics = data.getJSONArray("children");

                        for (int i = 0; i < POST_LIMIT; i++) {
                            JSONObject topic = topics.getJSONObject(i).getJSONObject("data");

                            String title = topic.getString("title");
                            String author = topic.getString("author");
                            String thumbnailUrl = topic.getString("thumbnail");
                            String postTime = topic.getString("created_utc");
                            int numComments = topic.getInt("num_comments");
                            String imageURL = null;
                            if (URLUtil.isHttpUrl(thumbnailUrl) || URLUtil.isHttpsUrl(thumbnailUrl)) {
                                try {
                                    imageURL = topic.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url");
                                } catch (JSONException ex) {
                                }
                            } else {
                                imageURL = "";
                            }
                            String entryUrl = topic.getString("url");

                            Entry entry = new Entry();
                            entry.setTitle(title);
                            entry.setAuthor(author);
                            entry.setThumbUrl(thumbnailUrl);
                            entry.setImageUrl(imageURL);
                            entry.setUrl(entryUrl);

                            Date time = new java.util.Date(Double.valueOf(postTime).longValue()*1000);
                            entry.setDate(time);

                            entry.setNumberOfComments(numComments);
                            downloadingEntries.add(entry);
                        }
                    } catch (JSONException e) {
                        handleDownloadError();
                    }
                } else {
                    handleDownloadError();
                }

            } catch (Exception e){
                handleDownloadError();
            }
            return null;
        }

        private void handleDownloadError(){
            entries.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.downloading_entries_error), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onProgressUpdate(String... item) {
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            materialRefreshLayout.finishRefresh();

            if (downloadingEntries.size() == 50) {
                entries.clear();
                entries.addAll(downloadingEntries);
                entryArrayAdapter.notifyDataSetInvalidated();
                EntryDAO entryDAO = new EntryDAO(getApplicationContext());
                entryDAO.open();
                entryDAO.deleteAllEntries(entryDAO.getAllEntries());
                for (Entry entry : entries) {
                    entry.setId(entryDAO.createEntry(entry).getId());
                }
                entryDAO.close();
            }

        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private void reloadList(){
        connectionAsyncTask = new ConnectionAsyncTask(getApplicationContext());
        connectionAsyncTask.execute();
    }
}
