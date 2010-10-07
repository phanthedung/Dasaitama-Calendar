package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DOption;
import jp.ac.keio.sfc.crew.dasaitama.model.DRoute;
import jp.ac.keio.sfc.crew.dasaitama.model.DSchedule;
import jp.ac.keio.sfc.crew.dasaitama.model.DSearch;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/*
 * ��ʏ�̃N���X�B
 */
public class SearchTransitActivity extends Activity {
	private GeoPoint startLocation;
	private GeoPoint finishLocation;
	private Calendar str, fin;
	public DSchedule sch;
	private ListView result_listView;
	private String[] value;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_transit);
		Intent intent = getIntent();
		value = intent.getStringArrayExtra("value");
		str = Calendar.getInstance(TimeZone.getDefault());
		fin = Calendar.getInstance(TimeZone.getDefault());
		str.set(Integer.parseInt(value[0]), 
				Integer.parseInt(value[1]), 
				Integer.parseInt(value[2]), 
				Integer.parseInt(value[3]), 
				Integer.parseInt(value[4]));
//		fin.set(Integer.parseInt(value[5]), 
//				Integer.parseInt(value[6]), 
//				Integer.parseInt(value[7]), 
//				Integer.parseInt(value[8]), 
//				Integer.parseInt(value[9]));
		String[] start = value[5].split(",");
		String[] finish = value[6].split(",");

		startLocation = new GeoPoint(Integer.parseInt(start[0]), Integer
				.parseInt(start[1]));
		finishLocation = new GeoPoint(Integer.parseInt(finish[0]), Integer
				.parseInt(finish[1]));

		TextView startPoint = (TextView) findViewById(R.id.start);
		startPoint.setText(Double.valueOf(startLocation.getLatitudeE6()) / 1E6
				+ "," + Double.valueOf(startLocation.getLongitudeE6()) / 1E6);

		TextView goalPoint = (TextView) findViewById(R.id.goal);
		goalPoint.setText(Double.valueOf(finishLocation.getLatitudeE6()) / 1E6
				+ "," + Double.valueOf(finishLocation.getLongitudeE6()) / 1E6);
		Log.v("hoge", String.valueOf(finishLocation.getLatitudeE6()));
		sch = new DSchedule(startLocation, finishLocation, str);

		result_listView = (ListView) findViewById(R.id.result_view);

	}

	public void search(View v) {
		if (isNetworkAvailable()) {
			Toast.makeText(getApplicationContext(), "情報を取得中", Toast.LENGTH_SHORT).show();
			onSearch();
			
		} else {
			showNetWorkAlert();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager m = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = m.getActiveNetworkInfo();
		return ni == null ? false : ni.isAvailable();
	}

	private void showNetWorkAlert() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("ネットワーク接続");
		adb.setMessage("接続が確認できません。");
		adb.setPositiveButton("OK", null);
		adb.show();
	}

	public DOption checkOptions() {
		String sort = null;
		RadioGroup radio_group = (RadioGroup) findViewById(R.id.sort);
		int id = radio_group.getCheckedRadioButtonId();
		if (id == R.id.fare) {
			sort = "fare";
		} else if (id == R.id.time) {
			sort = "time";
		} else if (id == R.id.change_lines) {
			sort = "num";
		}

		Spinner sp = (Spinner) findViewById(R.id.Spinner01);
		long item = sp.getSelectedItemId();
		int margin = 0;
		if(item==0){
			margin=15;
		}else if(item==1){
			margin=20;
		}else if(item==2){
			margin =25;
		}else if(item==3){
			margin=30;
		}else{
			margin = 30;
		}
		
		return new DOption(sort, margin);
	}

	/*
	 * @yokota �������ʂ�\�����郁�\�b�h
	 */
	public void showResult(final ArrayList<DRoute> routeList) {
		if (routeList!=null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getApplicationContext(), R.layout.result, R.id.result) {
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					v = convertResultList(v, position, routeList);
					return v;
				}
			};
			for (int i = 0; i < routeList.size(); i += 1) {
				adapter.add(routeList.get(i).getBasicInfo());
			}
			result_listView.setAdapter(adapter);
		} else {
			Toast.makeText(this, "ルートがみつかりません", Toast.LENGTH_LONG).show();
		}

	}

	/*
	 * @yokota ���X�g�r���[���J�X�^�}�C�Y���郁�\�b�h �A��l�͕ύX��̃r���[
	 */
	private View convertResultList(View v, final int position,
			final ArrayList<DRoute> routeList) {
		// �e�r���[��ݒ肷��B
		TextView result = (TextView) v.findViewById(R.id.result);
		Button detail = (Button) v.findViewById(R.id.detail);
		Button deside = (Button) v.findViewById(R.id.deside);
		DRoute r = routeList.get(position);
		String timeData = r.getTimeFrom(0) + "→"
				+ r.getTimeTo(r.getListSize() - 1);
		if (result != null)
			result.setText(timeData + " " + r.getBasicInfo());
		// ���׃{�^�����X�i�[���쐬
		detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDetails(routeList.get(position));
			}
		});

		// ����{�^�����X�i�[���쐬�B
		deside.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchTransitActivity.this.getIntent().putExtra("DRoute",
						routeList.get(position));
				SearchTransitActivity.this.setResult(RESULT_OK,
						SearchTransitActivity.this.getIntent());
				SearchTransitActivity.this.finish();
				Log.v("---------", "gggggggg");
			}
		});
		return v;
	}

	/*
	 * @�悱�� o-method ���[�g�̏ڍׂ����郁�\�b�h �����Ȃ�����ɂ��C�����Ă��郁�\�b�h
	 */
	public void showDetails(final DRoute r) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("経路詳細");
		ListView detail_list = new ListView(getApplicationContext());
		ArrayAdapter<String> detail_adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.detail_row,
				R.id.transfer_info, r.getTransitInfo()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				v = createDetailRow(v, r, position);
				return v;
			}
		};
		detail_list.setAdapter(detail_adapter);
		adb.setView(detail_list);
		adb.setPositiveButton("決定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				desideRouteAndGoToAddEventActivity(r);
				
			}

		});
		adb.setNegativeButton("戻る", null);
		adb.setCancelable(true);
		adb.show();
	}

	/*
	 * @yokota ���׃A���[�g�̎��ɁA���r���[
	 */
	private View createDetailRow(View v, DRoute r, int lines) {
		TextView transit = (TextView) v.findViewById(R.id.transfer_info);
		TextView from = (TextView) v.findViewById(R.id.from_info);
		TextView to = (TextView) v.findViewById(R.id.to_info);
		ImageView img = (ImageView) v.findViewById(R.id.detail_image);
		if (transit != null)
			transit.setText(r.getTransitInfo(lines));
		if (from != null)
			from.setText(r.getTimeFrom(lines) + " " + r.getLocationFrom(lines));
		if (to != null)
			to.setText(r.getTimeTo(lines) + " " + r.getLocationTo(lines));

		if (img != null) {
			if (r.isTrainOrWalk(lines) == DRoute.TRAIN) {
				img.setImageResource(R.drawable.rail);
			} else {
				img.setImageResource(R.drawable.walk);
			}
		}

		return v;
	}

	/*
	 * @���c o-method ���[�g�����肷�郁�\�b�h �����Ȃ�����ɂ��C�����Ă��郁�\�b�h�ł��B
	 */
	public void showDesideRoute(final DRoute r) {

			
			desideRouteAndGoToAddEventActivity(r);
			}

	public void desideRouteAndGoToAddEventActivity(DRoute r){
		Toast.makeText(this, "ルートを保存しました", Toast.LENGTH_LONG).show();
		SearchTransitActivity.this.getIntent().putExtra("DRoute", r);
		SearchTransitActivity.this.setResult(RESULT_OK,
				SearchTransitActivity.this.getIntent());
		SearchTransitActivity.this.finish();
	}

	/*
	 * @yokota �������郁�\�b�h
	 */
	private void onSearch() {

		DSearch get_search = new DSearch(this);
		get_search.execute();

	}

	/*
	 * @yokota schedule���Q�b�g���郁�\�b�h
	 */
	public DSchedule getSchedule() {
		return sch;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/*
	 * @yokota
	 */
	public void backPrvActivity(View v) {
		SearchTransitActivity.this.getIntent();
		SearchTransitActivity.this.setResult(RESULT_CANCELED,
				SearchTransitActivity.this.getIntent());
		SearchTransitActivity.this.finish();
	}
}