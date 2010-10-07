package jp.ac.keio.sfc.crew.dasaitama.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.model.DEvent;
import jp.ac.keio.sfc.crew.dasaitama.model.DEventManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MonthActivity extends Activity {
	/*-------------------------------------------------------------------------
	 * �ϐ��錾
	 *-------------------------------------------------------------------------*/

	private final ArrayList<Button> btnList = new ArrayList<Button>();
	private Date focusDate;
	private float beforeY = 0, afterY = 0;
	private TableLayout tblView;
	private LinearLayout evLayout;
	private Drawable btnResorce;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month);

		// focusDate������
		long time = this.getIntent().getLongExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", 0);
		if (time != 0)
			focusDate = new Date(time);
		else
			focusDate = new Date();

		// ListView��tableView�̎擾
		ListView list = (ListView) findViewById(R.id.dayEventList);
		tblView = (TableLayout) findViewById(R.id.tblView);

		// �{�^����ݒ肷��
		buttonSetup();

		// �O���Ɉړ�����
		((ImageButton) findViewById(R.id.btnLeft))
		.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				// GUI�̏C��
				int target = focusDate.getDate()
				+ getFirstDay(focusDate) - 1;
				btnList.get(target).setBackgroundResource(
						R.drawable.day_1);
				btnList.get(target).setTextColor(
						getWeekDayColor(target));
				focusDate.setMonth(focusDate.getMonth() - 1);

				// �\��
				drawCalendar(focusDate);
			}
		});

		// �����Ɉړ�����
		((ImageButton) findViewById(R.id.btnRight))
		.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {
				// GUI�̏C��
				int target = focusDate.getDate()
				+ getFirstDay(focusDate) - 1;
				btnList.get(target).setBackgroundDrawable(btnResorce);
				btnList.get(target).setTextColor(
						getWeekDayColor(target));
				focusDate.setMonth(focusDate.getMonth() + 1);

				// �\��
				drawCalendar(focusDate);
			}
		});

		// �J�����_�[�\��
		drawCalendar(focusDate);

	};

	private void buttonSetup() {
		// TODO Auto-generated method stub
		btnList.add((Button) findViewById(R.id.btn00));
		btnList.add((Button) findViewById(R.id.btn01));
		btnList.add((Button) findViewById(R.id.btn02));
		btnList.add((Button) findViewById(R.id.btn03));
		btnList.add((Button) findViewById(R.id.btn04));
		btnList.add((Button) findViewById(R.id.btn05));
		btnList.add((Button) findViewById(R.id.btn06));
		btnList.add((Button) findViewById(R.id.btn10));
		btnList.add((Button) findViewById(R.id.btn11));
		btnList.add((Button) findViewById(R.id.btn12));
		btnList.add((Button) findViewById(R.id.btn13));
		btnList.add((Button) findViewById(R.id.btn14));
		btnList.add((Button) findViewById(R.id.btn15));
		btnList.add((Button) findViewById(R.id.btn16));
		btnList.add((Button) findViewById(R.id.btn20));
		btnList.add((Button) findViewById(R.id.btn21));
		btnList.add((Button) findViewById(R.id.btn22));
		btnList.add((Button) findViewById(R.id.btn23));
		btnList.add((Button) findViewById(R.id.btn24));
		btnList.add((Button) findViewById(R.id.btn25));
		btnList.add((Button) findViewById(R.id.btn26));
		btnList.add((Button) findViewById(R.id.btn30));
		btnList.add((Button) findViewById(R.id.btn31));
		btnList.add((Button) findViewById(R.id.btn32));
		btnList.add((Button) findViewById(R.id.btn33));
		btnList.add((Button) findViewById(R.id.btn34));
		btnList.add((Button) findViewById(R.id.btn35));
		btnList.add((Button) findViewById(R.id.btn36));
		btnList.add((Button) findViewById(R.id.btn40));
		btnList.add((Button) findViewById(R.id.btn41));
		btnList.add((Button) findViewById(R.id.btn42));
		btnList.add((Button) findViewById(R.id.btn43));
		btnList.add((Button) findViewById(R.id.btn44));
		btnList.add((Button) findViewById(R.id.btn45));
		btnList.add((Button) findViewById(R.id.btn46));
		btnList.add((Button) findViewById(R.id.btn50));
		btnList.add((Button) findViewById(R.id.btn51));
		btnList.add((Button) findViewById(R.id.btn52));
		btnList.add((Button) findViewById(R.id.btn53));
		btnList.add((Button) findViewById(R.id.btn54));
		btnList.add((Button) findViewById(R.id.btn55));
		btnList.add((Button) findViewById(R.id.btn56));
	}

	/*
	 * ����1��͉��j��𒲂ׂ�
	 */
	private int getFirstDay(Date date) {
		// TODO Auto-generated method stub
		Date firstDate = new Date(date.getYear(), date.getMonth(), 1);
		return firstDate.getDay();
	}

	/*
	 * �J�����_�[�̌��\��
	 */
	private void drawCalendar(final Date date) {
		// GUI�̏C��
		int target = focusDate.getDate() + getFirstDay(focusDate) - 1;
		btnList.get(target).setTextColor(Color.WHITE);
		btnResorce = btnList.get(target).getBackground();
		btnList.get(target).setBackgroundResource(R.drawable.on_clicked);

		// focusDate�̗\�胊�X�g�\��
		drawList();

		// ���\���̃^�C�g��
		((TextView) findViewById(R.id.tvMonthTitle)).setText((1900 + focusDate
				.getYear())
				+ "年" + (focusDate.getMonth() + 1) + "月");
		Calendar cal = new GregorianCalendar(focusDate.getYear() + 1900,
				focusDate.getMonth(), focusDate.getDate());

		// ��{�^���N���b�N�̏���
		for (int k = 0; k < 42; ++k) {
			if ((k >= getFirstDay(focusDate))
					&& (k < getFirstDay(focusDate)
							+ cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
				btnList.get(k).setText("" + (k - getFirstDay(focusDate) + 1));
				btnList.get(k).setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View view) {
						int target = focusDate.getDate()
						+ getFirstDay(focusDate) - 1;
						btnList.get(target).setBackgroundDrawable(btnResorce);

						btnList.get(target).setTextColor(
								getWeekDayColor(target));

						focusDate.setDate(Integer.parseInt(((Button) view)
								.getText().toString()));
						((Button) view).setTextColor(Color.WHITE);
						btnResorce = view.getBackground();
						((Button) view)
						.setBackgroundResource(R.drawable.on_clicked);
						drawList();
					}
				});
			} else
				btnList.get(k).setText("");

		}

	}

	/*
	 * @yokota �T���Ƃ̐F��Ԃ��܂��B
	 */
	private int getWeekDayColor(int target) {
		int beforeColor;
		if (target % 7 == 0) {
			beforeColor = Color.RED;
		} else if (target % 7 == 6) {
			beforeColor = Color.BLUE;
		} else {
			beforeColor = Color.BLACK;
		}
		return beforeColor;
	}

	protected void drawList() {
		ListView list = (ListView) findViewById(R.id.dayEventList);

		ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		Date begin = new Date(focusDate.getTime());
		begin.setHours(0);
		begin.setMinutes(0);
		begin.setSeconds(0);


		DEventManager dem = DEventManager.getInstance(this);
		ArrayList<DEvent> eventList = dem.find(begin.getTime(), begin.getTime() + DateUtils.DAY_IN_MILLIS);


		// TODO Auto-generated method stub
		for (final DEvent ev : eventList) {
			map = new HashMap<String, String>();
			SimpleDateFormat df = new SimpleDateFormat("HH':' mm ");
			map.put("evStartTime", df.format(ev.getBegin()));
			map.put("evTitle", ev.getTitle());
			map.put("evHiddenId", "" + ev.getId());
			mylist.add(map);
		}

		ListAdapter listAdapter = new SimpleAdapter(this, mylist,
				R.layout.dayeventrow, new String[] { "evStartTime", "evTitle",
				 "evHiddenId" }, new int[] { R.id.evStartTime,
				R.id.evTitle, R.id.evHiddenId });
		list.setAdapter(listAdapter);

		list.setOnItemClickListener(new MyClickAdapter());

	}

	// �C�x���g�N���X
	class MyClickAdapter implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position,long id) {
			Intent intent = new Intent(MonthActivity.this.getApplication(),
					EventDetailActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.eventId",
					Integer.parseInt(((TextView) view.findViewById(R.id.evHiddenId)).getText().toString()));
			startActivity(intent);
			//			Toast.makeText(getBaseContext(),
			//					((TextView) view.findViewById(R.id.evHiddenId)).getText().toString(),
			//					Toast.LENGTH_LONG).show();

		}
	}

	private boolean isSameDate(Date date1, Date date2) {
		// TODO Auto-generated method stub
		return (date1.getMonth() == date2.getMonth() && (date1.getDate() == date2
				.getDate()));
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
		menu.add(Menu.NONE, 1, Menu.NONE, "日表示");
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
			intent = new Intent(MonthActivity.this.getApplication(),
					AgendaActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		case 1:
			intent = new Intent(MonthActivity.this.getApplication(),
					DayActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		case 2:
			intent = new Intent(MonthActivity.this.getApplication(),
					AddEventActivity.class);
			intent.putExtra("jp.ac.keio.sfc.crew.dasaitama.focusDate", focusDate.getTime());
			startActivity(intent);
			return true;
		}
		return false;
	}

}
