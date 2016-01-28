package intcloud.bridge;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
//1

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.widget.Toast;

   
public class Bridge extends Activity {
	
	WebView webView;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        webView = (WebView) findViewById(R.id.webView1);

//2
        webView.getSettings().setJavaScriptEnabled(true); //to enable JavaScript 
        webView.addJavascriptInterface(new bridge_protocol(this), "bridge"); // add bridge protocol 
        webView.setWebChromeClient(new WebChromeClient()); // for new device with API > 19
        
        webView.loadUrl("file:///android_asset/www/index.html");

    }
    
//3    	

    public class bridge_protocol {
        Context aContext;

        bridge_protocol(Context c) {
            aContext = c;
        }

        //  Show Toast Message
        // @param toast as String
         
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(aContext, toast, Toast.LENGTH_SHORT).show();
  
        }
  //4      
	}
    
}

