package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DEvent;
import jp.ac.keio.sfc.crew.dasaitama.model.DEventManager;
import jp.ac.keio.sfc.crew.dasaitama.model.DRoute;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEventActivity extends Activity {
	/*
	 * 愛すべき変数たち
	 */
	int Id;
	Date strTime = null, finTime = null;
	String strLocation = null, finLocation = null, description, title;
	private Date focusDate;
	DRoute route = null;

	// Route r;
	/*----------------------------------------------------------------------------
	 * アクティビティ
	 *----------------------------------------------------------------------------*/

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Log.v("EXAMPLE", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_event);
		// focusDateを決定
		long time = this.getIntent().getLongExtra(
				"jp.ac.keio.sfc.crew.dasaitama.focusDate", 0);
		if (time != 0) {
			focusDate = new Date(time);
		} else {
			focusDate = new Date();
		}
		Log.v("Time", focusDate.toString());

		// すでに予定の情報があればここで値の代入
		Intent intent = getIntent();
		loadParams(intent);

	}

	/*
	 * Diagramの値を調べるメソッド
	 */

	private void checkRadioButtonAndSetTimeData(View v) {
		RadioGroup r = (RadioGroup) v.findViewById(R.id.time_radio_group);

		if (r.getCheckedRadioButtonId() == R.id.start_time) {
			strTime = getTimeFromDatePicker(v);
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy'年' MM'月' dd'日' HH'時' mm'分' ");
			TextView strTime_text = (TextView) findViewById(R.id.strTime_on_add_event);
			Log.v("yeaR", String.valueOf(strTime.getDay()));
			
			strTime_text.setText(df.format(strTime));
			Toast.makeText(this, "予定開始時刻を設定しました", Toast.LENGTH_SHORT).show();
		} else {
			finTime = getTimeFromDatePicker(v);
			TextView finTime_text = (TextView) findViewById(R.id.finTime_on_add_event);
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy'年' MM'月' dd'日' HH'時' mm'分' ");
			finTime_text.setText(df.format(finTime));
			Toast.makeText(this, "予定終了時刻を設定しました", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * @yokota時間設定メソッド
	 */

	public void setTime(View v) {
		LayoutInflater factory = LayoutInflater.from(getApplicationContext());
		final View date_picker = factory.inflate(R.layout.date_picker, null);
		Button deside = (Button) date_picker.findViewById(R.id.deside_date);
		TimePicker tp = (TimePicker) date_picker.findViewById(R.id.time_picker);
		tp.setIs24HourView(true);
		deside.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkRadioButtonAndSetTimeData(date_picker);

			}
		});

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setPositiveButton("戻る", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkRadioButtonAndSetTimeData(date_picker);
			}
		})

		.setTitle("時刻設定").setView(date_picker).show();

	}

	/*
	 * dateをゲットする。
	 */
	private Date getTimeFromDatePicker(View v) {
		DatePicker d = (DatePicker) v.findViewById(R.id.date_picker);
		TimePicker t = (TimePicker) v.findViewById(R.id.time_picker);
		Log.v("hoge",String.valueOf(d.getDayOfMonth()));
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.set(d.getYear(), d.getMonth(), d.getDayOfMonth(), t
				.getCurrentHour(), t.getCurrentMinute());
		return cal.getTime();
	}

	static final int START_LOCATION_REQUEST = 1;
	static final int GOAL_LOCATION_REQUEST = 2;
	static final int SEARCH_ROOT_REQUEST = 3;

	/*
	 * @yokota 場所決定ダイアグラムメソッド
	 */
	public void setLocation(View v) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View location_picker = factory.inflate(R.layout.location_picker,
				null);
		Button deside = (Button) location_picker
				.findViewById(R.id.deside_locaiton);
		deside.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RadioGroup r = (RadioGroup) location_picker
						.findViewById(R.id.locations_radio_group);

				if (r.getCheckedRadioButtonId() == R.id.start_location_radio) {
					Intent start = new Intent(AddEventActivity.this
							.getApplication(), SelectLocationActivity.class);
					startActivityForResult(start, START_LOCATION_REQUEST);

				} else {
					Intent finish = new Intent(AddEventActivity.this
							.getApplication(), SelectLocationActivity.class);
					startActivityForResult(finish, GOAL_LOCATION_REQUEST);
				}

			}
		});
		Button selectHome = (Button) location_picker
				.findViewById(R.id.select_home);
		selectHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// set home value
				RadioGroup r = (RadioGroup) location_picker
						.findViewById(R.id.locations_radio_group);

				if (r.getCheckedRadioButtonId() == R.id.start_location_radio) {
					strLocation = "35396012,139467530";
					TextView strText = (TextView) findViewById(R.id.start_place_add_event);
					strText.setText(strLocation);
					Toast.makeText(getApplicationContext(), "出発地点を自宅に設定しました",
							Toast.LENGTH_SHORT).show();
				} else {
					finLocation = "35396012,139467530";
					TextView finText = (TextView) findViewById(R.id.finish_place_add_event);
					finText.setText(finLocation);
					Toast.makeText(getApplicationContext(), "目的地を自宅に設定しました",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setPositiveButton("戻る", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				

			}
		});

		dialog.setNeutralButton("路線検索", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToSearchTransitActivity();
			}
		});
		dialog.setTitle("出発・目的地選択");
		dialog.setView(location_picker);

		dialog.show();
	}

	private void goToSearchTransitActivity() {
		/*
		 * 路線検索アクティビティへ
		 */

		getParams();
		String msg = checkContent();
		Log.v("msg",msg);
		if (msg.equals("")) {
			Intent intent = new Intent(AddEventActivity.this.getApplication(),
					SearchTransitActivity.class);

			String[] value = { String.valueOf(strTime.getYear()),
					String.valueOf(strTime.getMonth()),
					String.valueOf(strTime.getDate()),
					String.valueOf(strTime.getHours()),
					String.valueOf(strTime.getMinutes()), strLocation, finLocation };

			intent.putExtra("value", value);
			startActivityForResult(intent, SEARCH_ROOT_REQUEST);

		} else {
			AlertDialog.Builder a_dialog = new AlertDialog.Builder(this);
			a_dialog.setMessage(msg);
			a_dialog.setTitle("以下に未設定があります");
			a_dialog.setNegativeButton("戻る", null);
			a_dialog.show();
		}

	}

	// overrride
	protected void onStart() {
		super.onStart();
		Log.v("EXAMPLE", "onStart");
	}

	// overrride
	protected void onRestart() {
		super.onRestart();
		Log.v("EXAMPLE", "onRestart");
	}

	// overrride
	protected void onResume() {
		super.onResume();
		Log.v("EXAMPLE", "onResume");
	}

	// override
	protected void onPause() {
		super.onPause();
		Log.v("EXAMPLE", "onPause");
	}

	// override
	protected void onStop() {
		super.onPause();
		Log.v("EXAMPLE", "onStop");
	}

	// override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("EXAMPLE", "onDestroy");
	}

	private String checkContent() {
		boolean strTimeIsOk = (strTime != null);
		boolean finTimeIsOK = (finTime != null);
		boolean startLocationIsOK = (!strLocation.equals(""));
		boolean finishLocationIsOK = (!finLocation.equals(""));
		boolean titleIsOK = (!title.equals(""));
		String msg = "";
		if (!titleIsOK)
			msg += "・タイトル\n";
		if (!strTimeIsOk)
			msg += "・開始時間\n";
		if (!finTimeIsOK)
			msg += "・終了時間\n";
		if (!startLocationIsOK)
			msg += "・出発地点\n";
		if (!finishLocationIsOK)
			msg += "・目的地";
		return msg;
	}

	/**
	 * ＠よこた 予定を保存するメソッド
	 */

	public void desideSchedule(View v) {
		getParams();
		String msg = checkContent();
		if (!msg.equals("")) {
			Log.v("the girls front of me", "fuckkkkking");
			AlertDialog.Builder ad_b = new AlertDialog.Builder(this);
			ad_b.setMessage(msg).setTitle("以下に未入力があります").setNeutralButton("戻る",
					null).show();
		} else {
			saveParams();
			Intent intent;
			intent = new Intent(AddEventActivity.this.getApplication(),
					AgendaActivity.class);

			startActivity(intent);
			finish();

		}

	}

	public void setRoute(View v) {
		goToSearchTransitActivity();
	}

	/*
	 * @yokota 前画面に戻るメソッド
	 */
	public void goToPreviousActivity(View v) {
		AlertDialog.Builder ad_b = new AlertDialog.Builder(getApplicationContext());
		ad_b.setPositiveButton("決定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				((Activity) getApplicationContext()).finish();
			}
		});
		
		ad_b.setNegativeButton("戻る", null);
		ad_b.setMessage("入力データが消えますがよろしいですか？").show();
	}
	
	/**
	 * 呼び出し先アクティビティの返り値の取得
	 * 
	 * @override
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		SharedPreferences prefer = getSharedPreferences("PREVIOUS_RESULT",
				MODE_PRIVATE);
		if (requestCode == START_LOCATION_REQUEST && resultCode == RESULT_OK) {

			strLocation = prefer.getString(
					"jp.ac.keio.sfc.crew.dasaitama.geoPoint", "");
			TextView str_tex = (TextView) findViewById(R.id.start_place_add_event);
			str_tex.setText(strLocation);
			Toast.makeText(this, "出発地点を設定しました", Toast.LENGTH_SHORT).show();
		} else if (requestCode == GOAL_LOCATION_REQUEST
				&& resultCode == RESULT_OK) {

			finLocation = prefer.getString(
					"jp.ac.keio.sfc.crew.dasaitama.geoPoint", "");
			TextView fin_tex = (TextView) findViewById(R.id.finish_place_add_event);
			fin_tex.setText(finLocation);
			Toast.makeText(this, "目的地を設定しました", Toast.LENGTH_SHORT).show();

		} else if (requestCode == SEARCH_ROOT_REQUEST
				&& resultCode == RESULT_OK) {

			route = (DRoute) intent.getSerializableExtra("DRoute");
			Log.v("--------", "ggggggg");
			setRouteDetail();
			Toast.makeText(this, "経路を設定しました", Toast.LENGTH_SHORT).show();
		} else if (requestCode == SEARCH_ROOT_REQUEST
				&& resultCode == RESULT_CANCELED) {
			Toast.makeText(this, "出発時刻は設定されていません", Toast.LENGTH_SHORT).show();
		}
	}

	private void getParams() {
		EditText title_name = (EditText) findViewById(R.id.title_name);
		title = title_name.getText().toString();
		TextView strLocation_tex = (TextView) findViewById(R.id.start_place_add_event);
		strLocation = strLocation_tex.getText().toString();
		TextView finLocation_tex = (TextView) findViewById(R.id.finish_place_add_event);
		finLocation = finLocation_tex.getText().toString();
		Log.v("fin", finLocation);
	}

	public void saveParams() {
		DEventManager manager = DEventManager.getInstance(this);
		DEvent event = new DEvent();
		event.setId(Id);
		event.setBegin(strTime);
		event.setEnd(finTime);
		event.setTitle(title);
		if (route != null)
			event.setRoute(route);
		if (finLocation != null)
			event.setLoc(finLocation);
		manager.save(event);
		Toast.makeText(this, "予定を保存しました", Toast.LENGTH_SHORT);

	}

	private void setRouteDetail() {
		Date str = route.getStartTime();
		Date fin = route.getFinishTime();
		SimpleDateFormat df = new SimpleDateFormat("hh:mm");
		String strTime = String.valueOf(df.format(str));
		String finTime = String.valueOf(df.format(fin));
		long need_time = fin.getTime() - str.getTime();
		Date needTime = new Date(need_time);
		SimpleDateFormat neccesaryTimeFormat = new SimpleDateFormat(
				"hh'時間'mm'分'");
		TextView basicInfo = (TextView) findViewById(R.id.basic_info);
		basicInfo.setText(strTime + "→" + finTime + " / "
				+ neccesaryTimeFormat.format(needTime) + route.getBasicInfo());
	}

	/**
	 * 
	 */
	public void loadParams(Intent intent) {
		Id = intent.getIntExtra("jp.ac.keio.sfc.crew.dasaitama.event_id", 0);
		if (Id != 0) {
			Log.v("IDDDD", Id + "");
			DEventManager manager = DEventManager.getInstance(this);
			DEvent event = manager.findById(Id);
			title = event.getTitle();
			EditText title_name = (EditText) findViewById(R.id.title_name);
			title_name.setText(title);
			TextView activity_title = (TextView) findViewById(R.id.activity_title);
			activity_title.setText("予定編集");
			strTime = event.getBegin();
			TextView strTime_text = (TextView) findViewById(R.id.strTime_on_add_event);
			SimpleDateFormat df = new SimpleDateFormat(
					"yyyy'年' MM'月' DD'日' HH'時' mm'分' ");
			strTime_text.setText(df.format(strTime));

			finTime = event.getEnd();
			TextView finTime_text = (TextView) findViewById(R.id.finTime_on_add_event);
			finTime_text.setText(df.format(finTime));

			if (event.getRoute() != null) {
				strLocation = event.getRoute().getLocationTo(0);
				TextView str_tex = (TextView) findViewById(R.id.start_place_add_event);
				str_tex.setText(strLocation);

				finLocation = event.getRoute().getLocationTo(
						event.getRoute().getCount() - 1);
				TextView fin_tex = (TextView) findViewById(R.id.finish_place_add_event);
				fin_tex.setText(finLocation);

				route = event.getRoute();
				setRouteDetail();
			}
		}
	}
}