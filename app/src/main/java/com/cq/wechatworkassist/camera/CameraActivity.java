package com.cq.wechatworkassist.camera;
 

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.cq.wechatworkassist.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class CameraActivity extends Activity implements CameraInterface.CamOpenOverCallback {
	private static final String TAG = "yanzi";
	CameraSurfaceView surfaceView = null;
	ImageButton shutterBtn;
	float previewRate = -1f;
	@SuppressLint("CheckResult")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		RxPermissions rxPermissions = new RxPermissions(this);
		rxPermissions.request(Manifest.permission.CAMERA)
				.subscribe(permission -> {
					Thread openThread = new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							CameraInterface.getInstance().doOpenCamera(CameraActivity.this);

							CameraInterface.getInstance().doTakePicture();
						}
					};
					openThread.start();

					initUI();
					initViewParams();

					shutterBtn.setOnClickListener(new BtnListeners());
				});



	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void initUI(){
		surfaceView = (CameraSurfaceView)findViewById(R.id.camera_surfaceview);
		shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
	}
	private void initViewParams(){
		LayoutParams params = surfaceView.getLayoutParams();
		Point p = DisplayUtil.getScreenMetrics(this);
//		params.width = p.x;
//		params.height = p.y;
		previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
//		surfaceView.setLayoutParams(params);
 
		//手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
		LayoutParams p2 = shutterBtn.getLayoutParams();
		p2.width = DisplayUtil.dip2px(this, 80);
		p2.height = DisplayUtil.dip2px(this, 80);
		shutterBtn.setLayoutParams(p2);


	}
 
	@Override
	public void cameraHasOpened() {
		// TODO Auto-generated method stub
		SurfaceHolder holder = surfaceView.getSurfaceHolder();
		CameraInterface.getInstance().doStartPreview(holder, previewRate);
	}
	private class BtnListeners implements OnClickListener{
 
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_shutter:
				CameraInterface.getInstance().doTakePicture();
				break;
			default:break;
			}
		}
 
	}
 
}
