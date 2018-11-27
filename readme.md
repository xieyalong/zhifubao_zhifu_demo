# 支付标准sdk_demo

demo现在已切换到AndroidStudio环境，第一次build如果出现

```
Error:A problem occurred configuring project ':app'.
> SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable.
```

这是由于Android SDK路径没有配置好，推荐：

1. 在全局的环境变量上添加`ANDROID_HOME`
2. 在project根目录下添加`local.properties`文件，并指定

```sdk.dir={本机Android SDK所在绝对路径}```

-----------------------------------------------
权限问题：
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CALL_PHONE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
   代码申请：
  private void requestPermission() {
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
				!= PackageManager.PERMISSION_GRANTED
			|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{
							Manifest.permission.READ_PHONE_STATE,
							Manifest.permission.WRITE_EXTERNAL_STORAGE
					}, PERMISSIONS_REQUEST_CODE);

		} else {
			showToast(this, "支付宝 SDK 已有所需的权限");
		}
	}
在根gradle：
allprojects {
    repositories {

        // 支付宝 SDK AAR 包所需的配置
        flatDir {
            dirs 'libs'
        }

        jcenter()
    }
}
导入arr：
  // 支付宝 SDK AAR 包所需的配置
    compile (name: 'alipaySdk-15.5.7-20181023110917', ext: 'aar')
    
//orderInfo 是后台生成的秘钥
Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);

				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
回调：
 private Handler mHandler = new Handler(Looper.myLooper()) {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showAlert(mContext, "支付成功: " + payResult);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(mContext, "支付失败: " + payResult);
                    }
                    break;
                }
 }		
---------------------------------------------
