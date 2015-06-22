package com.ztx.ballmove;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//此类为启动页activity
public class SloganAcdtivity extends Activity {
	private TextView yourbest, played, about;
	private Button beginButton;
	SharedPreferences sf;
	Editor editor;
	static boolean isHard = false;
	private ImageView yes, no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_main);

		setContentView(R.layout.slogan_xml);
		sf = this.getSharedPreferences("best_score", MODE_PRIVATE);
		int bestScore = sf.getInt("best", 0);
		int dies = sf.getInt("dies", 0);
		editor = sf.edit();
		played = (TextView) findViewById(R.id.played);
		yourbest = (TextView) findViewById(R.id.yourBest);
		about = (TextView) findViewById(R.id.about);
		played.setText(dies + "");
		yourbest.setText(bestScore + "");
		yes = (ImageView) findViewById(R.id.yes);
		no = (ImageView) findViewById(R.id.no);
		yes.setVisibility(View.GONE);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editor.putBoolean("ishard", false);
				editor.commit();
				isHard = false;
				no.setVisibility(View.VISIBLE);
				yes.setVisibility(View.GONE);
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isHard = true;
				editor.putBoolean("ishard", true);
				editor.commit();
				no.setVisibility(View.GONE);
				yes.setVisibility(View.VISIBLE);
			}
		});

		CloseAllActivities.getInstance().addActivity(this);
		beginButton = (Button) findViewById(R.id.beginGame);
		beginButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					beginButton.setBackgroundColor(Color.parseColor("#EE2C2C"));
					break;

				case MotionEvent.ACTION_UP:
					beginButton.setBackgroundColor(Color.parseColor("#EE0000"));
					break;
				}

				return false;
			}
		});
		beginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SloganAcdtivity.this,
						MainActivity.class);
				startActivity(intent);

			}
		});
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SloganAcdtivity.this, About.class);
				// intent.putExtra("ishard", isHard);
				startActivity(intent);
			}
		});
	}
}
