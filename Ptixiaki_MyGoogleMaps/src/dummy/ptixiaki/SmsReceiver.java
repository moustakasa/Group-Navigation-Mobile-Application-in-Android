package dummy.ptixiaki;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.text.TextUtils;


public class SmsReceiver extends BroadcastReceiver {
	
	public static final String queryString = "@app";
	
	
	@SuppressLint({ "InlinedApi", "NewApi", "DefaultLocale" })
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
			/**
			 * When a new SMS is received, we have to check if it's a normal SMS .. OR a special SMS sent from our app..
			 * So, we check incoming SMSs that start with the string @app.
			 * We create a Notification for our app, containing the values received from the sender's app.
			 * We deliver it to the Notification Manager.
			 * We ignore all other incoming SMSs.
			 */
			
			Bundle smsBundle = intent.getExtras();
			
			Object[] pdus = (Object[]) smsBundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			for (int i = 0; i < pdus.length; i++){
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				if (messages[i].getMessageBody().toLowerCase().startsWith(queryString)){
					
					
					String[] sender_lat_lon_values = TextUtils.split(messages[i].getMessageBody().substring(5), "\n");
					
					Intent mapIntent = new Intent(context, MapActivity.class);
					// Set the Activity to start in a new, empty task
					//mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					
					// Add extra data to the mapIntent
					mapIntent.putExtra("lat", sender_lat_lon_values[0]);
					mapIntent.putExtra("lon", sender_lat_lon_values[1]);
					mapIntent.putExtra("sender_ID", messages[i].getOriginatingAddress());
					
					// Create the PendingIntent
					PendingIntent displayPendingIntent = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					
					// Instantiate a Builder object.
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
					
					// Put all necessary info to the Builder
					builder
					.setContentTitle("_Ptixiaki")
					.setContentText(messages[i].getMessageBody().toLowerCase())
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(displayPendingIntent)
					.setAutoCancel(true);
					
					// Notifications are issued by sending them to the NotificationManager system service
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					// Build an anonymous Notification object from the builder.
					// Pass it to the NotificationManager
					mNotificationManager.notify(0, builder.build());
				}
			
			}
			/*for (SmsMessage message : messages) {
				String msg = message.getMessageBody();
				if (msg.toLowerCase().startsWith(queryString)) {
					System.out.println("1.You have new SMS");
					
				}
			}*/
		}
	}
}
