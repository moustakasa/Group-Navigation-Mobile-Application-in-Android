package dummy.ptixiaki;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import dummy.ptixiaki.Dots;
import dummy.ptixiaki.DotView;
//import dummy.ptixiaki.MainActivity.DispLocListener;

/**
 * DisplayActivity called from ReceiveSmsActivity.
 * 
 * This is the activity that displays UI results.
 * It receives all the necessary info about the sender's location/position/orientation.
 * It displays a minimap with the distance between the sender and the receiver-s.
 * 
 * @author taz
 *
 */


public class DisplayActivity extends Activity {
	
	// Get current time, to calculate Ät (endTime - startTime)
	//private float endTime = (float) (SystemClock.elapsedRealtime() * Math.pow(10, -6)); // System.currentTimeMillis()
	
	//Display display = getWindowManager().getDefaultDisplay();
	//Point size = new Point();
	//display.getSize(size);
	//float width = size.x;
	//float height = size.y;
	
	private LinearLayout ll;
	private String sender_ID = null;

	private float width;	// device's screen width
	private float height;	// device's screen height
	public TextView tvLatitude;
	//public TextView tvLongitude;
	public TextView tvSenderID;
	public TextView tvDistance;
	

	// -- GPS variables
	private LocationManager lm = null;
	private int lm_count = 0;
	private LocationListener locListenD = null;
	private Location myLoc;
	private Location sender_loc;
	float distance_in_meters;

		// sender_orient_values[3] in Radians..
	
	// -- Sensors variables
	private SensorManager sensorManager;
	private Sensor accelerometer;
			//XXX
				//private Sensor magneticField;
			//XXX
	private SensorEventListener myAccelerometerListener;
			//XXX
				//private SensorEventListener myMagneticFieldListener;
			//XXX
	private boolean sensor_enabled = false;
	// Counter used for sensor updates every 10th time (Look at MySensorEventListener)
	private int sensors_counter = 0;
	
	// -- DotView variables
	private final int DOT_RADIAN = 6;
	private final int DOT_DIAMETER = DOT_RADIAN * 2;
	
	public static float x_middle;
	public static float y_middle;
	public static float screen_radian;
	
	public static final Dots dotModel = new Dots();
	public static DotView dotView;
	private float bearing = 0;
	public static float arrowRotate;
	private double sensor_difference = 0;
	
	
	
	// Receiver's Orientation ( Accelerometer + MagneticField ) values
	private float[] accelerometerValues = new float[3];
	private float[] magneticFieldValues = new float[3];
	//private float[] my_orient_results = new float[3];
	//private float[] RotationMatrix = new float[9];
	//private float azimuth;
	//private float pitch;
	//private float roll;
	
	

	// Sender's Orientation values
	private float[] sender_orient_results = new float[5]; // Azimuth, Pitch, Roll, Latitude, Longitude, Bearing
	//private float[] sender_latLonValues = new float[2];
	

	
	@SuppressLint({ "DefaultLocale", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_display);
		
		// 0.
		// Get handle for Dotview
			// Calculate display dimensions
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			width = metrics.widthPixels;
			height = metrics.heightPixels;
			
			x_middle = width / 2;
			y_middle = height / 2;
			screen_radian = Math.min(x_middle, y_middle) - DOT_RADIAN;
			
			tvSenderID = (TextView) findViewById(R.id.senderID);
			
			tvDistance = (TextView) findViewById(R.id.lblDistance);
			
			//dotView = new DotView(this, dotModel); //TODO 1. Move this in makeDot !!!!!!!!!!
			
			
			ll = (LinearLayout) findViewById(R.id.dotView);
			//ll.addView(dotView, 0); //TODO 2. Move this in makeDot() !!!!!!!!!!
			
			//ll.addView(tvDistance);
			//ll.addView(tvDistance, (int) width/2, (int) (height/2 - (width/2 + 20)));
			//lista.add(tvDistance);
			//dotView.addChildrenForAccessibility(lista);
			//dotView.addTouchables(lista);
			//ll.layout(0, 20, 0, 0);
			//ll.addView(dotView, (int) width, (int) height - 30);
			//ll.addView(dotView, (int) height - 30);
			
			
			
		// 1. 
			// 1.a.
			// Get handle for LocationManager
				lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
				myLoc = new Location(LocationManager.GPS_PROVIDER);
				// connect to the GPS location service
				if ( lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ) {
					myLoc.set(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)); //TODO Move it to: onSendButton1Click(View view)
				}
				
				//Toast.makeText(this, "GPS auto-enabled", Toast.LENGTH_SHORT).show();
				
				locListenD = new DispLocListener();
				
				//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locListenD);

			//
			// 1.b.
			// Get handle for SensorManager
				sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				
				// Connect to the Accelerometer sensor
				accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				
				// Connect to the Magnetic Field sensor
				//XXX
					//magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				//XXX
				
				// Monitor changes in the Sensors
						//myAccelerometerListener = new MySensorEventListener();
						//myMagneticFieldListener = new MySensorEventListener();	
				myAccelerometerListener = new SensorEventListener() {
					public void onSensorChanged(SensorEvent sensorEvent) {
						if(sensors_counter++ % 10 == 0) {
							if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
								accelerometerValues = sensorEvent.values;
								//XXX
								/*
								SensorManager.getRotationMatrix(RotationMatrix, null, accelerometerValues, magneticFieldValues);
								SensorManager.getOrientation(RotationMatrix, my_orient_results);
								
								// Convert from radians to degrees			   				 Orientation values
								my_orient_results[0] = (float) Math.toDegrees(my_orient_results[0]); // azimuth 
								my_orient_results[1] = (float) Math.toDegrees(my_orient_results[1]); // pitch
								my_orient_results[2] = (float) Math.toDegrees(my_orient_results[2]); // roll
								*/
								//XXX
								// Update dotView, with the new results
								makeDot_Update(dotModel, dotView, Color.RED);
									//makeDot(dotModel, dotView, Color.RED);
							}
						}
					}
					public void onAccuracyChanged(Sensor sensor, int accuracy) {}
				};
				
				//XXX
				/*
				myMagneticFieldListener = new SensorEventListener() {
					public void onSensorChanged(SensorEvent sensorEvent) {
						if(sensors_counter++ % 10 == 0) {
							if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
								magneticFieldValues = sensorEvent.values;
							
								XXX
								SensorManager.getRotationMatrix(RotationMatrix, null, accelerometerValues, magneticFieldValues);
								SensorManager.getOrientation(RotationMatrix, my_orient_results);
								
								// Convert from radians to degrees			   				 Orientation values
								my_orient_results[0] = (float) Math.toDegrees(my_orient_results[0]); // azimuth 
								my_orient_results[1] = (float) Math.toDegrees(my_orient_results[1]); // pitch
								my_orient_results[2] = (float) Math.toDegrees(my_orient_results[2]); // roll
								XXX
								
								// Update dotView, with the new results
								makeDot(dotModel, dotView, Color.RED);
							}
						}
					}
					public void onAccuracyChanged(Sensor sensor, int accuracy) {}
				};
				*/
				//XXX
				
				//sensorManager.registerListener(myAccelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
				//XXX
						//sensorManager.registerListener(myMagneticFieldListener, magneticField, SensorManager.SENSOR_DELAY_UI);
				//XXX
				
				
			

			

		// Get the SMS_RECEIVED intent
		Intent smsReceivedIntent = getIntent();
		//System.out.println("2. Scheme: " + smsReceivedIntent.getScheme() + "\n");
		Bundle extras = smsReceivedIntent.getExtras();
		
		
		// Extract Accelerometer values
		//float[] accelerometerValues = YourArrayUtils.unpack(extras.getString(key_accelerometer)); // extras.getString(key_accelerometer) returns NULL
		//float[] accelerometerValues = (float[]) extras.get(key_accelerometer);
		//accelerometerValues = extras.getFloatArray(key_accelerometer); // getFloatArray(key_accelerometer) returns NULL !!!!!!!!!!!!!!!!!!
		// Float.parseFloat(extras.getString(key_accelerometer));
		/*
		accelerometerValues[0] = extras.getFloat("accel_lateral");
		accelerometerValues[1] = extras.getFloat(accel_longitudinal, 11.0f);
		accelerometerValues[2] = extras.getFloat(accel_vertical, 12.0f);
		
		System.out.println("AFTER: " + accelerometerValues.length);
		for (int i=0; i < accelerometerValues.length; i++){
			System.out.println("Values["+i+"]: " + accelerometerValues[i] + "\n");
		}
		*/
		
		
		// Extract Magnetic Field values
		//float[] magneticFieldValues = YourArrayUtils.unpack(extras.getString(key_mfield_values));
		//float[] magneticFieldValues = (float[]) extras.get(key_mfield_values);
		//magneticFieldValues = extras.getFloatArray(key_mfield_values);
		/*
		magneticFieldValues[0] = extras.getFloat(mf_lateral, 20.0f);
		magneticFieldValues[1] = extras.getFloat(mf_longitudinal, 21.0f);
		magneticFieldValues[2] = extras.getFloat(mf_vertical, 22.0f);
		*/
		
		
		// Extract startTime (time the sender wrote the SMS)
		//long startTime = extras.getLong(key_start_Time);
		
		/*
		// Extract the orientation values
		azimuth = extras.getFloat("azimuth");
		pitch = extras.getFloat("pitch");
		roll = extras.getFloat("roll");
		*/

		
		/*
		System.out.println("Emulator 5556\nSender's values\nTime: " + startTime + " nanoseconds"
						  + "\nAzimuth: " + azimuth + " degrees\nPitch: " + pitch + " degrees\nRoll: " + roll + " degrees\n");
		
		/*
		float[] values = (float[]) extras.getFloatArray("orientation_values"); //smsReceivedIntent.getFloatArrayExtra("orientation_values");
		//values = Arrays.copyOf(extras.getFloatArray("Ptixiaki.src.dummy.ptixiaki.orientation_values"), values.length);
		
		System.out.println("5556: Values array\n");
		for (int i=0; i < values.length; i++){
			//values[i] = extras.getFloat("orientation_values", i); //extras.getFloat("orientation_values");
			System.out.println("Values["+i+"]: " + values[i] + "\n");
		}
		
		// fill in the TextViews
		tvLatitude.setText("Orientation: Lateral " + values[0] + " Longitudinal " + values[1] + " Vertical " + values[2]);
		*/
		
		// Extract its extras from the messageBody of the SMS
		Object[] pdus = (Object[]) extras.get("pdus");
		SmsMessage[] messages = new SmsMessage[pdus.length];
		for (int i = 0; i < pdus.length; i++){
			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
			
			sender_ID = messages[i].getOriginatingAddress();
			// Get ONLY accelerometer, magnetic field, latitude and longitude values from messagebody. Ignore all the other
			String substring = messages[i].getMessageBody().substring(5);
			//String substring = messages[i].getMessageBody().substring(2, 14);
			
			sender_orient_results = Unpack.toFloat(substring);
			// sender_orient_results[0] --> Sender's Azimuth
			// sender_orient_results[1] --> Sender's Pitch
			// sender_orient_results[2] --> Sender's Roll
			// sender_orient_results[3] --> Sender's Latitude
			// sender_orient_results[4] --> Sender's Longitude
		}
		
		sender_loc = new Location(LocationManager.GPS_PROVIDER);
		
		//sender_loc.setLatitude(sender_orient_values[3]);
		//sender_loc.setLongitude(sender_orient_values[4]);
		//sender_loc.setLatitude(37.98256);
		//sender_loc.setLongitude(23.71941);
		//sender_loc.setLatitude(37.98255833333334);
		//sender_loc.setLongitude(23.71940833333333);
		sender_loc.setLatitude(sender_orient_results[0]);
		sender_loc.setLongitude(sender_orient_results[1]);
		
		
		// fill in the TextViews
		//tvLatitude.setText(result[j]); // messages[i].getMessageBody().toLowerCase()
		//tvLongitude.setText(Double.toString());
		
		System.out.println("3.\n");
		//displayResults(orientation_values, accelerometerValues, magneticFieldValues, results[6]); // results[6] contains totalTime
	}
	
	
	// Resume location updates when we're resumed
	
	@Override
	public void onResume() {
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MainActivity.locUpdateTime, MainActivity.locUpdateDistance, locListenD);
//XXX	
		// Monitor changes in the Sensors
			sensorManager.registerListener(myAccelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
			//XXX
			//sensorManager.registerListener(myMagneticFieldListener, magneticField, SensorManager.SENSOR_DELAY_UI);
			//XXX
//XXX
		
		makeDot(dotModel, dotView, Color.RED);
		
		// Reset timers
		//startTime = (double) System.currentTimeMillis();
		//stopTime = 0.0;
		//totalTime = 0.0f;
	}
	
	
	// Turn off location updates if we're paused
	
	@Override
	public void onPause() {
		super.onPause();
		lm.removeUpdates(locListenD);
		
		//Remove sensor updates
		sensors_counter = 0;
		sensorManager.unregisterListener(myAccelerometerListener, accelerometer);
			//XXX
			//sensorManager.unregisterListener(myMagneticFieldListener, magneticField);
			//XXX
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item){
		
		switch (item.getItemId()) {
		
			case R.id.action_enable_Sensor:
				
				onSensorEnabled();
			    return true;
			    
			case R.id.action_disable_Sensor:
				
				sensor_enabled = false;
				sensors_counter = 0;
				
				sensorManager.unregisterListener(myAccelerometerListener, accelerometer);
				//XXX
				//sensorManager.unregisterListener(myMagneticFieldListener, magneticField);
				//XXX
				
				// Update dotView, with the new results
				makeDot(dotModel, dotView, Color.RED);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	private void onSensorEnabled() {
		
		sensor_enabled = true;
	}


	private class DispLocListener implements LocationListener {
		
		@Override
		public void onLocationChanged(Location location) {

//XXX
			//if(lm_count++ % 10 == 0){
//XXX
				if(location != null){
					myLoc.set(location);
					makeDot(dotModel, dotView, Color.RED);
				}
			//}
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
	
/*
	private class MySensorEventListener implements SensorEventListener {

		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			

		
				System.out.println("MySensorEventListener: enabled \n");
				switch (sensorEvent.sensor.getType()) { 
					
					case Sensor.TYPE_ACCELEROMETER:
						// Display every 10th value
						if(sensors_counter++ % 10 == 0) {
							for (int i = 0; i < sensorEvent.values.length; i++){
								accelerometerValues[i] = sensorEvent.values[i];
							}
							//Toast.makeText(getApplicationContext(), "ARRAY accelerometerValues updated", Toast.LENGTH_SHORT).show();
							
							// Update dotView, with the new results
							makeDot(dotModel, dotView, Color.RED);
						}
						break;
						
					case Sensor.TYPE_MAGNETIC_FIELD:
						// Display every 10th value
						if(sensors_counter++ % 10 == 0) {
							for (int i = 0; i < sensorEvent.values.length; i++){
								magneticFieldValues[i] = sensorEvent.values[i];
							}
							//Toast.makeText(getApplicationContext(), "ARRAY magneticFieldValues updated", Toast.LENGTH_SHORT).show();
							
							// Update dotView, with the new results
							makeDot(dotModel, dotView, Color.RED);
						}
						break;
					
					default:
						break;
				}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO React to a change in Sensor accuracy.
		}
		  
	  }
*/
	
	
	/**
	 * makeDot(Dots dots, DotView view, int color)
	 * 
	 * makeDot is used for screen representation of the desired results. These are:
	 * 	- Sender's positioning (left/right/front/back) , in comparison to the receiver's position (center of the screen).
	 * 	- ContactBadge of sender and receiver.
	 * 	- The distance between them.
	 */
	
	private void makeDot(Dots dots, DotView view, int color) {
		
		/**
		 *  0.  
		 * 
		 *  Clear previous results.
		 */
		
		dotModel.clearDots();
		if(dotView != null){
			dotView.clearAnimation();
		}
		
		/**
		 *  1. 
		 * 
		 *  Calculate distance between sender and receiver.
		 */
		
		distance_in_meters = myLoc.distanceTo(sender_loc);
		
		/**
		 * 2. 
		 * 
		 * Draw distance, receiver and sender on the receiver's screen.
		 * Receiver is represented by a red arrow, pointing to the sender.
		 * Sender is represented by a GREEN dot.
		 */
		
	/*
		//Location.distanceBetween(sender_orient_values[3], sender_orient_values[4], receiver_loc.getLatitude(), receiver_loc.getLongitude(), results);
		//Initial and final bearing are positive in the clockwise direction
	*/
		
		// bearingTo() returns valid double between -180.0 and 180.0 .
		// Negative bearing is equivalent to degrees west of true north
		bearing = myLoc.bearingTo(sender_loc);
		if ( bearing < 0 ) {
			bearing = 360 + bearing;
		}
		arrowRotate = bearing;
		
		dotView = new DotView(this, dotModel);
		ll.addView(dotView, 0);
		
		
		if (distance_in_meters == 0) {
			dots.addDot(x_middle, y_middle, 0, Color.GREEN, DOT_DIAMETER);
		}
		else {
			
			//float scale = 90/screen_radian;
			//float x_move = Math.round(screen_radian*bearing/90);
			//dots.addDot(x_middle + x_middle*x_move, y_middle, 0, Color.GREEN, DOT_DIAMETER);
			
			// ..counting difference clockwise, from 0 to 360.
			if ( bearing >= 0 && bearing <= 90 ) {
				// Sender is to the 1st quadrant of the receiver's imaginary circle
				
				// ??
				/*
				float angle = 90 - bearing;
				float x_movement = (float) (screen_radian * Math.cos(angle));
				float y_movement = (float) (screen_radian * Math.sin(angle));
				dots.addDot(x_middle + x_movement, y_middle - y_movement, 0, Color.GREEN, DOT_DIAMETER);
				*/
				// ??
				
				
				float res = Math.round(bearing/11.25);
				dots.addDot((x_middle + x_middle*res/8), (y_middle - x_middle + DOT_RADIAN) + res*x_middle/8, 0, Color.GREEN, DOT_DIAMETER);
				
				System.out.println("Difference is:" + bearing + "\n");
			}
			
			else if ( bearing > 90 && bearing <= 180 ) {
				// Sender is to the 2nd quadrant of the receiver's imaginary circle
				
				// ??
				/*
				float angle = bearing - 90;
				float x_movement = (float) (screen_radian * Math.cos(angle));
				float y_movement = (float) (screen_radian * Math.sin(angle));
				dots.addDot(x_middle + x_movement, y_middle + y_movement, 0, Color.GREEN, DOT_DIAMETER);
				*/
				// ??
				
				
				bearing = bearing - 90;
				 
				float res = Math.round(bearing/11.25);
				dots.addDot(width - res*x_middle/8, (y_middle - DOT_RADIAN) + res*x_middle/8, 0, Color.GREEN, DOT_DIAMETER);
				
				System.out.println("Difference is:" + bearing + "\n");
			}
			
			else if ( bearing > 180 && bearing <= 270 ) {
				// Sender is to the 3rd quadrant of the receiver's imaginary circle
				
				// ??
				/*
				float angle = 270 - bearing;
				float x_movement = (float) (screen_radian * Math.cos(angle));
				float y_movement = (float) (screen_radian * Math.sin(angle));
				dots.addDot(x_middle - x_movement, y_middle + y_movement, 0, Color.GREEN, DOT_DIAMETER);
				*/
				// ??
				
				
				bearing = bearing - 2*90;
				
				float res = Math.round(bearing/11.25);
				dots.addDot(x_middle - res*x_middle/8, (y_middle + x_middle - DOT_RADIAN) - res*x_middle/8, 0, Color.GREEN, DOT_DIAMETER);
				
				System.out.println("Difference is:" + bearing + "\n");
			}
			
			else {
				// Sender is to the 4th quadrant of the receiver's imaginary circle
				/*
				float angle = bearing - 270;
				float x_movement = (float) (screen_radian * Math.cos(angle));
				float y_movement = (float) (screen_radian * Math.sin(angle));
				dots.addDot(x_middle - x_movement, y_middle - y_movement, 0, Color.GREEN, DOT_DIAMETER);
				*/
				
				
				bearing = bearing - 3*90;
				float res = Math.round(bearing/11.25);
				dots.addDot(res*x_middle/8, (y_middle + DOT_RADIAN) - res*x_middle/8, 0, Color.GREEN, DOT_DIAMETER);
				
				System.out.println("Difference is:" + bearing + "\n");
			}
		}
		
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
        
        
		// Display sender's ID (name or phone number)
		tvSenderID.setText("Sender (green dot): " + sender_ID);
		
		// Display distance between sender and receiver
		if ( distance_in_meters < 1000 ) {
			tvDistance.setText("Distance: " + Math.round(distance_in_meters) + " meters.");
		}
		else {
			tvDistance.setText("Distance: " + Math.round(distance_in_meters/1000) + " kilometers.");
		}
	}
	
	
	private void makeDot_Update(Dots dots, DotView view, int color){
		
		
	}
	
	
	
	
	/**
	 * QuickContactHelper
	 * 
	 * This class takes a Context, QuickContactBadge and a telephone number.
	 * It attaches a locally stored image to the badge if there is one available for the specified phone number
	 * 
	 * 
	 * @author taz
	 *
	 */
/*
	public final class QuickContactHelper {

		private final String[] PHOTO_ID_PROJECTION = new String[] {
		    ContactsContract.Contacts.PHOTO_ID
		};

		private final String[] PHOTO_BITMAP_PROJECTION = new String[] {
		    ContactsContract.CommonDataKinds.Photo.PHOTO
		};

		private final QuickContactBadge badge;

		private final String phoneNumber;

		private final ContentResolver contentResolver;

		public QuickContactHelper(final Context context, final QuickContactBadge badge, final String phoneNumber) {

		    this.badge = badge;
		    this.phoneNumber = phoneNumber;
		    contentResolver = context.getContentResolver();

		}

		public void addThumbnail() {

		    final Integer thumbnailId = fetchThumbnailId();
		    if (thumbnailId != null) {
		        final Bitmap thumbnail = fetchThumbnail(thumbnailId);
		        if (thumbnail != null) {
		            badge.setImageBitmap(thumbnail);
		        }
		    }

		}

		private Integer fetchThumbnailId() {

		    final Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		    final Cursor cursor = contentResolver.query(uri, PHOTO_ID_PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

		    try {
		        Integer thumbnailId = null;
		        if (cursor.moveToFirst()) {
		            thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
		        }
		        return thumbnailId;
		    }
		    finally {
		        cursor.close();
		    }

		}

		final Bitmap fetchThumbnail(final int thumbnailId) {

		    final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
		    final Cursor cursor = contentResolver.query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

		    try {
		        Bitmap thumbnail = null;
		        if (cursor.moveToFirst()) {
		            final byte[] thumbnailBytes = cursor.getBlob(0);
		            if (thumbnailBytes != null) {
		                thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
		            }
		        }
		        return thumbnail;
		    }
		    finally {
		        cursor.close();
		    }

		}
	}
*/
}
