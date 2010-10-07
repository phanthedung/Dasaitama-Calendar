package jp.ac.keio.sfc.crew.dasaitama.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.util.Log;

public class DRoute implements Serializable{
	/*
	 * �ϐ������B
	 */
	public static final int WALK = 0;
	public static final int TRAIN = 1;
	private ArrayList<Integer> walkOrTrain = new ArrayList<Integer>();
	private ArrayList<String> locationFrom = new ArrayList<String>();
	private ArrayList<String> timeTo = new ArrayList<String>();
	private ArrayList<String> locationTo = new ArrayList<String>();
	private ArrayList<String> timeFrom = new ArrayList<String>();
	private ArrayList<String> transfer = new ArrayList<String>();
	private String basicInfo;
	private Calendar cal;

	/*
	 * @yokota �R���X�g���N�^
	 */
	public DRoute(String basic_info, ArrayList<String> transit_info,
			ArrayList<String> location_from, ArrayList<String> location_to,
			ArrayList<String> time_from, ArrayList<String> time_to, Calendar cal) {
		this.basicInfo = basic_info;
		this.transfer = transit_info;
		this.locationFrom = location_from;
		this.locationTo = location_to;
		this.timeFrom = time_from;
		this.timeTo = time_to;
		this.cal = cal;
		long sta = System.currentTimeMillis();

		walkDataToTimeData();
		convertBasicInfo();
		long fin = System.currentTimeMillis();
		Log.v("DRoute", String.valueOf(fin - sta));
	}

	public DRoute(String basic_info, ArrayList<Integer> wark_or_train,
			ArrayList<String> transit_info, ArrayList<String> location_from,
			ArrayList<String> location_to, ArrayList<String> time_from,
			ArrayList<String> time_to, Calendar cal) {		
		this.walkOrTrain = wark_or_train;
		this.basicInfo = basic_info;
		this.transfer = transit_info;
		this.locationFrom = location_from;
		this.locationTo = location_to;
		this.timeFrom = time_from;
		this.timeTo = time_to;
		this.cal = cal;
	}

	/*
	 * @yokota �ړ���{�������Ԃ���菜�����\�b�h
	 */
	private void convertBasicInfo() {
		Pattern p = Pattern.compile("\\s");
		String[] info = p.split(basicInfo);
		if (info.length > 2) {
			basicInfo = info[1] + " " + info[2];
		}
	}

	public int getListSize() {
		return transfer.size();
	}

	public ArrayList<String> getTransitInfo() {
		return transfer;
	}

	/*
	 * @yokota Getter
	 */

	public String getBasicInfo() {
		return basicInfo;
	}

	/*
	 * @yokota �k���Ȃ̂����`�F�b�N����B�K�v�ł���΁A����u��������B
	 */

	private void walkDataToTimeData() {
		for (int change_line = 0; change_line < timeFrom.size(); change_line += 1) {
			String time = timeFrom.get(change_line);
			if (time.equals("")) {
				walkOrTrain.add(WALK);
				if (change_line + 1 < timeFrom.size()) {
					// ���̎���timeFrom������Ƃ��B
					timeTo.set(change_line, timeFrom.get(change_line + 1));
					// ���̌�A�g�����W�b�g��񂩂�擾����B
					String target = transfer.get(change_line);
					int min = getTimeFromTransitInfo(target);
					String walk = timeTo.get(change_line);
					walk = convertWalkDataToTime(walk, -min);
					timeFrom.set(change_line, walk);
				} else {
					timeFrom.set(change_line, timeTo.get(change_line - 1));
					// �g�����W�b�g��񂩂珈������B
					String target = transfer.get(change_line);
					int min = getTimeFromTransitInfo(target);
					String walk = timeFrom.get(change_line);
					walk = convertWalkDataToTime(walk, min);
					timeTo.set(change_line, walk);
				}
			} else {
				walkOrTrain.add(TRAIN);
			}
		}
	}

	/*
	 * @yokota �����Ƃ�����񂩂玞�ԏ��ɕϊ�����B
	 */
	private String convertWalkDataToTime(String s, int min) {
		Pattern p = Pattern.compile(":");
		String[] time = p.split(s);
		int hour = 0, mini = 0;
		hour = Integer.valueOf(time[0]);
		mini = Integer.valueOf(time[1]);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, mini);
		cal.add(Calendar.MINUTE, min);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(cal.getTime());
	}

	/*
	 * @yokota transitData��������̂ɕK�v�Ȏ��Ԃ�����o���B int �����̂ɕK�v�Ȏ���
	 */
	private int getTimeFromTransitInfo(String s) {
		Pattern hourPattern = Pattern.compile("(\\d{1,2})時");
		Pattern minitusPattern = Pattern.compile("(\\d{1,2})分");
		Matcher hourMatcher = hourPattern.matcher(s);
		Matcher minitusMatcher = minitusPattern.matcher(s);
		int hour = 0, min = 0;
		if (hourMatcher.find())
			hour = Integer.valueOf(hourMatcher.group(1));
		if (minitusMatcher.find())
			min = Integer.valueOf(minitusMatcher.group(1));
		return hour * 60 + min;
	}

	public String getTimeFrom(int i) {
		return timeFrom.get(i);
	}

	public String getTimeTo(int i) {
		return timeTo.get(i);
	}

	public String getLocationFrom(int i) {
		return locationFrom.get(i);
	}

	public String getLocationTo(int i) {
		return locationTo.get(i);
	}

	public String getTransitInfo(int i) {
		return transfer.get(i);
	}

	public int isTrainOrWalk(int i) {
		return walkOrTrain.get(i);
	}

	public Calendar getCarendar() {
		return this.cal;
	}

	public int getCount() {
		return walkOrTrain.size();
	}

	public Date getStartTime() {
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(cal.getTime());
		Pattern p = Pattern.compile(":");
		String[] time = p.split(timeFrom.get(0));
		startTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
		startTime.set(Calendar.MINUTE, Integer.valueOf(time[1]));
		return startTime.getTime();
	}

	public Date getFinishTime() {
		Calendar finTime = Calendar.getInstance();
		finTime.setTime(cal.getTime());
		Pattern p = Pattern.compile(":");
		String[] time = p.split(timeTo.get(timeTo.size() - 1));
		finTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(time[0]));
		finTime.set(Calendar.MINUTE, Integer.valueOf(time[1]));
		return finTime.getTime();
	}
	public Date getNeedTime(){
		long need_time = getFinishTime().getTime()-getStartTime().getTime();
		return new Date(need_time);
	}

}
