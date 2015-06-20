package com.zhangyanye.didipark.activity;

import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.fragments.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

	private Fragment[] fragments;
	private Fragment fragment_carport, fragment_user, fragment_publish;
	private Button[] btn_Tabs = new Button[4];
	private int index, currentTabIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		if (!(fragment_carport instanceof CarportFragment)
				&& !(fragment_user instanceof UserFragment)
				&& !(fragment_publish instanceof PublishFragment)
				) {
			initFragment();
		}

	}
    

	private void initFragment() {
		fragment_user = new UserFragment();
		fragment_carport = new CarportFragment();
		fragment_publish = new PublishFragment();

		fragments = new Fragment[] { fragment_carport, fragment_publish,
				fragment_user};
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, fragment_carport)
				.add(R.id.fragment_container, fragment_publish)
				.add(R.id.fragment_container, fragment_user)
				.hide(fragment_user).hide(fragment_publish)
				.commit();
		btn_Tabs[0] = (Button) findViewById(R.id.bt_carport);
		btn_Tabs[1] = (Button) findViewById(R.id.bt_publish);
		btn_Tabs[2] = (Button) findViewById(R.id.bt_user);
		btn_Tabs[0].setSelected(true);
	}

	public void login(View view) {
		Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.bt_carport:
			index = 0;
			break;
		case R.id.bt_publish:
			index = 1;
			break;
		case R.id.bt_user:
			index = 2;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}

			switch (index) {
			case 0:
				trx.hide(fragment_user)
						.hide(fragment_publish).show(fragment_carport).commit();
				break;
			case 1:
				trx.hide(fragment_user)
						.hide(fragment_carport).show(fragment_publish).commit();
				break;
			case 2:
				trx.hide(fragment_publish)
						.hide(fragment_carport).show(fragment_user).commit();
				break;
			}

			btn_Tabs[currentTabIndex].setSelected(false);
			btn_Tabs[index].setSelected(true);
			currentTabIndex = index;
		}
	}

}
