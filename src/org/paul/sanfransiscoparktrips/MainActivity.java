package org.paul.sanfransiscoparktrips;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final double SAN_FRANCISCO_LAT = 37.776824;
	private static final double SAN_FRANCISCO_LNG = -122.419073;
	
	public GoogleMap mMap;
	private HashMap<String, Park> parks;
	private HashMap<String, Bicycle> bikes;
	private List<Marker> parkMarkers;
	private List<Marker> bikeMarkers;
	private Spinner sp;
	private ImageButton search;
	private String typeSelected;
	private LatLngBounds.Builder bounds;
	private int boundPointsLength;
	private Park selectedPark;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(servicesOK()) {
			setContentView(R.layout.map);
			if(initMap()) {
				gotoLocation(SAN_FRANCISCO_LAT, SAN_FRANCISCO_LNG, 12);
				SAXParkHandler saxParkHandler = new SAXParkHandler();
				try {
					parks = saxParkHandler.getParkInfo(new InputSource(getResources().openRawResource(R.raw.sfparks)));
					parkMarkers = new ArrayList<Marker>();
					SAXBikeHandler saxBikeHandler = new SAXBikeHandler();
					bikes = saxBikeHandler.getBikeInfo(new InputSource(getResources().openRawResource(R.raw.bicycle)));
					bikeMarkers = new ArrayList<Marker>();
					
					
					sp = (Spinner) findViewById(R.id.spinner1);
					ArrayAdapter<CharSequence> ad = ArrayAdapter.createFromResource(this, R.array.parkTypes, android.R.layout.simple_list_item_1);
					ad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
					sp.setAdapter(ad);
					sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							for (Marker marker : parkMarkers) {
								marker.remove();
							}
							for (Marker marker : bikeMarkers) {
								marker.remove();
							}
							parkMarkers.clear();
							bikeMarkers.clear();
							bounds = new LatLngBounds.Builder();
							boundPointsLength = 0;
							for (Entry<String, Park> entry : parks.entrySet()) {
								String choice = parent.getItemAtPosition(position).toString();
								String parkType = entry.getValue().getType().replaceAll("\\s+$", "").replaceAll("\n", "");
								TextView searchTv = (TextView) findViewById(R.id.editText1);
								String searchQuery = searchTv.getText().toString();
								typeSelected = choice;
								if (choice.equals(parkType) || choice.equals("All")) {
									if (entry.getValue().getName().toLowerCase().contains(searchQuery.toLowerCase())) {
										LatLng ll = new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude());
										parkMarkers.add(setMarker(entry.getValue().getName(), "park", ll));
										bounds.include(ll);
										boundPointsLength++;
									}
									
								}
							}
							
							mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
								
								@Override
								public void onMapLoaded() {
									if (boundPointsLength != 0) {
										mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
										boundPointsLength = 0;
									}
																		
								}
							});
							
							
							
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							
						}
					});
					
					search = (ImageButton) findViewById(R.id.button1);
					search.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {							
							TextView searchTv = (TextView) findViewById(R.id.editText1);
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(searchTv.getWindowToken(), 0);
							
							String searchQuery = searchTv.getText().toString();
							for (Marker marker : parkMarkers) {
								marker.remove();
							}
							parkMarkers.clear();
							bounds = new LatLngBounds.Builder();
							boundPointsLength = 0;
							for (Entry<String, Park> entry : parks.entrySet()) {
								Park park = entry.getValue();
								if(park.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
									if(typeSelected.equals(park.getType().replaceAll("\\s+$", "").replaceAll("\n", "")) || typeSelected.equals("All")) {
										LatLng ll = new LatLng(park.getLatitude(), park.getLongitude());
										parkMarkers.add(setMarker(park.getName(), "park", ll));
										bounds.include(ll);
										boundPointsLength++;
									}									
								}
							}
							if (boundPointsLength != 0) {
								mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
								boundPointsLength = 0;
							}
							for (Marker bike : bikeMarkers) {
								bike.remove();
							}
							bikeMarkers.clear();
							
						}
					});
					
					mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
						
						@Override
						public void onMapClick(LatLng arg0) {
							LinearLayout firstLayout = (LinearLayout) findViewById(1111);
							if (firstLayout != null) {
								((ViewGroup)firstLayout.getParent()).removeView(firstLayout);
							}
							selectedPark = null;
						}
					});
					
					mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
						
						@Override
						public void onInfoWindowClick(Marker marker) {
							if(marker.getSnippet().equals("Park")) {
								StringBuilder uri = new StringBuilder("geo:");
								uri.append(marker.getPosition().latitude);
								uri.append(",");
								uri.append(marker.getPosition().longitude);
								uri.append("?z=10");
								uri.append("&q=" + URLEncoder.encode(marker.getTitle()));
								
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
								startActivity(intent);
							}
							
						}
					});
					
					
				} catch (NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		else {
			Toast.makeText(this, "The Google Maps aren't available.", Toast.LENGTH_LONG).show();
		}
	}



	private Marker setMarker(String name, String type, LatLng ll) {
		MarkerOptions options = new MarkerOptions()
			.title(name)
			.position(ll)
			.icon((type.equals("park")) ? BitmapDescriptorFactory.fromResource(R.drawable.ic_park) : BitmapDescriptorFactory.fromResource(R.drawable.ic_bike))
			.snippet((type.equals("park") ? "Park" : "Bike Stand"));
		return mMap.addMarker(options);
	}



	private void gotoLocation(double lat, double lng, int zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);		
	}



	private boolean initMap() {
		if (mMap == null) {
			MapFragment mapFrag =
					(MapFragment) getFragmentManager().findFragmentById(R.id.map);
			mMap = mapFrag.getMap();
		}
		if (mMap != null) {
			mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
				
				@Override
				public View getInfoWindow(Marker arg0) {
					return null;
				}
				
				@Override
				public View getInfoContents(Marker marker) {
					View v;
					if (marker.getSnippet().equals("Park")) {
					v = getLayoutInflater().inflate(R.layout.info_window, null);
					
					
					TextView parkName = (TextView) v.findViewById(R.id.parkName);
					TextView parkType = (TextView) v.findViewById(R.id.parkType);
					TextView managerName = (TextView) v.findViewById(R.id.managerName);
					TextView email = (TextView) v.findViewById(R.id.email);
					TextView phone = (TextView) v.findViewById(R.id.phone);
					
					Park park = parks.get(marker.getTitle());					
				
					parkName.setText(marker.getTitle());
					parkType.setText(park.getType());
					managerName.setText(park.getManager());
					email.setText(park.getEmail());
					phone.setText(park.getPhone());
					}
					else {
						v = getLayoutInflater().inflate(R.layout.bike_info_window, null);
						TextView bikeLocation = (TextView) v.findViewById(R.id.location);
						TextView bikeAddress = (TextView) v.findViewById(R.id.address);
						TextView bikeRacks = (TextView) v.findViewById(R.id.racks);
						TextView bikeSpaces = (TextView) v.findViewById(R.id.spaces);
						
						Bicycle bike = bikes.get(marker.getTitle());
						
						bikeLocation.setText(bike.getLocation());
						bikeAddress.setText(bike.getAddress());
						bikeRacks.setText(Integer.toString(bike.getRacks()));
						bikeSpaces.setText(Integer.toString(bike.getSpaces()));
					}
					return v;
				}
			});
			
			mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
				
				@Override
				public boolean onMarkerClick(final Marker marker) {
					
					
					LinearLayout firstLayout = (LinearLayout) findViewById(1111);
					if (firstLayout != null) {
						((ViewGroup)firstLayout.getParent()).removeView(firstLayout);
					}	
					
					selectedPark = parks.get(marker.getTitle());
					if (selectedPark != null) {
					LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					
					LinearLayout layout = new LinearLayout(MainActivity.this);
					layout.setOrientation(LinearLayout.HORIZONTAL);
					layout.setId(1111);
					
					ImageButton email = new ImageButton(MainActivity.this);
					email.setImageDrawable(getResources().getDrawable(R.drawable.email));
					email.setLayoutParams(params);
					
					ImageButton phone = new ImageButton(MainActivity.this);
					phone.setImageDrawable(getResources().getDrawable(R.drawable.call));
					phone.setLayoutParams(params);
					
					layout.addView(email);
					layout.addView(phone);
					
					MainActivity.this.addContentView(layout, params);
					
					email.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Park park = parks.get(marker.getTitle());
							Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", park.getEmail(), null));
							startActivity(Intent.createChooser(intent, "Send Email"));
						}
					});
					
					phone.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Park park = parks.get(marker.getTitle());
							Intent intent = new Intent(Intent.ACTION_CALL);
							intent.setData(Uri.fromParts("tel", park.getPhone(), null));
							startActivity(intent);
						}
					});
					}
					return false;
				}
			});
		}
		return (mMap != null);		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.gpsLicense) {
			Intent intent = new Intent(this, GpsLicense.class);
			startActivity(intent);
		}
		if (id == R.id.bike) {
				if (bikeMarkers.size() != 0) {
					for (Marker bike : bikeMarkers) {
						bike.remove();
					}
					bikeMarkers.clear();
				}
				else {
				for (Marker bike : bikeMarkers) {
					bike.remove();
				}
				bikeMarkers.clear();
				if (selectedPark != null) {
					for (Entry<String, Bicycle> entry : bikes.entrySet()) {
						Bicycle bike = entry.getValue();
						float[] distance = new float[1];
						Location.distanceBetween(selectedPark.getLatitude(), selectedPark.getLongitude(), 
								bike.getLatitude(), bike.getLongitude(), distance);
						if (distance[0] <= 1000) {
							LatLng ll = new LatLng(bike.getLatitude(), bike.getLongitude());
							bikeMarkers.add(setMarker(bike.getLocation(), "bike", ll));
						}
					}
				}
				else {
					for (Entry<String, Bicycle> entry : bikes.entrySet()) {
						Bicycle bike = entry.getValue();
						LatLng ll = new LatLng(bike.getLatitude(), bike.getLongitude());
						bikeMarkers.add(setMarker(bike.getLocation(), "bike", ll));
					}
				}
				}
			
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		}
		else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		}
		else {
			Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
