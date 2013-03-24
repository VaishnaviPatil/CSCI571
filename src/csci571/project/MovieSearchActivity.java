package csci571.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MovieSearchActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
		
    public static EditText searchString;
	public static Spinner searchType;
	public static Button search;
	public static String urlString;
	public static URL url;
	public static StringBuilder sb;
	public static String[] titles, directors, ratings, years, images, links;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        searchString = (EditText)findViewById(R.id.editTextMovieName);
        searchType = (Spinner)findViewById(R.id.spinnerType);
        search = (Button)findViewById(R.id.buttonSearch);
        search.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.buttonSearch){
					
			if(searchString.getText().toString().equals(""))
			
				Toast.makeText(this, "No input given!", Toast.LENGTH_SHORT).show();
			else{
			
				try{
				
					// Building URL
					urlString = "http://cs-server.usc.edu:14528/hw8/hello?searchString=";
					urlString = urlString.concat(URLEncoder.encode(searchString.getText().toString(),"UTF-8")+"&dropDown=");
					String type = String.valueOf(searchType.getSelectedItem());
					
					if(type.equals("All Types")){
					
						urlString = urlString.concat("feature,tv_series,game");
					}
					else if(type.equals("Feature Film")){
					
						urlString = urlString.concat("feature");
					}
					else if(type.equals("TV Series")){
					
						urlString = urlString.concat("tv_series");
					}
					else if(type.equals("Video Game")){
					
						urlString = urlString.concat("game");
					}
					
					url = new URL(urlString);
				 
				 	// Fetching result
					InputStream urlStream = url.openStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(urlStream));
				
					sb = new StringBuilder(); 
					String newLine="";
	
					while ((newLine=in.readLine())!=null)
						sb.append(newLine);
	
					in.close();
					
					JSONObject myjson = new JSONObject(sb.toString());
					JSONObject results = myjson.getJSONObject("results");
					JSONArray resultsArray = results.getJSONArray("result");
					
					titles = new String[resultsArray.length()];
					images = new String[resultsArray.length()];
					directors = new String[resultsArray.length()];
					ratings = new String[resultsArray.length()];
					years = new String[resultsArray.length()];
					links = new String[resultsArray.length()];
					
					//No results found
					if(resultsArray.length() == 0){
					
						Toast.makeText(this, "no movies found!", Toast.LENGTH_SHORT).show();
					}
					else{
					
						for(int i=0; i<resultsArray.length(); i++){
						
							ratings[i] = resultsArray.getJSONObject(i).getString("rating");
							directors[i] = resultsArray.getJSONObject(i).getString("director");
							images[i] = resultsArray.getJSONObject(i).getString("cover");
							years[i] = resultsArray.getJSONObject(i).getString("year");
							titles[i] = resultsArray.getJSONObject(i).getString("title");
							links[i] = resultsArray.getJSONObject(i).getString("details");
			                Log.i("result ",resultsArray.getJSONObject(i).getString("title")+" "+i);
			            }
						
						Intent intent = new Intent(getApplicationContext(), ListMovies.class);
					    startActivity(intent);
					}
				}
				
		        catch (MalformedURLException e) {
		        
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				catch (IOException e) {
				
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				catch (JSONException e) {
				
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
