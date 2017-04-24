package dummy.ptixiaki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;


public class SendSmsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
	    System.out.println("SendSmsActivity  : enabled (from MainActivity)");
	    setContentView(R.layout.activity_sendsms);
	    
		// find the editText (where the Send button is appeared)
	    EditText editText = (EditText) findViewById(R.id.msgEditText);
	    /*
	    // Get the intent, get the extras and display them in the msgEditText EditText.
	    Intent intent = getIntent();
	    if(Intent.ACTION_SENDTO.equals(intent.getAction())){
	    	System.out.println("2.");
	    	editText.setText(intent.getExtras().getString(null));
	    }*/
	    
	    // listen for presses on the Send button
	    editText.setOnEditorActionListener(new HandleActionListener());
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	public void doSend(View view) {
		EditText addrTxt = (EditText) findViewById(R.id.addrEditText);
		EditText msgTxt = (EditText) findViewById(R.id.msgEditText);
		
		try {
			sendSmsMessage(addrTxt.getText().toString(), msgTxt.getText().toString());
			Toast.makeText(this, "SMS Sent", Toast.LENGTH_LONG).show();
		} 
		catch (Exception e) {
			Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	
	private void sendSmsMessage(String address,String message)throws Exception {
		SmsManager smsMgr = SmsManager.getDefault();
		smsMgr.sendTextMessage(address, null, message, null, null);
	}
	
	
	private class HandleActionListener implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			boolean handled = false;
	        if (actionId == EditorInfo.IME_ACTION_SEND) {
	            doSend((View) v);
	            handled = true;
	        }
	        
	        return handled;
		}
	}
}
