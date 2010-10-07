package jp.ac.keio.sfc.crew.dasaitama.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import jp.ac.keio.sfc.crew.dasaitama.activity.SearchTransitActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/*
 * googleTransit�ɐڑ����ă��X�|���X�𓾂�܂ł��s���N���X�B
 */
public class DSearch extends AsyncTask<Void, Void, ArrayList<DRoute>>{
	private SearchTransitActivity serchTransit;
	/*@yokota
	 * �N�G���[�����̂ɕK�v�ȕϐ������B
	 */
	private DSchedule sch;
	private DOption opt;
	private DParse par;
	long sta;
	/*
	 * constructer
	 */
	public DSearch(SearchTransitActivity act) {
		
		serchTransit = act;
	}
	/*
	 * �O�[�O���g�����W�b�g�ɐڑ����郁�\�b�h
	 */
	public void connectToGoogleTransit() throws Exception{
		HttpResponse resp = null;
	    HttpClient mClient = new DefaultHttpClient();  
	    HttpGet mGetMethod = new HttpGet();
	    URI uri = createURI();
	    mGetMethod.setURI(uri);
	    Log.v("uri",String.valueOf(uri));
    	resp = mClient.execute(mGetMethod);
 		responseToInputStream(resp);
 		
	}
	/*
	 * @yokota
	 * response-> inputStream
	 */
	private void responseToInputStream(HttpResponse r) throws Exception{
		if (r.getStatusLine().getStatusCode() == 200){  
			InputStream	is = r.getEntity().getContent();
			InputStreamToReadLine(is);
		}
	}

	/*@yokota
	 * �����Ȃǂ�ݒ肵�āA�N�G���[�𐶐�����
	 * �Ԃ�l:�N�G���[
	 */
	private URI createURI() {

		String http = "http://www.google.co.jp/transit?";
		ArrayList<String> conditionList = new ArrayList<String>();
		conditionList.add("saddr="+getStart()+"&");
		//�ړI�n�̐ݒ�
		conditionList.add("daddr="+getGoal()+"&");
		//���Ԃ̐ݒ�B���ƂŃ}�[�W���̎��Ԃ��Ƃ�K�v�����邯�ǂƂ肠�����B
		conditionList.add("time="+getTime()+"&");
		//��t�̎w��B
		conditionList.add("date="+getDate()+"&");
		//�����̐ݒ�Bdep�ŏo���Aarr�œ����Alast�ōŏI�d��
		conditionList.add("ttype=arr&");
		//�\�[�g���̎w��Bfare:��,time:����,num:���̎w��
		conditionList.add("sort="+opt.getSort()+"&");
		//���}�A��s�@�̎w�� �B1�ŏ��Ȃ��B
		conditionList.add("noexp=1&noal=1&");
		//�����R�[�h�A�A�E�g�v�b�g�`���A��or�d�Ԃ̎w��B���܂肩�񂯂��Ȃ������Ȃ̂ŁA�܂Ƃ߂āB
		conditionList.add("ie=UTF8&output=xhtml&f=d&dirmode=transit&num=5");
		//�o�Ă���H��̍ő吔�͂Ƃ肠����5�ɐݒ肵�Ă����B
		/*
		 * for���ɂ�����B
		 */
		for(int i=0;i<conditionList.size();i++){
			http += conditionList.get(i);
		}
		URI u = null;
		try {
			u =  new URI(http);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}
	/*@yokota
	 * Schedule�����������X�^�[�ƒn�_���N�G���ɕϊ����\�b�h�B
	 * latitude,longitude�ŕԂ��܂��B
	 */
	public String getStart(){
		double lat = Double.valueOf(sch.getStart().getLatitudeE6())/1E6;
		String lat_str = String.valueOf(lat);
		double longi = Double.valueOf(sch.getStart().getLongitudeE6())/1E6;
		String longi_str = String.valueOf(longi);
		return lat_str+","+longi_str;
	}
	/*@yokota
	 * schdule�N���X�̃S�[���n�_��ϊ�����B
	 * latitude,longitude�ŕԂ��܂��B
	 */
	
	public String getGoal(){
		double lat = Double.valueOf(sch.getGoal().getLatitudeE6())/1E6;
		String lat_str = String.valueOf(lat);
		double longi = Double.valueOf(sch.getGoal().getLongitudeE6())/1E6;
		String longi_str = String.valueOf(longi);
		return lat_str+","+longi_str;
	}

	private String getDate(){
		SimpleDateFormat format = new SimpleDateFormat("MMdd");
		String date = format.format(sch.getDate().getTime());
		return date;
	}
	private String getTime(){
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTime(sch.getDate().getTime());
		c.add(Calendar.MINUTE, -opt.getMargin());
		SimpleDateFormat format = new SimpleDateFormat("HHmm");
		String date = format.format(c.getTime());
		return date;
	}

	/*@yokota
	 * �I�v�V������ݒ肷�郁�\�b�h
	 */
	public void setOption(DOption o){
		opt = o; 
	}
	
	@Override
	protected void onPreExecute(){
		opt = serchTransit.checkOptions();
		sch = serchTransit.getSchedule();
	}
	@Override
	protected ArrayList<DRoute> doInBackground(Void... params){
			try {
				connectToGoogleTransit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return par.getRouteList();
	}
	
	@Override
	protected void onPostExecute(ArrayList<DRoute> routeList){
		serchTransit.showResult(routeList);
	}
	/*@yokota
	 * InputStream��htmlData�ɕϊ�����N���X�B
	 */
	private void InputStreamToReadLine(InputStream is) throws Exception{
		String html_data=null;
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis,"SHIFT-JIS"));
		String line;
		while((line=br.readLine())!=null){
			html_data+=line;
		}
		par = new DParse(html_data,sch.getDate());
	}
	@Override
	protected void onCancelled(){
		serchTransit.onDestroy();
		
	}


}