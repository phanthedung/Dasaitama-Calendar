package jp.ac.keio.sfc.crew.dasaitama;

import jp.ac.keio.sfc.crew.dasaitama.activity.AgendaActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenda);
		Intent intent = new Intent(Main.this.getApplication(),
				AgendaActivity.class);
		startActivity(intent);
	}
}