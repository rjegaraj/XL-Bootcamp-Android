package com.example.mytwitter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class TwitterFeedActivity extends ListActivity {

    private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyTask().execute();
    }
      private class MyTask extends AsyncTask<Void, Void, Void> {
              private ProgressDialog progressDialog;
              protected void onPreExecute() {
                      progressDialog = ProgressDialog.show(TwitterFeedActivity.this,
                                        "", "Loading. Please wait...", true);
              }
              @Override
              protected Void doInBackground(Void... arg0) {
                      try {
                              HttpClient hc = new DefaultHttpClient();
                              HttpGet get = new
                              HttpGet("http://search.twitter.com/search.json?q=%23bieber");
                              HttpResponse rp = hc.execute(get);
                              if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                              {
                                      String result = EntityUtils.toString(rp.getEntity());
                                      JSONObject root = new JSONObject(result);
                                      JSONArray sessions = root.getJSONArray("results");
                                      for (int i = 0; i < sessions.length(); i++) {
                                              JSONObject session = sessions.getJSONObject(i);
                                      Tweet tweet = new Tweet();
                                               tweet.content = session.getString("text");
                                               tweet.author = session.getString("from_user");
                                               tweet.creationdate = session.getString("created_at");
                                               tweet.profileimage = session.getString("profile_image_url");
                                               tweets.add(tweet);
                                      }
                             }
                     } catch (Exception e) {
                             Log.e("TwitterFeedActivity", "Error loading JSON", e);
                     }
                     return null;
        }
        @Override
        protected void onPostExecute(Void result) {
                progressDialog.dismiss();
                setListAdapter(new TweetListAdaptor(
                                TwitterFeedActivity.this, R.layout.list_item, tweets));
         }
}
    private class TweetListAdaptor extends ArrayAdapter<Tweet> {
            private ArrayList<Tweet> tweets;
            public TweetListAdaptor(Context context,
            int textViewResourceId,
            ArrayList<Tweet> items) {
            super(context, textViewResourceId, items);
            this.tweets = items;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                	DownloadImageTask downloadImageTask = new DownloadImageTask(v);
                    if (v == null) {
                            LayoutInflater vi = (LayoutInflater)
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.list_item, null);
                    }
                    Tweet finaltweet = tweets.get(position);
                    TextView tt = (TextView) v.findViewById(R.id.toptext);
                    TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                    //ImageView image = (ImageView) v.findViewById(R.id.profileimage);
                    new DownloadImageTask(v).execute(finaltweet.profileimage);
                    tt.setText(finaltweet.content);
                    bt.setText(finaltweet.author + "    " + finaltweet.creationdate);
                    return v;
            }
                

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    	DownloadImageTask(View v){
    		super();
    		x = v;
  
    	}
    	
    	View x;
    	
    	
        protected void onPreExecute() {  }
        @Override
        protected Bitmap doInBackground(String... arg0) {
        	String imageURLString = arg0[0];
                try {
                    String replaced = imageURLString.replaceAll("\\/", "/");
                    URL imageURL = new URL(replaced);

                    return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                } catch (Exception e) {
                }
                return null;
            
    }
    
  @Override
  protected void onPostExecute(Bitmap result) {
      ImageView image = (ImageView) x.findViewById(R.id.profileimage);
      image.setImageBitmap(result);                   
   }
  
}
    }}