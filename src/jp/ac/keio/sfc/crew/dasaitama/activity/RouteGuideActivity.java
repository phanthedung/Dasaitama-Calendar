package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.map.MyOverLay;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class RouteGuideActivity extends MapActivity {

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location myLoc;
	private GeoPoint currentGP, srcGP, destGP;

	/** Called when the activity is first created. */
	MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routeguide);

		
		mapView = (MapView) findViewById(R.id.viewRouteMap);
		mapView.getController().setZoom(15);
		mapView.invalidate();
					
		int lat = this.getIntent()
			.getIntExtra("jp.ac.keio.sfc.crew.dasaitama.latitude", 0);
		int lon = this.getIntent()
			.getIntExtra("jp.ac.keio.sfc.crew.dasaitama.longtitude", 0);
		destGP = new GeoPoint(lat, lon);// �ړI�n�@�Ó��w
		Log.v("GEOPOINT", destGP.getLatitudeE6()+" "+destGP.getLongitudeE6());
		//GPS通信の収得
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		String provider = locationManager.getBestProvider(new Criteria(),true);
		myLoc = locationManager.getLastKnownLocation(provider); 
		locationManager.requestLocationUpdates(provider, 60*1000, 40,locationListener);
		if (myLoc!=null) {
			currentGP = new GeoPoint((int) (myLoc.getLatitude() * 1E6),
					(int) (myLoc.getLongitude() * 1E6));
			DrawPath(currentGP, destGP, Color.GREEN, mapView);
		}
		
		//ズームの設定
        MapView.LayoutParams lp;
        lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
        MapView.LayoutParams.WRAP_CONTENT,
        10, 10,
        MapView.LayoutParams.TOP_LEFT);
        View zoomControls = mapView.getZoomControls();
        mapView.addView(zoomControls, lp);
        mapView.displayZoomControls(true);
        
        //戻るボタン
		((Button) findViewById(R.id.btnViewRouteReturn))
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						locationManager.removeUpdates(locationListener);
						RouteGuideActivity.this.finish();
					}
				});
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/*
	 * 参考文献
	 * http://csie-tw.blogspot.com/2009/06/android-driving-direction-route-path.html
	 */
	private void DrawPath(GeoPoint src, GeoPoint dest, int color,
			MapView mMapView01) {

		// connect to map web service
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append(Double.toString((double) src.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString
				.append(Double.toString((double) src.getLongitudeE6() / 1.0E6));
		urlString.append("&daddr=");// to
		urlString
				.append(Double.toString((double) dest.getLatitudeE6() / 1.0E6));
		urlString.append(",");
		urlString.append(Double
				.toString((double) dest.getLongitudeE6() / 1.0E6));
		urlString.append("&ie=UTF8&0&om=0&dirflg=d&output=kml");
		Log.d("xxx", "URL=" + urlString.toString());
		// get the kml (XML) doc. And parse it to get the coordinates(direction
		// route).
		Document doc = null;
		HttpURLConnection urlConnection = null;
		URL url = null;
		try {
			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(urlConnection.getInputStream());

			if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
				// String path =
				// doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
				String path = doc.getElementsByTagName("GeometryCollection")
						.item(0).getFirstChild().getFirstChild()
						.getFirstChild().getNodeValue();
				Log.d("xxx", "path=" + path);
				String[] pairs = path.split(" ");
				String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
														// lngLat[1]=latitude
														// lngLat[2]=height

				// Init for draw path
				mapView.getOverlays().clear();
				mapView.invalidate();
				// srs
				GeoPoint startGP = new GeoPoint((int) (Double
						.parseDouble(lngLat[1]) * 1E6), (int) (Double
						.parseDouble(lngLat[0]) * 1E6));
				mMapView01.getOverlays()
						.add(new MyOverLay(startGP, startGP, 1));
				
				GeoPoint gp1;
				GeoPoint gp2 = startGP;
				for (int i = 1; i < pairs.length; i++) // the last one would be
														// crash
				{
					lngLat = pairs[i].split(",");
					gp1 = gp2;
					// watch out! For GeoPoint, first:latitude, second:longitude
					gp2 = new GeoPoint(
							(int) (Double.parseDouble(lngLat[1]) * 1E6),
							(int) (Double.parseDouble(lngLat[0]) * 1E6));
					mMapView01.getOverlays().add(
							new MyOverLay(gp1, gp2, 2, color));
					Log.d("xxx", "pair:" + pairs[i]);
				}
				mMapView01.getOverlays().add(new MyOverLay(dest, dest, 3)); // use
																			// the
																			// default
																			// color
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        
    }

    @Override
    public void onStop() {
    super.onStop();
    locationManager.removeUpdates(locationListener);
    }


	class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				currentGP = new GeoPoint((int) (loc.getLatitude() * 1E6),
						(int) (loc.getLongitude() * 1E6));
				Location destLoc = new Location(loc); 
				destLoc.setLatitude(destGP.getLatitudeE6()/1e6);
				destLoc.setLongitude(destGP.getLongitudeE6()/1e6);
				Toast.makeText(
						getBaseContext(),
						"目的地まで 後" + (int)loc.distanceTo(destLoc) +"メートル",
						Toast.LENGTH_LONG).show();
				DrawPath(currentGP, destGP, Color.GREEN, mapView);
				mapView.getController().animateTo(currentGP);
			}
		}

		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	}


}