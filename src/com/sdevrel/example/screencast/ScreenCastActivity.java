package com.sdevrel.example.screencast;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sec.android.allshare.ERROR;
import com.sec.android.allshare.ServiceConnector;
import com.sec.android.allshare.ServiceConnector.IServiceConnectEventListener;
import com.sec.android.allshare.ServiceConnector.ServiceState;
import com.sec.android.allshare.ServiceProvider;
import com.sec.android.allshare.screen.ScreenCastManager;
import com.sec.android.allshare.screen.ScreenCastManager.IScreenCastEventListener;

public class ScreenCastActivity extends Activity implements OnClickListener {
	
	private Button mScreenCastButton;
	private Button mModeButton;
	private View mDualScreenViewWrapper;
	private DualScreenView mDualScreenView;
	
	private boolean mIsScreenCastStarted = false;
	private boolean mIsTwinMode = true;
	private ScreenCastManager mScreenCastManager;
	private ServiceProvider mServiceProvider;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mScreenCastButton = (Button) findViewById(R.id.screen_cast);
        mScreenCastButton.setOnClickListener(this);
        
        mModeButton = (Button) findViewById(R.id.mode);
        mModeButton.setOnClickListener(this);
        
        mDualScreenViewWrapper = findViewById(R.id.dual_screen_view_wrapper);
        
        mDualScreenView = (DualScreenView) findViewById(R.id.dual_screen_view);
        
        ERROR err = ServiceConnector.createServiceProvider(this, mServiceConnectEventListener);
        if (err == ERROR.FRAMEWORK_NOT_INSTALLED) {
			// AllShare Framework Service is not installed.
			AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Framework not installed").create();
			dialog.show();
		} else if (err == ERROR.INVALID_ARGUMENT) {
			// Input argument is invalid. Check and try again
			AlertDialog dialog = new AlertDialog.Builder(this).setMessage("Invalid argument").create();
			dialog.show();
		}
    }
    
    private IServiceConnectEventListener mServiceConnectEventListener = new IServiceConnectEventListener() {
		
		@Override
		public void onDeleted(ServiceProvider sprovider) {
			
		}
		
		@Override
		public void onCreated(ServiceProvider sprovider, ServiceState state) {
			mServiceProvider = sprovider;
			
			/**
			 * TODO:
			 * 1. Get screen cast manager
			 * 2. Set screen cast event listener
			 * 3. Set dual screen drawer
			 */
			//<task>
			mScreenCastManager = sprovider.getScreenCastManager();
			mScreenCastManager.setScreenCastEventListener(mScreenCastEventListener);
			mScreenCastManager.setDualScreenDrawer(mDualScreenView);
			//</task>
		}
	};
    
    @Override
    protected void onDestroy() {
    	if (mScreenCastManager != null) {
    		/**
    		 * TODO:
    		 * 1. Remove screen cast event listener
    		 * 2. Stop screen cast
    		 */
    		//<task>
    		mScreenCastManager.setScreenCastEventListener(null);
    		mScreenCastManager.stop();
    		//</task>
    	}
    	
    	if (mServiceProvider != null) {
    		ServiceConnector.deleteServiceProvider(mServiceProvider);
    	}
    	
    	super.onDestroy();
    }

	@Override
	public void onClick(View v) {
		if (v == mScreenCastButton) {
			if (mIsScreenCastStarted) {
				/**
				 * TODO: stop screen cast
				 */
				//<task>
				mScreenCastManager.stop();
				//</task>
				
				mIsScreenCastStarted = false;
			} else {
				/**
				 * TODO: start screen cast
				 */
				//<task>
				mScreenCastManager.activateManagerUI();
				//</task>
				
				mIsScreenCastStarted = true;
			}
		} else if (v == mModeButton) {
			if (mIsTwinMode) {
				mDualScreenViewWrapper.setVisibility(View.VISIBLE);
				
				mModeButton.setText(R.string.twin_mode);
				
				/**
				 * TODO: set mode to dual
				 */
				//<task>
				mScreenCastManager.setMode(ScreenCastManager.ScreenMode.DUAL);
				//</task>
				
				mIsTwinMode = false;
			} else {
				mDualScreenViewWrapper.setVisibility(View.GONE);
				
				mModeButton.setText(R.string.dual_mode);
				
				/**
				 * TODO: set mode to twin
				 */
				//<task>
				mScreenCastManager.setMode(ScreenCastManager.ScreenMode.TWIN);
				//</task>
				
				mIsTwinMode = true;
			}
		}
	}
	
	private IScreenCastEventListener mScreenCastEventListener = new IScreenCastEventListener() {
		
		@Override
		public void onStopped(ScreenCastManager manager) {
			mScreenCastButton.setText(R.string.start_screen_cast);
			
			mModeButton.setVisibility(View.GONE);
		}
		
		@Override
		public void onStarted(ScreenCastManager manager) {
			mScreenCastButton.setText(R.string.stop_screen_cast);
			
			mModeButton.setText(R.string.dual_mode);
			mModeButton.setVisibility(View.VISIBLE);
		}
	};
	
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	};
}