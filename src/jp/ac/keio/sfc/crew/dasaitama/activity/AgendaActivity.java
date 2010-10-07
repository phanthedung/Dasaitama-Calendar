package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DEvent;
import jp.ac.keio.sfc.crew.dasaitama.model.DEventManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AgendaActivity extends Activity {

	List<HashMap<String, String>> list;
	private ArrayList<DEvent> eventList = new ArrayList<DEvent>();
	private Date focusDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenda);
		//Get focusDate
		long time = this.getIntent().getLongExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", 0);
		if (time != 0){
			focusDate = new Date(time);
			focusDate.setHours(0);
			focusDate.setMinutes(0);
		}else{
			focusDate = new Date();
		}
		DEventManager cm = DEventManager.getInstance(this);
		//Log.v("CarendarManager", "Get instance ok");
		eventList = cm.find(focusDate.getTime(), focusDate.getTime() + DateUtils.WEEK_IN_MILLIS);

		//Log.v("CarendarManager", "event size:" + eventList.size());

		eventList = sortEvent(eventList);
		ArrayList<DayEvent> dayEventList =  sortDayEvent(eventList);
		
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("00");
		createListView(dayEventList);
	}

	private ArrayList<DayEvent> sortDayEvent(ArrayList<DEvent> eventList) {
		ArrayList<DayEvent> dayEventList = new ArrayList<DayEvent>();
		Date date = null;
		
		ArrayList<DEvent> tmpEvents = null;
		for (DEvent event : eventList) {
			Date begin = event.getBegin();
			int year = begin.getYear();
			int month = begin.getMonth();
			int day = begin.getDate();
			Date d = new Date(year, month, day);
			
			if (date == null) {
				date = d;
				tmpEvents = new ArrayList<DEvent>();
				tmpEvents.add(event);
			} else {
				if (d.compareTo(date) == 0) {
					tmpEvents.add(event);
				} else {
					DayEvent de = new DayEvent();
					de.date = date;
					de.events = tmpEvents;
					dayEventList.add(de);
					
					date = d;
					tmpEvents = new ArrayList<DEvent>();
					tmpEvents.add(event);
				}
			}
		}
		return dayEventList;
	}

	private ArrayList<DEvent> sortEvent(ArrayList<DEvent> eventList) {
		int count;
		DEvent temp;
		do {
			
			count = 0;
			for (int i = eventList.size() - 1; i > 0; i--) {
				if (eventList.get(i).getBegin().getTime() < eventList
						.get(i - 1).getBegin().getTime()) {
					temp = eventList.get(i);
					eventList.set(i, eventList.get(i - 1));
					eventList.set(i - 1, temp);
					count++;
				}
			}
		} while (count != 0);
		return eventList;
	}

	private class DayEvent {
		Date date;
		ArrayList<DEvent> events;
	}


	/*
	 * @yokota ListViewを作り出す
	 */
	private void createListView(final ArrayList<DayEvent> eventList) {
		ListView listView = (ListView) findViewById(R.id.day_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplication(), R.layout.day_row, R.id.day) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				v = setAgenda(v, eventList.get(position));
				return v;
			}
		};
		for (int i = 0; i < eventList.size(); i += 1) {
			//ひづけ
			String dateString = DateFormat.format("yyyy/MM/dd", eventList.get(i).date).toString();
			
			adapter.add(dateString);
		}
		listView.setAdapter(adapter);
	}

	private View setAgenda(View v, DayEvent dayEvent) {
		LinearLayout day_row = (LinearLayout) v.findViewById(R.id.day_row);
		day_row.removeAllViews();

		for (int i = 0; i < dayEvent.events.size(); i++) {
			DEvent e = dayEvent.events.get(i);
			LayoutInflater factory = LayoutInflater.from(this);
			View agenda = factory.inflate(R.layout.agenda_row, null);
			TextView title = (TextView) agenda.findViewById(R.id.title);
			String title_tex = e.getTitle();
			title.setText(title_tex);
			TextView time = (TextView) agenda.findViewById(R.id.time);
			SimpleDateFormat df = new SimpleDateFormat("yyyy'/'MM'/'dd'/'HH':'mm ");
			time.setText(df.format(e.getBegin()));
			TextView hiddenId = (TextView) agenda.findViewById(R.id.hiddenId);
			hiddenId.setText(e.getId() + "");
			agenda.setOnClickListener(agendaClickListener);
			day_row.addView(agenda);
		}
		return v;
	}

	View.OnClickListener agendaClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(AgendaActivity.this.getApplication(),
					EventDetailActivity.class);			
			int hiddenId = Integer.parseInt(((TextView) v.findViewById(R.id.hiddenId)).getText().toString());
			//Log.d("ID", "hiddenId = "+hiddenId);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.eventId", hiddenId);
			startActivity(intent);
		}

	};
	

	/**
	 * Setup menus for this page
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, "日表示");
		menu.add(Menu.NONE, 1, Menu.NONE, "月表示");
		menu.add(Menu.NONE, 2, Menu.NONE, "予定追加");
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
			intent = new Intent(AgendaActivity.this.getApplication(),
					DayActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		case 1:
			intent = new Intent(AgendaActivity.this.getApplication(),
					MonthActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		case 2:
			intent = new Intent(AgendaActivity.this.getApplication(),
					AddEventActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		}
		return false;
	}


}
