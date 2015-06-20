package com.zhangyanye.didipark.fragments;

import java.util.List;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.activity.PublishActivity;
import com.zhangyanye.didipark.activity.UpdateCarportActivity;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.DbOperate;
import com.zhangyanye.didipark.view.ToolBarOnClickListener;
import com.zhangyanye.didipark.view.TopBar;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PublishFragment extends Fragment {

	private TopBar topbar;
	private Intent intent;
	private DbUtils db;
	private ListView listView;
	private List<Carport> carports;
	private Photo photos;
	private BaseAdapter adapter;
	private ImageLoader imageLoader;
	private final int Create_OK = 1;
	private SwipeRefreshLayout swipe;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_publish, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initCarport();
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	private void initView() {
		swipe = (SwipeRefreshLayout) getView().findViewById(R.id.fresh_swipe);
		swipe.setColorScheme(android.R.color.holo_blue_dark,
				android.R.color.holo_green_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		swipe.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				initCarport();
				swipe.setRefreshing(false);
			}
		});
		listView = (ListView) getView().findViewById(R.id.publish_list);
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		topbar = (TopBar) getView().findViewById(R.id.publish_topbar);
		topbar.setOnTopbarClickListener(new ToolBarOnClickListener() {
			@Override
			public void leftBtnClick() {
			}

			@Override
			public void rightBtnClick() {
				intent = new Intent(getActivity(), PublishActivity.class);
				PublishFragment.this.startActivityForResult(intent, Create_OK);
			}
		});
	}

	private void initCarport() {
		db = DbOperate.getInstance();
		try {
			carports = db.findAll(Carport.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		adapter = new MyAdapter(getActivity());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Crouton.makeText(getActivity(), "提示：长按进入车位管理哦~", Style.CONFIRM,
						R.id.publish_alternate_view).show();
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				Bundle bundle = new Bundle();
				bundle.putInt("carport_id", carports.get(position).getDb_id());
				intent = new Intent(getActivity(), UpdateCarportActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, 2);
				return true;
			}
		});

	}

	private NetworkImageView getIvView() {
		NetworkImageView imgView = new NetworkImageView(getActivity());
		imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		return imgView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == Create_OK) {
				initCarport();
			} else {
				initCarport();
			}
		}
	}

	private class MyAdapter extends BaseAdapter {
		private Context mContext;

		public MyAdapter(Context context) {
			this.mContext = context;
		}

		/**
		 * 元素的个数
		 */
		public int getCount() {
			if (carports != null)
				return carports.size();
			else
				return 0;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// 用以生成在ListView中展示的一个个元素View
		@SuppressWarnings("deprecation")
		@SuppressLint("InflateParams")
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// 优化ListView
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_carport, null);
				ItemViewCache viewCache = new ItemViewCache();

				viewCache.tv_addr = (TextView) convertView
						.findViewById(R.id.publish_addr);
				viewCache.tv_describe = (TextView) convertView
						.findViewById(R.id.publish_describe);
				viewCache.tv_money = (TextView) convertView
						.findViewById(R.id.publish_money);
				viewCache.tv_people = (TextView) convertView
						.findViewById(R.id.publish_people);
				viewCache.tv_num = (TextView) convertView
						.findViewById(R.id.publish_num);
				viewCache.iv_photo = (NetworkImageView) convertView
						.findViewById(R.id.publish_img);
				viewCache.iv_state = (ImageView) convertView
						.findViewById(R.id.publish_state);
				viewCache.iv_num = (ImageView) convertView
						.findViewById(R.id.publish_num_iv);
				convertView.setTag(viewCache);

			}
			ItemViewCache cache = (ItemViewCache) convertView.getTag();
			// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
			if (carports.size() > 0) {
				try {
					photos = db.findFirst(Selector.from(Photo.class).where(
							"carport_id", "=",
							carports.get(position).getId() + ""));
				} catch (DbException e) {
					e.printStackTrace();
				}
				cache.iv_photo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							photos = db
									.findFirst(Selector.from(Photo.class)
											.where("carport_id",
													"=",
													carports.get(position)
															.getId() + ""));
						} catch (DbException e) {
							e.printStackTrace();
						}
						final Dialog dialog = new Dialog(getActivity(),
								android.R.style.Theme_Black_NoTitleBar);
						NetworkImageView imgView = getIvView();
						dialog.setContentView(imgView);
						dialog.show();
						imgView.setImageUrl(photos.getPhotoUrl(), imageLoader);
						// 点击图片消失
						imgView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								System.gc();
							}
						});

					}
				});
				{
					if (photos.getPhotoUrl2().equals(""))
						cache.iv_photo.setImageUrl(photos.getPhotoUrl(),
								imageLoader);
					else
						cache.iv_photo.setImageUrl(photos.getPhotoUrl2(),
								imageLoader);
					if (carports.get(position).getState().equals("运营")) {
						cache.iv_state.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_carport_run));
						if (carports.get(position).getNum() == 0)
							cache.iv_num.setImageDrawable(getResources()
									.getDrawable(R.drawable.ic_sold_out));
					} else
						cache.iv_state.setImageDrawable(getResources()
								.getDrawable(R.drawable.ic_carport_stop));
					cache.tv_addr.setText(carports.get(position).getAddr());
					cache.tv_describe.setText(carports.get(position)
							.getDescribe());
					cache.tv_money.setText(carports.get(position)
							.getPerHoursMoney() + "");
					cache.tv_num.setText(carports.get(position).getNum() + "");
					cache.tv_people.setText(carports.get(position).getUser_num()
							+ "");
				}
			}
			return convertView;
		}

	}

	private static class ItemViewCache {
		public TextView tv_addr, tv_describe, tv_people, tv_money, tv_num;
		public ImageView iv_state, iv_num;
		public NetworkImageView iv_photo;
	}

}