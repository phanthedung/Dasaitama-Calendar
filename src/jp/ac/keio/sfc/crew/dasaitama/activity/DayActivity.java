package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.util.ArrayList;
import java.util.Date;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DEvent;
import jp.ac.keio.sfc.crew.dasaitama.model.DEventManager;
import jp.ac.keio.sfc.crew.dasaitama.model.DRoute;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsoluteLayout.LayoutParams;

public class DayActivity extends Activity {
	private String TAG = "DasaitamaCalendar";
	private ArrayList<Button> btnList = new ArrayList<Button>();
	private ArrayList<DEvent> eventList = new ArrayList<DEvent>();
	private ArrayList<DEvent> allDayEventList = new ArrayList<DEvent>();
	private int LAYOUT_WIDTH = 294;
	private Date focusDate;
	private String[] DoW = { "日", "月", "火", "水", "木", "金", "土" };
	@SuppressWarnings("deprecation")
	private AbsoluteLayout eventLayout;
	private LinearLayout allDayLayout;
	private ScrollView scrollView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_view);

		// focusDateを決定
		long time = this.getIntent()
							.getLongExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", 0);

		if (time != 0) {
			focusDate = new Date(time);
		} else {
			focusDate = new Date();
		}
		// イベントを表示
		updateEventList(focusDate);		
		
		eventLayout = (AbsoluteLayout) findViewById(R.id.eventLayout);
		allDayLayout = (LinearLayout) findViewById(R.id.allDayEvents);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		((ImageButton) findViewById(R.id.btnPrevDay))
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View view) {
						focusDate.setDate(focusDate.getDate() - 1);
						updateEventList(focusDate);
						drawEventList(focusDate);
					}
				});

		((ImageButton) findViewById(R.id.btnNextDay))
				.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View view) {
						focusDate.setDate(focusDate.getDate() + 1);
						updateEventList(focusDate);
						drawEventList(focusDate);
					}
				});
		drawEventList(focusDate);
	};

	// イベントを表示
	private void drawEventList(final Date date) {
		
		// 今の時刻にScrollする
		scrollView.smoothScrollTo(0, timeToPos(focusDate, focusDate));
		// 前のCachedを削除
		eventLayout.removeAllViewsInLayout();
		allDayLayout.removeAllViewsInLayout();
		// 日付のタイトル
		((TextView) findViewById(R.id.tvDateTitle)).setText(
				(1900 + date.getYear())	+ "年"
				+ (date.getMonth() + 1)	+ "月"
				+ date.getDate()+ "日     "
				+ DoW[date.getDay()] + "曜日");

		Log.v(TAG, String.valueOf(eventList.size()));

		// Draw allday events
		for (final DEvent ev : allDayEventList) {
			android.widget.LinearLayout.LayoutParams p;
			p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			
			p.setMargins(2, 2, 2, 2);
			Button btn = new Button(this);
			btn.setText(ev.getTitle()+" (終日)");
			btn.setBackgroundResource(R.drawable.location_back);
			allDayLayout.addView(btn, p);
			btn.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(DayActivity.this
							.getApplication(), EventDetailActivity.class);
					intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.eventId", ev
							.getId());
					startActivity(intent);
				}
			});
		}

		for (int k = 0; k < eventList.size(); ++k) {
			final DEvent ev = eventList.get(k);
			/*
			 * route = null -> death
			 */

			// Log.v(TAG, ev.getRoute().getBasicInfo());
			int left = getNumberOfPrevConflictEvent(k);
			int right = getNumberOfNextConflictEvent(k);

			// Log.v(TAG, String.valueOf(left));
			// Log.v(TAG, String.valueOf(right));

			if (ev.getRoute() != null) {
				Log.v(TAG, "hogeabababababaaaaa");
				if (isSameDate(focusDate, ev.getRoute().getFinishTime())
						|| isSameDate(focusDate, ev.getBegin())) {
					Log.v("Start", ev.getRoute().getStartTime().toString());
					Log.v("End", ev.getRoute().getFinishTime().toString());
					AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(
							LAYOUT_WIDTH / (left + right + 1),
							durationToHeight(focusDate, ev.getRoute()
									.getStartTime(), ev.getRoute()
									.getFinishTime()) + 5, LAYOUT_WIDTH * left
									/ (left + right + 1), timeToPos(focusDate,
									ev.getRoute().getStartTime()));

					Button btn = new Button(this);

					btn.setBackgroundResource(R.drawable.moving);
					btn.setText("移動中  "
							+ ev.getRoute().getStartTime().getHours() + ":"
							+ ev.getRoute().getStartTime().getMinutes());
					
					btn.setGravity(android.view.Gravity.TOP);
					eventLayout.addView(btn, p);
					btn.setGravity(android.view.Gravity.TOP);
					btn.setOnClickListener(new Button.OnClickListener() {
						@Override
						public void onClick(View view) {
							// Toast.makeText(getBaseContext(),
							// "予定" + ev.getId() + "の経路案内画面を開く",
							// Toast.LENGTH_SHORT).show();
							// dialog
							
							
							showRouteDetail(ev.getRoute());
						}
					});
				}
			}
			if (isSameDate(focusDate, ev.getBegin())
					|| isSameDate(focusDate, ev.getEnd())) {
				AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(
						LAYOUT_WIDTH / (left + right + 1), durationToHeight(
								focusDate, ev.getBegin(), ev.getEnd()) + 10,
						LAYOUT_WIDTH * left / (left + right + 1), timeToPos(
								focusDate, ev.getBegin()));
				Button btn = new Button(this);
				btn.setBackgroundResource(R.drawable.schedule);
				btn.setText(ev.getTitle());
				btn.setGravity(android.view.Gravity.TOP);
				eventLayout.addView(btn, p);
				btn.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(DayActivity.this
								.getApplication(), EventDetailActivity.class);
						intent.putExtra(
								"jp.ac.keio.sfc.crew.dasaitama.eventId", ev
										.getId());
						startActivity(intent);
					}
				});
			}

		}
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

	private int getNumberOfNextConflictEvent(int k) {
		// TODO Auto-generated method stub
		int count = 0;
		// while ((k < eventList.size() - 1)
		// && eventList.get(k).getEnd().after(
		// eventList.get(k + 1).getRoute().getStartTime())) {
		// ++k;
		// ++count;
		// }
		while ((k < eventList.size() - 1)) {
			//Log.v(TAG, eventList.get(k + 1).getRoute().toString());
			if (eventList.get(k + 1).getRoute() == null) {
				if (eventList.get(k).getEnd().after(
						eventList.get(k + 1).getBegin())) {
					++count;
				} else break;
			} else if (eventList.get(k).getEnd().after(
					eventList.get(k + 1).getRoute().getStartTime())) {

				++count;
			} else break;
			k++;
		}
		return count;
}

	private int getNumberOfPrevConflictEvent(int k) {
		// TODO Auto-generated method stub
		int count = 0;
		// while ((k > 0)
		// && eventList.get(k - 1).getEnd().after(
		// eventList.get(k).getRoute().getStartTime())) {
		// --k;
		// ++count;
		// }
		while (k > 0) {
			if (eventList.get(k).getRoute() == null) {
				if (eventList.get(k - 1).getEnd().after(
						eventList.get(k).getBegin())) {
					++count;
				} else break;
			} else if (eventList.get(k - 1).getEnd().after(
					eventList.get(k).getRoute().getStartTime())) {
				++count;
			} else break;
			k--;
		}
		return count;
	}

	private int timeToPos(Date currDate, Date startDate) {
		// TODO Auto-generated method stub
		if (isSameDate(currDate, startDate))
			return startDate.getHours() * 60 + startDate.getMinutes();
		else {
			Log.v("curr", currDate.toString());
			Log.v("startd", startDate.toString());
			Log.v("WTF", "FFFF");
			return 0;
		}
	}

	private boolean isSameDate(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return (date1.getMonth() == date2.getMonth() && (date1.getDate() == date2
				.getDate()));
	}

	private int durationToHeight(Date currDate, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		Date curr = (Date) currDate.clone(), start = (Date) startDate.clone(), end = (Date) endDate
				.clone();
		start.setDate(start.getDate() + 1);
		if (isSameDate(curr, start)) {
			start.setHours(0);
			start.setMinutes(0);
		}
		;

		end.setDate(end.getDate() - 1);
		if (isSameDate(curr, end)) {
			end.setHours(23);
			end.setMinutes(59);
		}
		;
		int height = (int) ((end.getHours() - start.getHours()) * 60 + (end
				.getMinutes() - start.getMinutes()));
		return height;
	}

	public void updateEventList(Date d) {
		ArrayList<DEvent> tempList;
		Date begin = new Date(d.getTime());
		begin.setHours(0);
		begin.setMinutes(0);
		begin.setSeconds(0);
		Log.v(TAG, begin.toString());
		Log.v(TAG, new Date(begin.getTime() + DateUtils.DAY_IN_MILLIS)
				.toString());
		DEventManager dm = DEventManager.getInstance(this);
		tempList = dm.find(begin.getTime(), begin.getTime()
				+ DateUtils.DAY_IN_MILLIS);
		eventList.clear();
		allDayEventList.clear();
		for (DEvent ev : tempList) {
			if (ev.getAllDay()) {
				if (onDate(ev, d))
					allDayEventList.add(ev);
			} else
				eventList.add(ev);
		}
	}

	private boolean onDate(DEvent ev, Date date) {
		// TODO Auto-generated method stub
		Date d = (Date) date.clone();
		d.setHours(23);
		d.setMinutes(59);
		return ev.getBegin().before(d) && ev.getEnd().after(d);
	}

	/**
	 * Setup menus for this page
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "予定リスト");
		menu.add(Menu.NONE, 1, Menu.NONE, "月一覧");
		menu.add(Menu.NONE, 2, Menu.NONE, "今日");
		menu.add(Menu.NONE, 3, Menu.NONE, "追加");
		return supRetVal;
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case 0:
			intent = new Intent(DayActivity.this.getApplication(),
					AgendaActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate",
					focusDate.getTime());
			startActivity(intent);
			return true;

		case 1:
			intent = new Intent(DayActivity.this.getApplication(),
					MonthActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate",
					focusDate.getTime());
			startActivity(intent);
			return true;
		case 2:
			focusDate = new Date();
			drawEventList(focusDate);
			return true;
		case 3:
			intent = new Intent(DayActivity.this.getApplication(),
					AddEventActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate",
					focusDate.getTime());
			startActivity(intent);
			return true;
		}
		return false;
	}

}
