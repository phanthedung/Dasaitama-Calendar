package jp.ac.keio.sfc.crew.dasaitama.activity;

/**
 *
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DEvent;
import jp.ac.keio.sfc.crew.dasaitama.model.DEventManager;
import jp.ac.keio.sfc.crew.dasaitama.model.DRoute;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author maz
 * 
 */
public class EventDetailActivity extends Activity {

	protected Context context;
	private DEvent e;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventdetail);

		final int event_id = this.getIntent().getIntExtra(
				"jp.ac.keio.sfc.crew.dasaitama.eventId", 0);

		if (event_id == 0)
			this.finish();
		final DEventManager dem = DEventManager.getInstance(this);
		e = dem.findById(event_id);
		TextView hiddened_id = (TextView)findViewById(R.id.event_detail_hidden_id);
		hiddened_id.setText(String.valueOf(event_id));

		Log.v("event", "Event =" + e.getTitle());

		// Log.d("CHECK FIND", "Title : "+ev.getTitle());

		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(e.getTitle());
		// TextView month = (TextView) findViewById(R.id.str_month);
		// month.setText(Integer.parseInt(e.getBegin().getMonth()));

		setBeginDateInformation(e.getBegin());
		setEndDateInformation(e.getEnd());
		if (e.getRoute() != null) {
			final DRoute route = e.getRoute();
			Log.d("route", route.toString());
			TextView route_tex = (TextView) findViewById(R.id.route_text);
			route_tex.setText(route.getBasicInfo());
			TextView startPoint = (TextView)findViewById(R.id.start);
			startPoint.setText(route.getLocationFrom(0));
			TextView finishPoint = (TextView)findViewById(R.id.finish);
			finishPoint.setText(route.getLocationTo(route.getListSize() - 1));
			
			route_tex.setText(route.getBasicInfo());
			TextView neededTime = (TextView)findViewById(R.id.needed_time);
			neededTime.setText(route.getStartTime().getHours()
					+":"
					+route.getStartTime().getMinutes()
					+ "→"
					+route.getFinishTime().getHours()
					+":"
					+route.getFinishTime().getMinutes()
					+" / "
					
					+route.getNeedTime().getTime()/(1000*60)
					+"分");
			LinearLayout route_detail = ( LinearLayout )findViewById(R.id.route_detail_on_event_detail);
			route_detail.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showRouteDetail(route);

				}
			});
		} else {
			TextView r = (TextView) findViewById(R.id.route_text);
			r.setText("ルートが設定されていません");
		}

		// //�ⓚ���p�ŏ���
		// cm.del(eventId, this);

		// �߂�{�^���������ꂽ�Ƃ��̏���
		Button back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});


	}

	private void showRouteDetail(final DRoute route) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle("乗り換え詳細");
		ListView detail_list = new ListView(getApplicationContext());
		ArrayAdapter<String> detail_adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.detail_row,
				R.id.transfer_info, route.getTransitInfo()) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				v = createDetailRow(v, route, position);
				return v;
			}
		};
		detail_list.setAdapter(detail_adapter);
		adb.setView(detail_list);
		adb.setPositiveButton("戻る", null);
		adb.setCancelable(true);
		adb.show();

	}

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

	private void setBeginDateInformation(Date d) {
		TextView time = (TextView) findViewById(R.id.str_time_on_event_detail);
		SimpleDateFormat df = new SimpleDateFormat("yyyy'年' MM'月' dd'日' HH'時' mm'分' ");
		time.setText(df.format(d));
	}

	private void setEndDateInformation(Date d) {
		TextView time = (TextView) findViewById(R.id.fin_time_on_event_detail);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy'年' MM'月' dd'日' HH'時' mm'分' ");
		time.setText(df.format(d));
	}

	public void deleteEvent(View v) {
		TextView id = (TextView) findViewById(R.id.event_detail_hidden_id);
		DEventManager cm = DEventManager.getInstance(getApplicationContext());
		String id_str = id.getText().toString();
		int id_int = Integer.parseInt(id_str);
		Log.v("route", String.valueOf(id_int));
		cm.deleteEventById(id_int);
		this.finish();;
	}

	public void editEvent(View v) {

		TextView id = (TextView) findViewById(R.id.event_detail_hidden_id);		

		String id_str = id.getText().toString();
		Log.v("hoge",id_str);
		int id_int = Integer.parseInt(id_str);
		Intent intent = new Intent(EventDetailActivity.this.getApplication(),
				AddEventActivity.class);
		intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.event_id", id_int);
		startActivity(intent);
	}

	public void routeToFinishLocation(View v) {
		if (e.getLoc()!=null) {
			Intent intent = new Intent(EventDetailActivity.this.getApplication(),
					RouteGuideActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.latitude", 
					e.getLoc().getLatitudeE6());
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.longtitude", 
					e.getLoc().getLongitudeE6());
			Log.v("OKOKOK", "OK");
			startActivity(intent);
		}
	}
}
