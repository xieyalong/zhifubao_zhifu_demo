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
//orderInfo 是后台生成
Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(PayDemoActivity.this);
//				Map<String, String> result = alipay.payV2(orderInfo, true);
				Map<String, String> result = alipay.payV2(data_http, true);

				Log.i("msp", result.toString());
				
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
---------------------------------------------
