package com.zhangyanye.didipark.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangyanye.didipark.R;
import com.zhangyanye.didipark.activity.MoreCarportActivity;
import com.zhangyanye.didipark.application.MyApplication;
import com.zhangyanye.didipark.pojo.Carport;
import com.zhangyanye.didipark.pojo.Photo;
import com.zhangyanye.didipark.utils.BFImageCache;
import com.zhangyanye.didipark.utils.DistanceUtil;
import com.zhangyanye.didipark.utils.MyContants;
import com.zhangyanye.didipark.view.MyOrientationListener;
import com.zhangyanye.didipark.view.MyOrientationListener.OnOrientationListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class CarportFragment extends Fragment {
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	private ImageButton bt_location;
	private MyOrientationListener listener;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private float mCurrentX;
	private double mLatitude, mLongtitude;
	private View map_pop;
	private ImageListener img_listener;
	private LayoutInflater inflater;
	private ImageView imageView;
	private ImageLoader imageLoader;
	private TextView carport_addr, carport_money, carport_remain;
	List<LatLng> pts = new ArrayList<LatLng>();
	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_carport, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initLocation();
		initOrientation();
	}

	private void getCarport() {
		RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				MyContants.URL_GET_CARPORT + "latitude=" + mLatitude
						+ "&longitude=" + mLongtitude,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject json = null;
						Gson gson = new Gson();
						try {
							String temp = new String(
									response.getBytes("ISO8859-1"), "UTF-8");
							json = new JSONObject(temp);
							List<Carport> list = gson.fromJson(
									json.get("carports").toString(),
									new TypeToken<List<Carport>>() {
									}.getType());
							List<Photo> imgs = gson.fromJson(json.get("photos")
									.toString(), new TypeToken<List<Photo>>() {
							}.getType());
							addOverlays(list, imgs);
							Crouton.makeText(getActivity(),
									"附近有5公里" + list.size() + "个停车点",
									Style.CONFIRM, R.id.carport_view_group)
									.show();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Crouton.makeText(getActivity(), "服务器出错！", Style.ALERT,
								R.id.carport_view_group).show();
					}
				});
		requestQueue.add(stringRequest);
	}

	private void initView() {
		imageLoader = new ImageLoader(MyApplication.queue,
				BFImageCache.getInstance());
		inflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mMapView = (MapView) getView().findViewById(R.id.bmapView);
		bt_location = (ImageButton) getView().findViewById(R.id.bt_location);
		bt_location.setOnClickListener(new CarportListener());
		mMapView.showZoomControls(false);
		mBaiduMap = mMapView.getMap();

	}

	private void initLocation() {
		mCurrentMode = LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, null));
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mCurrentX).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			mLatitude = location.getLatitude();
			mLongtitude = location.getLongitude();

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// 生成地图状态将要发生的变化
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}
	}

	private void initOrientation() {
		listener = new MyOrientationListener(getActivity());
		listener.setOnOrientationListener(new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mCurrentX = x;
			}
		});
		listener.start();
	}

	class CarportListener implements OnClickListener {

		@SuppressLint("ShowToast")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_location:
				if (mLatitude == 0 && mLongtitude == 0) {
					Crouton.makeText(getActivity(), "当前网络不稳定，请稍后再试",
							Style.INFO, R.id.carport_view_group).show();
				} else {
					centerToMyLocation();
					getCarport();
				}
				break;

			}

		}
	}

	private void addOverlays(final List<Carport> carports,
			final List<Photo> photos) {
		mBaiduMap.clear();
		BitmapDescriptor mMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.maker);
		LatLng latLng = null;
		Marker marker = null;
		OverlayOptions options;
		for (int i = 0; i < carports.size(); i++) {
			// 经纬度
			latLng = new LatLng(carports.get(i).getLatitude(), carports.get(i)
					.getLongitude());
			// 图标
			options = new MarkerOptions().position(latLng).icon(mMarker)
					.zIndex(5);
			marker = (Marker) mBaiduMap.addOverlay(options);
			Bundle bundle = new Bundle();
			bundle.putInt("position", i);
			marker.setExtraInfo(bundle);
			pts.add(latLng);
		}
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@SuppressLint("InflateParams")
			@Override
			public boolean onMarkerClick(final Marker marker) {
				Bundle extraInfo = marker.getExtraInfo();
				final int position = extraInfo.getInt("position");
				InfoWindow infoWindow;
				map_pop = inflater.inflate(R.layout.info_carport, null, false);
				imageView = (ImageView) map_pop.findViewById(R.id.carport_img);
				img_listener = ImageLoader.getImageListener(imageView,
						R.drawable.ic_carport_def, R.drawable.ic_carport_def);
				imageLoader.get(photos.get(position).getPhotoUrl2(),
						img_listener);
				/*
				 * imageView.setImageUrl(photos.get(position).getPhotoUrl2(),
				 * imageLoader);
				 */
				carport_addr = (TextView) map_pop
						.findViewById(R.id.carport_addr);
				carport_money = (TextView) map_pop
						.findViewById(R.id.carport_moeny);
				carport_remain = (TextView) map_pop
						.findViewById(R.id.carport_remain);
				final String distance = carports.get(position).getAddr()
						+ "\n(距离约"
						+ DistanceUtil.GetDistance(mLongtitude, mLatitude,
								carports.get(position).getLongitude(), carports
										.get(position).getLatitude()) + "m)";
				carport_addr.setText(distance);
				carport_money.setText(carports.get(position).getPerHoursMoney()
						+ " (¥/H)");
				carport_remain.setText("剩余 " + carports.get(position).getNum()
						+ " 个车位");
				OnInfoWindowClickListener clickListener = new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick() {

						Intent intent = new Intent(getActivity(),
								MoreCarportActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("distance", distance);
						bundle.putSerializable("carport",
								carports.get(position));
						bundle.putCharSequence("photo", photos.get(position)
								.getPhotoUrl());
						intent.putExtras(bundle);
						startActivity(intent);
						mBaiduMap.hideInfoWindow();
					}
				};
				LatLng ll = marker.getPosition();
				infoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(map_pop), ll, -47, clickListener);
				mBaiduMap.showInfoWindow(infoWindow);
				return true;
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();
			}
		});

	}

	/**
	 * 定位到我的位置
	 */
	private void centerToMyLocation() {
		LatLng latLng = new LatLng(mLatitude, mLongtitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	@Override
	public void onStart() {
		super.onStart();
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted())
			mLocClient.start();
		// 开启方向传感器
		listener.start();
	}

	// map生命周期
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		// 关闭定位图层
		mMapView.onDestroy();
		mMapView = null;
		listener.stop();

	}

	@Override
	public void onStop() {

		super.onStop();
		// 停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();
		// 停止方向传感器
		listener.stop();

	}

}
