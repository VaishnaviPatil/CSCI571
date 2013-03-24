package csci571.project;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String> {

	private final Context context;
	private String[] images, titles, ratings;
	private static LayoutInflater inflater = null;
	
	public MyAdapter(Context context, String[] titles, String[] ratings,String[] images){
		
		super(context, 0);
		this.context = context;
		this.images = images;
		this.titles = titles;
		this.ratings = ratings;
		Log.i("MyAdapter","constructor"+getCount());
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}
	
	/*
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}*/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	
		// Populating a result in the List View
		Log.i("getView"," "+position);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View movieRow = inflater.inflate(R.layout.movie_record, null, true);
		
        TextView title = (TextView)movieRow.findViewById(R.id.textViewTitle);
        title.setText(Html.fromHtml(titles[position]));
        
        TextView rating = (TextView)movieRow.findViewById(R.id.textViewRating);
        rating.setText("Rating: "+ratings[position]);
                
        ImageView thumb_image = (ImageView)movieRow.findViewById(R.id.imageViewIcon);
        
        InputStream URLcontent;
		try {
		
			URLcontent = (InputStream) new URL(images[position]).getContent();
			Bitmap bmImg = BitmapFactory.decodeStream(URLcontent);
			thumb_image.setImageBitmap(bmImg);
		}
	
		catch (MalformedURLException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
	
		catch (IOException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return movieRow;
    }		
}
