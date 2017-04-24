package dummy.ptixiaki;

//import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import dummy.ptixiaki.Pack;
//import dummy.ptixiaki.DispLocListener;


@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	//private static double startTime = (double) System.currentTimeMillis();
	//private static double stopTime = 0.0;
	//private float totalTime = 0.0f;
	//private long lastUpdate = 0;
	
	//public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	
	
	// -- GPS variables -- \\
	private LocationManager lm = null;
	private LocationListener locListenD = null;
	private Location myLoc;
	//private double myLatitude = 0;
	//private double myLongitude = 0;
	private float[] latLonValues = new float[2];
	
	public static final long locUpdateTime = 1000;
	public static final float locUpdateDistance = 0;
	public static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
	public Geocoder geocoder;
	public List<Address> addresses;
	
	public TextView tvLatitude;
	public TextView tvLongitude;
	public TextView tvAddress;
	public TextView tvCity;
	public TextView tvState;
	public TextView tvPostalCode;
	public TextView tvCountry;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// find the TextViews
		tvLatitude = (TextView)findViewById(R.id.tvLatitude);
		tvLongitude = (TextView)findViewById(R.id.tvLongitude);
		tvAddress = (TextView)findViewById(R.id.tvAddress);
		tvCity = (TextView)findViewById(R.id.tvCity);
		tvState = (TextView)findViewById(R.id.tvState);
		tvPostalCode = (TextView)findViewById(R.id.tvPostalCode);
		tvCountry = (TextView)findViewById(R.id.tvCountry);
		
		//flag = false;
		//count = 0;
		
		// 1. 
		// 1.a. Get handle for LocationManager
			lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
			// connect to the GPS location service
			myLoc = new Location(GPS_PROVIDER);
			
			// Get last known location from GPS
			if ( lm.getLastKnownLocation(GPS_PROVIDER) != null ) {
				myLoc.set(lm.getLastKnownLocation(GPS_PROVIDER));
			}
			
			locListenD = new DispLocListener();
			
			
			geocoder = new Geocoder(this, Locale.getDefault());
			
			
			
			/*
			TelephonyManager telm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			if (telm.getSimState() == TelephonyManager.SIM_STATE_READY) {
				System.out.println("Has SIM card ;) Send via Sms" + "\n");
			}
			else if (telm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
				System.out.println("Absent SIM. Sms unavailable" + "\n");
			}
			else if (telm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
				System.out.println("SIM status unknown." + "\n");
			}
			*/
			
	}
	
	
	// Resume location updates when we're resumed
	@Override
	public void onResume() {
		super.onResume();
		lm.requestLocationUpdates(GPS_PROVIDER, locUpdateTime, locUpdateDistance, locListenD);
	}
	
	
	// Turn off location updates if we're paused
	
	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(locListenD);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		*/
		
		super.onCreateOptionsMenu(menu);
		
		// -- Item: GPS enable
		// Create the menu item and keep a reference to it.
		@SuppressWarnings("unused")
		MenuItem menuItem = menu.add(0, R.id.action_enable_GPS, 0, R.string.GPS_settings);
		
		/*
		// -- SubMenu: Demo submenu
		SubMenu subMenu = menu.addSubMenu(1, R.id.action_app_demo, 1, R.string.app_demo);
		int subItemID0 = 0;
		int subItemID1 = 1;
		int subItemID2 = 2;
		int subItemID3 = 3;
		int subItemID4 = 4;
		// Create the submenu items and keep a reference to them.
		MenuItem submenuItem0 = subMenu.add(1, subItemID0, 0, R.string.Location_0);
		MenuItem submenuItem1 = subMenu.add(1, subItemID1, 1, R.string.Location_1);
		MenuItem submenuItem2 = subMenu.add(1, subItemID2, 2, R.string.Location_2);
		MenuItem submenuItem3 = subMenu.add(1, subItemID3, 3, R.string.Location_3);
		MenuItem submenuItem4 = subMenu.add(1, subItemID4, 4, R.string.Location_4);
		*/
		return true;
	}


	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		
		switch (item.getItemId()) {
		
			//case R.id.action_create_group:*/
				/** 
				 * Call SearchableActivity to enable my search.
				 * Get a contact/list of contacts in return.
				 * Arrange them into a new group.
				 * Give group a name and save it.
				 */
				
				
				/**
				 *  The search dialog is hidden, but appears at the top of the screen
				 *  when the user presses the Search button
				 *  (when onSearchRequested() is called).
				 **/
				//this.onSearchRequested();
		
			case R.id.action_enable_GPS:
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			    return true;
			    
			/*
			case R.id.action_app_demo:
				item.getSubMenu();
				return true;
			*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@SuppressLint("NewApi")
	public void onSendButton1Click(View view){
		
		if (!lm.isProviderEnabled(GPS_PROVIDER)){
			Toast.makeText(this, "Enable GPS (Settings)", Toast.LENGTH_LONG).show();
		}
		else{
			latLonValues[0] = (float) myLoc.getLatitude();
			latLonValues[1] = (float) myLoc.getLongitude();
			String sendSms = Pack.toString(latLonValues);
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){ //At least KitKat (API level 19)
				String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); //!-- Added in API Level 19 --!
				
				Intent sendSmsIntent = new Intent(Intent.ACTION_SEND);
		        sendSmsIntent.setType("text/plain");
		        sendSmsIntent.putExtra(Intent.EXTRA_TEXT, sendSms);

		        if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the user would be able to choose any app that support this intent.
		        {
		            sendSmsIntent.setPackage(defaultSmsPackageName);
		        }
		        startActivity(sendSmsIntent);
			}
			else{
				Intent sendSmsIntent = new Intent(Intent.ACTION_VIEW);
				sendSmsIntent.setType("vnd.android-dir/mms-sms");
				sendSmsIntent.putExtra("sms_body", sendSms);
				startActivity(sendSmsIntent);
				
				System.out.println("Resolve Activity: " + sendSmsIntent.resolveActivity(getPackageManager()) + "\n");
			}
			
			/*
			 * To avoid spending battery and Android resources for location updates all the time,
			 * we can use requestSingleUpdate(), instead of requestLocationUpdates()/removeUpdates().
			 * This way, the locationListener is called once, only when the user press the 'Send Via Sms' button
			 * 
			 lm.requestSingleUpdate(GPS_PROVIDER, locListenD, null);
			 if (myLoc.getLatitude() != 0 && myLoc.getLongitude() != 0) {
			 	latLonValues[0] = (float) myLoc.getLatitude();
			 	latLonValues[1] = (float) myLoc.getLongitude();
			 	String sendSms = Pack.toString(latLonValues);
			 }
			 */
		}
		
	}
	
	
	private class DispLocListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {

			if(location != null){
				myLoc.set(location);
				//myLatitude = location.getLatitude();
				//myLongitude = location.getLongitude();
				
				/*
				 * Akriveia 6 dekadikon psifion, se double metavlites...
				 * 
				 * String[] split_latitude = TextUtils.split(String.valueOf(location.getLatitude()), ".");
				 * String[] split_longitude = TextUtils.split(Double.toString(location.getLongitude()), ".");
				 * tvLatitude.setText(split_latitude[0] + "." + split_latitude[1].substring(0, 6));
				 * tvLongitude.setText(split_longitude[0] + "." + split_longitude[1].substring(0, 6));
				*/
				
				tvLatitude.setText(String.valueOf((float) location.getLatitude()));
				tvLongitude.setText(String.valueOf((float) location.getLongitude()));
				if(isDeviceOnline()){
					try {
						addresses = geocoder.getFromLocation(myLoc.getLatitude(), myLoc.getLongitude(), 1);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					tvAddress.setText(addresses.get(0).getAddressLine(0));
					tvCity.setText(addresses.get(0).getLocality());
					tvState.setText(addresses.get(0).getAdminArea());
					tvPostalCode.setText(addresses.get(0).getPostalCode());
					tvCountry.setText(addresses.get(0).getCountryName());
				}
			}
		}
		
		@Override
		public void onProviderDisabled(String provider) {
		}
		
		@Override
		public void onProviderEnabled(String provider) {
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	
	public boolean isDeviceOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return (netInfo != null && netInfo.isConnected());
	}
	
}
