package jp.ac.keio.sfc.crew.dasaitama.model;

import java.util.Date;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class DEvent {

	private int id=0;
	private String title;
	private Date begin;
	private Date end;
	private GeoPoint loc;

	private Boolean allDay;

	private DRoute Droute;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLoc(String locString) {
		String st[]	= locString.split(",");
		this.loc = new GeoPoint(Integer.parseInt(st[0]),Integer.parseInt(st[1]));
		Log.v("GeoPoint", "lat "+st[0]+" long"+st[1]);
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public GeoPoint getLoc() {
		return loc;
	}

	public Boolean getAllDay() {
		return allDay;
	}

	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}

	public DRoute getRoute() {
		return Droute;
	}

	public void setRoute(DRoute Droute) {
		this.Droute = Droute;
	}
}
