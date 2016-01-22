package intcloud.bridge;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

   
public class Bridge extends Activity {
	Context context = this;
	double latitude;
	double longitude;
	WebView webView;
	 
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        
        //Inject WebAppInterface methods into Web page by having Interface 'Android' 
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/index.html");
                
    	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListner = new MyLocationListner();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListner);  
		
	//	  Button playButton = (Button) findViewById(R.id.button1);
	//    playButton.setVisibility(1); 
		/*
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
	        notificationIntent.addCategory("android.intent.category.DEFAULT");
	        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.SECOND, 5);
	        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
	        */
    }
    
    public void call_js(View view) {
	 javaFnCall(webView, "aaa('this is the test')");
    	//webView.evaluateJavascript("aaa('aaaaaaaaaaaaaaaaa')", null);
	 notifyUser("you won a ferary!");
    }

	public void javaFnCall(WebView webView, String jsString) {
		final String webUrl = "javascript:" +  jsString;
		// Add this to avoid android.view.windowmanager$badtokenexception unable to add window

		 if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
	        webView.evaluateJavascript(jsString, null);
	        System.out.println(jsString);
	    } else {
	        webView.loadUrl(webUrl);
	        System.out.println("old way");
	    }
	}
	
	public void notifyUser(String message) {
		// prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
		// use System.currentTimeMillis() to have a unique ID for the pending intent
		PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

		// build notification
		// the addAction re-use the same intent to keep the example short
		Notification n  = new Notification.Builder(this)
		        .setContentTitle("My notification")
		        .setContentText(message)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent)
		        .setAutoCancel(true)
		        .addAction(R.drawable.ic_launcher, "Call", pIntent)
		        .addAction(R.drawable.ic_launcher, "More", pIntent)
		        .addAction(R.drawable.ic_launcher, "And more", pIntent)
		        .build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, n); 
	}
	
    //Class to be injected in Web page
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show Toast Message
         * @param toast
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
            sendjs("aaa(\"" + latitude + ' ' + longitude + "\")");
        }
 
        private void sendjs(final String s) {
            webView.post(new Runnable() {
                public void run() {
                    webView.loadUrl("javascript:" + s + ";");
                }
            });
            System.out.println("javscript done..");
        }
        
        /**
         * Show Dialog 
         * @param dialogMsg
         */
        @JavascriptInterface
        public void showDialog(String dialogMsg){
        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        	 
            // Setting Dialog Title
            alertDialog.setTitle("Title of the Android dialog");
     
            // Setting Dialog Message
            //alertDialog.setMessage(dialogMsg);
            alertDialog.setMessage(latitude + " " + longitude);
            alertDialog.setCancelable(true);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
  // do something
                        	 dialog.cancel();
              
                         }
                     });
     
            // Showing Alert Message
            alertDialog.show();
        }
        
        /**
         * Intent - Move to next screen
         */
        @JavascriptInterface
        public void moveToNextScreen(){
        	 AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
             // Setting Dialog Title
             alertDialog.setTitle("Alert");
             // Setting Dialog Message
             alertDialog.setMessage("Are you sure you want to leave to next screen?");
             // Setting Positive "Yes" Button
             alertDialog.setPositiveButton("YES",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                        	 //Move to Next screen
                     //   	 Intent chnIntent = new Intent(Bridge.this, ChennaiIntent.class);
                        	 Intent chnIntent = new Intent(Bridge.this, Popup.class);
                        	 startActivity(chnIntent);  
                         }
                     });
             // Setting Negative "NO" Button
             alertDialog.setNegativeButton("NO",
                     new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             // Cancel Dialog
                             dialog.cancel();
                         }
                     });
             // Showing Alert Message
             alertDialog.show();
        }
    }

    public void retriveAddress() {
		String resultAddress;
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> list = geocoder.getFromLocation(latitude, longitude,
					1);
			if (list != null && list.size() > 0) {
				Address addressList = list.get(0);
				resultAddress = addressList.getAddressLine(0) + ", "
						+ addressList.getLocality();
				System.out.println(resultAddress);
			} else {
	//			address.setText(R.string.no_addresses_retrieved);
			}
		} catch (IOException e) {
	//		address.setText(R.string.cannot_get_address_);
		}
	}

	class MyLocationListner implements LocationListener {

	//	@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				retriveAddress();
		//		System.out.println(latitude + " " + longitude );
			}
		}

//		@Override
		public void onProviderDisabled(String arg0) {
	
		}

	//	@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

	//	@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

	}
}

