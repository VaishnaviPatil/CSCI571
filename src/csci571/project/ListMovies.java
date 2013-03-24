package csci571.project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ListMovies extends ListActivity implements OnItemClickListener{

	static int pos;
	
	// Facebook API integration
	Facebook facebook = new Facebook("456042924453856");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    
	public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list);
        
        MyAdapter adapter = new MyAdapter(this, MovieSearchActivity.titles, MovieSearchActivity.ratings, MovieSearchActivity.images);
        this.setListAdapter(adapter);
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(this);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

		// TODO Auto-generated method stub
		showDialog(position);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		pos = id;
		
		Dialog dialog;
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.details_dialog);
        dialog.setTitle("Details");
        
		// Extracting views 
        ImageView cover = (ImageView)dialog.findViewById(R.id.imageView);
        TextView name = (TextView)dialog.findViewById(R.id.textViewName);
        TextView year = (TextView)dialog.findViewById(R.id.textViewYear);
        TextView director = (TextView)dialog.findViewById(R.id.textViewDirector);
        TextView rating = (TextView)dialog.findViewById(R.id.textViewRating);
        Button postToFB = (Button)dialog.findViewById(R.id.buttonPost);
        
        postToFB.setOnClickListener(new OnClickListener(){
        	
        	@Override
			public void onClick(View arg0) {
        		
        		dismissDialog(pos);
        		if(!facebook.isSessionValid()) {
        		
        			Log.i("Facebook login","logging in!");
					// TODO Auto-generated method stub
	        		facebook.authorize(ListMovies.this, new DialogListener() {
	                    @Override
	                    public void onComplete(Bundle values) {
	                    	functionToPost();
	                    
	                    }
	                    @Override
	                    public void onFacebookError(FacebookError error) {}
	
	                    @Override
	                    public void onError(DialogError e) {}
	
	                    @Override
	                    public void onCancel() {}
	                });
        		}
        		else{
        			Log.i("Facebook login","logged in already!");
        			functionToPost();
        		}
        	}
        });
        
        // Displaying details of the selected movie, populating views
        name.setText("Name: "+Html.fromHtml(MovieSearchActivity.titles[id]));
		year.setText("Year: "+MovieSearchActivity.years[id]);
		director.setText("Director: "+Html.fromHtml(MovieSearchActivity.directors[id]));
		rating.setText("Rating: "+MovieSearchActivity.ratings[id]+"/10");
		
		InputStream URLcontent;
		try {
		
			URLcontent = (InputStream) new URL(MovieSearchActivity.images[id]).getContent();
			Bitmap bmImg = BitmapFactory.decodeStream(URLcontent);
			cover.setImageBitmap(bmImg);
		}
		
		catch (MalformedURLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		catch (IOException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		dialog.show();
		return dialog;        
	}
	
	
	public void functionToPost(){

		Bundle params = new Bundle();
		
		params.putString("method", "feed");
		params.putString("link", MovieSearchActivity.links[pos]);
		params.putString("picture", MovieSearchActivity.images[pos]);
		params.putString("name", MovieSearchActivity.titles[pos]);
		params.putString("caption", "I am interested in this movie/series/game");
		params.putString("description", MovieSearchActivity.titles[pos]+" released in "+MovieSearchActivity.years[pos]+" has a rating of "+MovieSearchActivity.ratings[pos]);
		params.putString("properties","{\"Look at user reviews\":{\"text\":\"here\", \"href\":\""+MovieSearchActivity.links[pos]+"/reviews\"}}");
		Log.i("params",params.toString());
		
		facebook.dialog(ListMovies.this, "feed", params, new DialogListener() {
		
            @Override
            public void onComplete(Bundle values) {
                final String postId = values.getString("post_id");
                if (postId != null) {
                    mAsyncRunner.request(postId, new RequestListener() {

						@Override
						public void onComplete(String response,Object state) {
						
							ListMovies.this.runOnUiThread(new Runnable() {
								  public void run() {
									  Toast.makeText(ListMovies.this, "successfully posted!", Toast.LENGTH_SHORT).show();
								  }
								});
							
						}

						@Override
						public void onIOException(IOException e,Object state) {}

						@Override
						public void onFileNotFoundException(FileNotFoundException e, Object state) {}

						@Override
						public void onMalformedURLException(MalformedURLException e, Object state) {}
						
						@Override
						public void onFacebookError(FacebookError e,Object state) {}
                    	
                    });
                    
                } 
                else {
                    Log.d("Facebook-Example", "No wall post made");
                }
            }
        
            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}

		});
		
	}
	

	// Menu option to logout from Facebook	
  	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	Log.i("Menu","item add");
   	 	menu.add(Menu.NONE, 1, Menu.NONE,"log out!");
   	 	return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    		  mAsyncRunner.logout(getApplicationContext(), new RequestListener() {
    		  @Override
    		  public void onComplete(String response, Object state) {
    			  ListMovies.this.runOnUiThread(new Runnable() {
					  public void run() {
						  Toast.makeText(ListMovies.this, "successfully logged out!", Toast.LENGTH_SHORT).show();
					  }
					});
				}
    		  
    		  @Override
    		  public void onIOException(IOException e, Object state) {}
    		  
    		  @Override
    		  public void onFileNotFoundException(FileNotFoundException e,
    		        Object state) {}
    		  
    		  @Override
    		  public void onMalformedURLException(MalformedURLException e,
    		        Object state) {}
    		  
    		  @Override
    		  public void onFacebookError(FacebookError e, Object state) {}
    		});

    	return (super.onOptionsItemSelected(item));
    }

}
