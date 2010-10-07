package jp.ac.keio.sfc.crew.dasaitama.model;

import java.util.Calendar;

import com.google.android.maps.GeoPoint;

/*
 * �O�[�O���g�����W�b�g���������邽�߂́A��������Ă����N���X�B
 *
 */
public class DSchedule {
	private GeoPoint start,goal;
	private Calendar cal;
	
	public DSchedule(GeoPoint s,GeoPoint g,Calendar c) {
		start =s;
		goal = g;
		cal = c;
	}
	/*
	 * @yokota
	 * �J�����_�[��Ԃ��N���X�B
	 */
	public Calendar getDate(){
		return cal;
	}
	/*
	 * @yokota
	 * �X�^�[�g�n�_��Ԃ�����
	 */
	public GeoPoint getStart(){
		return start;
	}
	/*
	 * @yokota
	 * �S�[���n�_��Ԃ�����
	 */
	public GeoPoint getGoal(){
		return goal;
	}
}
