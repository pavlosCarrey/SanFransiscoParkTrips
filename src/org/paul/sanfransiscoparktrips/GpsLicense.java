package org.paul.sanfransiscoparktrips;



import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class GpsLicense extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_license);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
//		display license terms here
		String license = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
		TextView tv = (TextView) findViewById(R.id.licenseTv);
		
		if (license != null) {
			tv.setText(license);
		} else {
			tv.setText("Google play services are not installed in this device.");
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
