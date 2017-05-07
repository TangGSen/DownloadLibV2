package com.lzy.demo.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.demo.R;

import java.util.ArrayList;
import java.util.List;


public class DownloadManagerActivityV extends FragmentActivity implements
		OnClickListener, HideCheckBoxListener {

	Button btnDonwnloading;
	Button btnDownloaded;
	private boolean isShowRigth;
	private boolean isShowLeft;
	/**
	 * 作为页面容器的ViewPager
	 */
	ViewPager mViewPager;
	/**
	 * 页面集合
	 */
	List<Fragment> fragmentList;

	/**
	 * 四个Fragment（页面）
	 */
	DownloadingFragment downloadingFragment;
	DownloadedFragment downloadedFragment;

	// 覆盖层

	// 屏幕宽度
	int screenWidth;
	// 当前选中的项
	int currenttab = -1;
	private TextView btn_delete_file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_download_manager);
		btnDonwnloading = (Button) findViewById(R.id.btn_donwnloading);
		btnDownloaded = (Button) findViewById(R.id.btn_downloaded);
		findViewById(R.id.down_imgbtn_close).setOnClickListener(
				new OnClickListener() {

					@SuppressLint("NewApi")
					@Override
					public void onClick(View arg0) {
						finish();
					}
				});

		btnDonwnloading.setOnClickListener(this);
		btnDownloaded.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		btn_delete_file = (TextView) findViewById(R.id.btn_delete_file);
		btn_delete_file.setOnClickListener(this);
		fragmentList = new ArrayList<>();
		downloadingFragment = new DownloadingFragment();
		downloadedFragment = new DownloadedFragment();
		
		Intent intent = getIntent();
		Bundle bundle =intent.getBundleExtra("actionData");
		downloadingFragment.setArguments(bundle);
		downloadedFragment.setArguments(bundle);
		fragmentList.add(downloadingFragment);
		fragmentList.add(downloadedFragment);

		mViewPager.setAdapter(new MyFrageStatePagerAdapter(
				getSupportFragmentManager()));

		changeIndexColor(mViewPager.getCurrentItem());
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				changeIndexColor(arg0);
				downloadingFragment.
						hideCheckBox();
				downloadedFragment.hideCheckBox();
				btn_delete_file.setText("删除");
				if (arg0==0) {
					//左
					btn_delete_file.setVisibility(isShowLeft ? View.VISIBLE : View.GONE);
				}else {
					btn_delete_file.setVisibility(isShowRigth ? View.VISIBLE : View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void changeIndexColor(int item) {

		switch (item) {
		case 0:
			btnDonwnloading.setSelected(true);
			btnDownloaded.setSelected(false);
			break;
		case 1:
			btnDonwnloading.setSelected(false);
			btnDownloaded.setSelected(true);
			break;

		}
	}

	class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

		public MyFrageStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		/**
		 * 每次更新取消ViewPager的内容后，调用该接口，
		 */
		@Override
		public void finishUpdate(ViewGroup container) {
			super.finishUpdate(container);// 这句话要放在最前面，否则会报错
			// 获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
			int currentItem = mViewPager.getCurrentItem();
			if (currentItem == currenttab) {
				return;
			}
			currenttab = mViewPager.getCurrentItem();

		}

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btn_donwnloading) {
			changeView(0);
			changeIndexColor(0);
		} else if (v.getId() == R.id.btn_downloaded) {
			changeView(1);
			changeIndexColor(1);
		} else if (v.getId() == R.id.btn_delete_file) {
			if (mViewPager.getCurrentItem() == 0) {
				if (downloadingFragment == null) {
					return;
				}
				if (btn_delete_file.getText().equals("取消")) {
					downloadingFragment.hideCheckBox();
					btn_delete_file.setText("删除");
				} else {
					// 删除
				//	downloadingFragment.showCheckBox();
					btn_delete_file.setText("取消");
				}

			} else {
				if (downloadedFragment == null) {
					return;
				}
				if (btn_delete_file.getText().equals("取消")) {
					downloadedFragment.hideCheckBox();
					btn_delete_file.setText("删除");
				} else {
					// 删除
				//	downloadedFragment.showCheckBox();
					btn_delete_file.setText("取消");
				}
			}

		}

	}

	// 手动设置ViewPager要显示的视图
	private void changeView(int desTab) {
		mViewPager.setCurrentItem(desTab, true);
	}

	@Override
	public void changeTextBtn() {
		if (btn_delete_file != null) {
			btn_delete_file.setText("删除");
			// btn_delete_file.setEnabled(isNullData);
		}

	}

	@Override
	public void showRigthHideBtn(boolean isShow) {
		isShowRigth = isShow;
		if (mViewPager.getCurrentItem() == 1) {
			btn_delete_file.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public void showLeftHideBtn(boolean isShow) {
		isShowLeft = isShow;
		if (mViewPager.getCurrentItem() == 0) {
			btn_delete_file.setVisibility(isShow ? View.VISIBLE : View.GONE);
		}
	}

}
