package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.util.List;

import jp.ac.keio.sfc.crew.dasaitama.R;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class SelectLocationActivity extends MapActivity {

	MapView mapView;
	int[] geoPoint = { 0, 0 };

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectlocation);
		mapView = (MapView) findViewById(R.id.selectLocationMap);
		mapView.getController().setZoom(16);
		mapView.getController().animateTo(new GeoPoint(35396221, 139466404));
		mapView.invalidate();
		
		//TextView search = (TextView)findViewById(R.id.selectAddress);
		//search.setText(R.string.selectAddress, BufferType.EDITABLE);

		// �����{�^�����N���b�N����
		((Button) findViewById(R.id.btnSearchAddress))
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						final TextView selectAddress = (TextView) findViewById(R.id.selectAddress);
						GeoPoint addressGP = findAddress(selectAddress
								.getText().toString());
						if (addressGP == null) {
							showAlert();
						} else {
							mapView.getController().animateTo(addressGP);
						}

					}
				});

		// �����{�^�����N���b�N����
		((Button) findViewById(R.id.btnSelectAddress))
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String value = String.valueOf(geoPoint[0]) + ","
								+ String.valueOf(geoPoint[1]);
						/*
						 * SelectLocationActivity.this.getIntent().putExtra(
						 * "jp.ac.keio.sfc.crew.dasaitama.geoPoint", value);
						 * SelectLocationActivity.this.setResult(RESULT_OK,
						 * SelectLocationActivity.this.getIntent());
						 * SelectLocationActivity.this.finish();
						 */
						SharedPreferences prefer = getSharedPreferences(
								"PREVIOUS_RESULT", MODE_PRIVATE);
						SharedPreferences.Editor editor = prefer.edit();
						editor
								.putString(
										"jp.ac.keio.sfc.crew.dasaitama.geoPoint",
										value);
						editor.commit();
						setResult(RESULT_OK);
						finish();
					}
				});

		// �Y�[���̐ݒ�
		MapView.LayoutParams lp;
		lp = new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, 10, 10,
				MapView.LayoutParams.TOP_LEFT);
		View zoomControls = mapView.getZoomControls();
		mapView.addView(zoomControls, lp);
		mapView.displayZoomControls(true);

		// �^�b�`�Œn�_������
		OnTouchListener myOnTouchListener = new OnTouchListener() {
			public boolean onTouch(View mView, MotionEvent m) {
				if (m.getAction() == MotionEvent.ACTION_UP) {
					int x = (int) m.getX();
					int y = (int) m.getY();
					Projection projection = mapView.getProjection();
					// selectedGP ���@���肵���n�_
					GeoPoint selectedGP = projection.fromPixels(x, y);
					mapView.getController().setCenter(selectedGP);
					geoPoint[0] = selectedGP.getLatitudeE6();
					geoPoint[1] = selectedGP.getLongitudeE6();
					Toast.makeText(
							getBaseContext(),
							"Lat : " + (int) geoPoint[0] + " Long : "
									+ geoPoint[1] + "\n 地点を選択しました",
							Toast.LENGTH_SHORT).show();

				}

				return false;
			};
		};

		mapView.setOnTouchListener(myOnTouchListener);

	}

	private GeoPoint findAddress(String address) {
		GeoPoint found = null;
		Geocoder gc = new Geocoder(this);
		try {
			List<Address> foundAdresses = gc.getFromLocationName(address, 5);
			if (foundAdresses.size() == 0) { // if no address found, display an
				// error
			} else { // else display address on map
				for (int i = 0; i < foundAdresses.size(); ++i) {
					// Save results as Longitude and Latitude
					// @todo: if more than one result, then show a select-list
					Address x = foundAdresses.get(i);
					found = new GeoPoint((int) (x.getLatitude() * 1E6),
							(int) (x.getLongitude() * 1E6));

				}
			}
		} catch (Exception e) {

		} // Search addresses
		return found;

	}

	private void showAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("見つかりません");
		builder.setPositiveButton("ok", null);
		builder.show();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
