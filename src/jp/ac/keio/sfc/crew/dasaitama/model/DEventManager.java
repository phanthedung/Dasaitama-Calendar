package jp.ac.keio.sfc.crew.dasaitama.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.maps.GeoPoint;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class DEventManager {
	private static DEventManager _instance;
	private static final String TAG = "DasaitamaCalendar";
	private static final int CALENDAR_ID = 1;
	public static final String AGENDA_SORT_ORDER = "startDay ASC, startMinute ASC";
	protected Context context;

	private DEventManager(Context context) {
		this.context = context;
	}

	public static DEventManager getInstance(Context context) {
		if (_instance == null) {
			_instance = new DEventManager(context);
		}
		return _instance;
	}

	public ArrayList<DEvent> find(long beginDate, long endDate) {

		ArrayList<DEvent> events = new ArrayList<DEvent>();
		Log.v("CarendarManager", "find ok");
		ContentResolver cr = this.context.getContentResolver();
		Log.v("CarendarManager", "cr ok");
		// Build request query.
		Uri.Builder builder = Uri.parse("content://calendar/instances/when")
				.buildUpon();
		ContentUris.appendId(builder, beginDate);
		ContentUris.appendId(builder, endDate);

		Cursor eventCursor = cr.query(builder.build(), new String[] {
				"event_id", "title", "begin", "end", "allDay", "description" },
				null, null, AGENDA_SORT_ORDER);
		Log.v("CarendarManager", "query ok");

		try {
			while (eventCursor.moveToNext()) {
				DEvent ev = new DEvent();
				ev.setId(eventCursor.getInt(0));
				ev.setTitle(eventCursor.getString(1));
				Log.v(TAG, "Title :" + ev.getTitle());
				ev.setBegin(new Date(eventCursor.getLong(2)));
				ev.setEnd(new Date(eventCursor.getLong(3)));
				ev.setAllDay(!eventCursor.getString(4).equals("0"));
				if (eventCursor.getString(5) == null) {
					events.add(ev);
					continue;
				}

				DRoute route = this.xmlToRoute(eventCursor.getString(5));
				ev.setRoute(route);
				events.add(ev);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return events;
	}

	public DEvent findById(int event_id) {
		ContentResolver cr = this.context.getContentResolver();
		Uri CALENDAR_URI = Uri.parse("content://calendar/events");
		Uri eventsUri = ContentUris.withAppendedId(CALENDAR_URI, event_id);

		Cursor eventCursor = cr.query(eventsUri, 
				(new String[] { "_id", "title", "dtstart", "dtend", "allDay", "description","eventLocation" }), 
				null, null, null);
		
		while (eventCursor.moveToNext()) {
			DEvent ev = new DEvent();
			ev.setId(eventCursor.getInt(0));
			Log.d("TAG", "_id = "+eventCursor.getInt(0));
			ev.setTitle(eventCursor.getString(1));
			ev.setBegin(new Date(eventCursor.getLong(2)));
			ev.setEnd(new Date(eventCursor.getLong(3)));
			ev.setAllDay(!eventCursor.getString(4).equals("0"));
			if (eventCursor.getString(6) != null) {
				ev.setLoc(eventCursor.getString(6));
			}
			if (eventCursor.getString(5) != null) {
				DRoute route = this.xmlToRoute(eventCursor.getString(5));
				ev.setRoute(route);
			}

			return ev;
		}
		return null;
	}

	public void save(DEvent event) {
		if (event.getId() != 0) {
			this.updateEvent(event);
		} else {
			this.insertEvent(event);
		}

	}

	private ContentValues convertToContentValues(DEvent event) {
		Log.v(TAG, "convertToContentValues");
		if (event == null)
			return null;
		ContentValues ev = new ContentValues();
		ev.put("calendar_id", CALENDAR_ID);
		ev.put("title", event.getTitle());
		if (event.getRoute() != null)
			ev.put("description", this.routeToXml(event.getRoute()));
		ev.put("eventLocation", event.getLoc().getLatitudeE6()+","
								+event.getLoc().getLongitudeE6());
		ev.put("dtstart", event.getBegin().getTime());
		ev.put("dtend", event.getEnd().getTime());
		ev.put("allDay", event.getAllDay());
		return ev;
	}

	private void insertEvent(DEvent event) {
		Log.v(TAG, "insertEvent");
		// TODO Auto-generated method stub
		ContentValues ev = convertToContentValues(event);
		Uri eventsUri = Uri.parse("content://calendar/events");
		Log.v(TAG, "Inserting" + ev.toString());
		this.context.getContentResolver().insert(eventsUri, ev);
	}

	private void updateEvent(DEvent event) {
		// TODO Auto-generated method stub
		/*
		Log.v(TAG, "updateEvent id ="+event.getId());
		ContentValues ev = convertToContentValues(event);
		Uri CALENDAR_URI = Uri.parse("content://calendar/events");
		Uri eventsUri = ContentUris.withAppendedId(CALENDAR_URI, event.getId());
		Log.v("CarendarManager", "Updating");
		this.context.getContentResolver().update(eventsUri, ev, null, null);
		Log.v("CarendarManager", "Updated");*/

		 this.deleteEventById(event.getId()); 
		 event.setId(0);
		 this.save(event);

	}

	public void deleteEventById(int event_id) {
		// TODO Auto-generated method stub
		Log.v("CarendarManager", "Going to delete");
		Uri CALENDAR_URI = Uri.parse("content://calendar/events");
		ContentResolver cr = this.context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(CALENDAR_URI, event_id);
		cr.delete(uri, null, null);
		Log.v("CarendarManager", "Deleted");
		// this.context.getContentResolver().update(eventsUri, ev,
		// "_id="+event.getId(), null);

	}

	private String routeToXml(DRoute r) {
		String xml = "";
		xml += "<Route " + "date=\""
				+ String.valueOf(r.getCarendar().getTime().getTime()) + "\" "
				+ "basicInfo=\"" + r.getBasicInfo()	+"\" >";

		int count = r.getCount();
		for (int i = 0; i < count; i++) {
			xml += "<Element " + "walkOrTrain=\"" + r.isTrainOrWalk(i) + "\" "
					+ "locationFrom=\"" + r.getLocationFrom(i) + "\" "
					+ "timeFrom=\"" + r.getTimeFrom(i) + "\" "
					+ "locationTo=\"" + r.getLocationTo(i) + "\" "
					+ "timeTo=\"" + r.getTimeTo(i) + "\" " + "transfer=\""
					+ r.getTransitInfo(i) + "\" />";
		}
		xml += "</Route>";
		return xml;
	}

	private DRoute xmlToRoute(String xml) {
		if (xml.length() < 1) {
			Log.v(TAG, "No Route Xml");
			return null;
		}

		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		String basicInfo = "";
		ArrayList<Integer> warkOrTrain = new ArrayList<Integer>();
		ArrayList<String> locationFrom, timeFrom, locationTo, timeTo, transfer;
		locationFrom = new ArrayList<String>();
		timeFrom = new ArrayList<String>();
		locationTo = new ArrayList<String>();
		timeTo = new ArrayList<String>();
		transfer = new ArrayList<String>();

		try {
			final XmlPullParserFactory factory = XmlPullParserFactory
					.newInstance();
			final XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xml));

			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				if (type == XmlPullParser.START_TAG) {
					String tag = parser.getName();
					if (tag.equals("Route")) {
						cal.setTimeInMillis(Long.parseLong(parser
								.getAttributeValue(0)));
						basicInfo = parser.getAttributeValue(1);
					} else if (tag.equals("Element")) {
						warkOrTrain
								.add(parser.getAttributeValue(0).equals("0") ? 0
										: 1);
						locationFrom.add(parser.getAttributeValue(1));
						timeFrom.add(parser.getAttributeValue(2));
						locationTo.add(parser.getAttributeValue(3));
						timeTo.add(parser.getAttributeValue(4));
						transfer.add(parser.getAttributeValue(5));
					}
				}
				type = parser.next();
			}	
			return new DRoute(basicInfo, warkOrTrain, transfer, locationFrom,
					locationTo, timeFrom, timeTo, cal);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}