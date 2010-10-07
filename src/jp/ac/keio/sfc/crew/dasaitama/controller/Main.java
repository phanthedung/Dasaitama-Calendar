package jp.ac.keio.sfc.crew.dasaitama.controller;

import jp.ac.keio.sfc.crew.dasaitama.R;
import jp.ac.keio.sfc.crew.dasaitama.activity.AddEventActivity;
import jp.ac.keio.sfc.crew.dasaitama.activity.AgendaActivity;
import jp.ac.keio.sfc.crew.dasaitama.activity.SearchTransitActivity;
import jp.ac.keio.sfc.crew.dasaitama.model.DRoute;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	public final static int FROM_AGENDA = 0;
	public final static int FROM_MONTH = 1;
	public final static int FROM_DAY = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getIntent()!=null){
			Intent intent = getIntent();
		}else{
			Intent intent = new Intent(Main.this.getApplication(),
					AgendaActivity.class);
		startActivity(intent);
		}
			
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
		if(requestCode==FROM_AGENDA){

		}else if(requestCode == FROM_DAY){
			startActivityForResult(intent,FROM_DAY);
		}else if(requestCode== FROM_MONTH){
			startActivityForResult(intent, FROM_MONTH);
		}
		
	}

	public void goToAddEventActivity(int flag) {

		
	}
}