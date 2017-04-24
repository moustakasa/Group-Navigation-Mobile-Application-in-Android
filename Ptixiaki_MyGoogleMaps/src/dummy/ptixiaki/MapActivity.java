package dummy.ptixiaki;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
	
	private float sender_latitude = 0;
	private float sender_longitude = 0;
	private String sender_ID = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        Intent mapIntent = getIntent();
        sender_latitude = Float.parseFloat(mapIntent.getStringExtra("lat"));
        sender_longitude = Float.parseFloat(mapIntent.getStringExtra("lon"));
        sender_ID = mapIntent.getStringExtra("sender_ID");
        
        contactsLookUp();
        
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    
	@Override
    public void onMapReady(GoogleMap map) {
		
		map.setMyLocationEnabled(true);
		
        // Add a marker in destination, and move the camera.
        LatLng destination = new LatLng(sender_latitude, sender_longitude);
        map.addMarker(new MarkerOptions().position(destination).title(sender_ID));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 17));
    }
	
	
	private void contactsLookUp() {
    	// Check if sender_ID is in your Contacts List.
    	// If so, then his contact name is displayed.
    	// Otherwise, only his phone number is displayed.
    	Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender_ID));
    	
    	ContentResolver contentResolver = getContentResolver();
    	
    	Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
    	
    	try {
    		if (contactLookup != null && contactLookup.getCount() > 0) {
    			contactLookup.moveToNext();
    			 sender_ID = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
    	        //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
    		}
    	} finally {
    		if (contactLookup != null) {
    			contactLookup.close();
    		}
    	}
		
	}
	
}