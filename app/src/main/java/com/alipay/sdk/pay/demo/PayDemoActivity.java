package com.alipay.sdk.pay.demo;

import java.util.Map;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.util.OrderInfoUtil2_0;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

/**
 *  重要说明：
 *  
 *  本 Demo 只是为了方便直接向商户展示支付宝的整个支付流程，所以将加签过程直接放在客户端完成
 *  （包括 OrderInfoUtil2_0）。
 *
 *  在真实 App 中，私钥（如 RSA_PRIVATE 等）数据严禁放在客户端，同时加签过程务必要放在服务端完成，
 *  否则可能造成商户私密数据泄露或被盗用，造成不必要的资金损失，面临各种安全风险。
 */
public class PayDemoActivity extends AppCompatActivity {
	
	/**
	 * 用于支付宝支付业务的入参 app_id。
	 */
	public static final String APPID = "2018092961486812";
	
	/**
	 * 用于支付宝账户登录授权业务的入参 pid。
	 */
	public static final String PID = "";

	/**
	 * 用于支付宝账户登录授权业务的入参 target_id。
	 */
	public static final String TARGET_ID = "";

	/**
	 *  pkcs8 格式的商户私钥。
	 *
	 * 	如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
	 * 	使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
	 * 	RSA2_PRIVATE。
	 *
	 * 	建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
	 * 	工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
	 */
	public static final String RSA2_PRIVATE = "-----BEGIN RSA PRIVATE KEY-----\n" +
			"MIIEpAIBAAKCAQEAunftawLx8a+v0Np7euoZkqQH4kcHFdeqkNTm6pJ6QiNkTzGO\n" +
			"JcSxUNjyZ+o1V6PRZKhoM/Ld+5zoeAwT7EB24AD8H542vlc3gPmmQT9fkf5XuosS\n" +
			"1pUj33w3/0IjwGtRT/m/iQGI/atCumPuz9TATQlBYq/m3/4eq1/v6SUWwapRY1V8\n" +
			"/2zoMTg4Tu+i+61AOzdB0J49eNCumWK5UDt9WorYPsQ/9022WBzgX1mhYHEIP8mv\n" +
			"0z4m/zyPhsR6cscjyWSmzetIWwq/8tw7zaSMZwh1AkxCuoLzce0tw+ll8WyqGmBJ\n" +
			"OfpkBeNJtXVW+P5nvaKa2YBxdWWbvuBPJwly6wIDAQABAoIBABVpEcy4wNkUaCad\n" +
			"/5GkcZ1B0lnr9+dNJRuOJpmGih0v/SOOuBikFgwHbQX7gXZ3q+JX5+cXGt/6K0Ml\n" +
			"d+Anor6jvk9PFlASqZ/YintxcGLOBnq330jeRQkzNSbNDFfaurJpJdHwaC9gS8Al\n" +
			"G1WnwxDVYDmZovPTNOhOkrh9drhzyKcFHI64cYaX4SbGwPpKeS98/t50wJEKSIP7\n" +
			"+yHcoGHXY7rb6XSWAuIYjkoChJX3O0Mne0ft2QuLxvoZgfD3ape9WHd/355KxwLt\n" +
			"J0o4JmR2jel8Fnp1DNGF9SX3mW80ZRAMVxByYRbOGdkS8TbaHeeE955ZNua4G5Kp\n" +
			"mwR20wkCgYEA6ziNNcfvqHVEMOC+NqhcIUPuXI0lB+Yo7LUh1i+6ogkBmfszfhF7\n" +
			"KLh2zorb2PlLI2YZK5HnCPhQTFT34KX5cE8VGVgmNjiwDCKinAlwbgVn22/onZyL\n" +
			"yayhnD3TwV1zuLTwmWWMPxJ5qxVEqsp70B5Bve/jW70DLWHL2zsbgT0CgYEAyvDa\n" +
			"uzr+RzeizFDE37g0ZFV60x2WkVTqCxfpsK5FyjDIXWI6x0GGOwUDX4vdh1chf2PI\n" +
			"/wTqoOwfCVSv66t8X9n6FmVCMTBSsofb7QNUYf38GcqogKnE+XFgKWQs3WXhmJ+H\n" +
			"U61ThcPqDoLn9lFv9Sfe5vAMj0aWZ1RL80F+t0cCgYBDMwXMnpEOPGo92IcfEFw5\n" +
			"uLGJSV2R8yQv89N14G7eSdpmRkjAtHfN+10dxrfmNdpMg6efNfCeNGqkVAX47RHl\n" +
			"DwLFGdTY2rsYzHBOSbgQBoVUQJVhKc8Zz7Lf8x3i+TLNSwwNluBJSJrv0ISskmpq\n" +
			"26oGfFkk5UvaFD1sJWfHNQKBgQCop8wQJ/Hku98HD+TdzFd2PvfpdcCJHVm4SHJF\n" +
			"DEAhzbLmsNkKeXEVoY/xGurLLGVBWmtTTQG1JSaR+ZiOMiEWA7BsZlDBUliX2IK0\n" +
			"kbzSB2M5CAxLiUuSxl9VZtRGVwJKdeHgb75m/6+HydD/CyURR8ZehIbbl50tWIga\n" +
			"LIcjxwKBgQC/qV1c4CXkiWRD5uhDr8K28gQu3polRPIAmu5yDvi6VuxO9GNBjfdm\n" +
			"cFwXrUDjCCOc1ySbNTKp340jUdC2MEueNAMtKOThxlI2HqTb0Vk484zb0gBbOU9P\n" +
			"gu8GB0eNlSD4L1UuEy1uKaIsQs3mVEimF25nsthm+8RfUd/Psicd6g==\n" +
			"-----END RSA PRIVATE KEY-----";
	public static final String RSA_PRIVATE = "-----BEGIN RSA PRIVATE KEY-----\n" +
			"MIIEpAIBAAKCAQEAunftawLx8a+v0Np7euoZkqQH4kcHFdeqkNTm6pJ6QiNkTzGO\n" +
			"JcSxUNjyZ+o1V6PRZKhoM/Ld+5zoeAwT7EB24AD8H542vlc3gPmmQT9fkf5XuosS\n" +
			"1pUj33w3/0IjwGtRT/m/iQGI/atCumPuz9TATQlBYq/m3/4eq1/v6SUWwapRY1V8\n" +
			"/2zoMTg4Tu+i+61AOzdB0J49eNCumWK5UDt9WorYPsQ/9022WBzgX1mhYHEIP8mv\n" +
			"0z4m/zyPhsR6cscjyWSmzetIWwq/8tw7zaSMZwh1AkxCuoLzce0tw+ll8WyqGmBJ\n" +
			"OfpkBeNJtXVW+P5nvaKa2YBxdWWbvuBPJwly6wIDAQABAoIBABVpEcy4wNkUaCad\n" +
			"/5GkcZ1B0lnr9+dNJRuOJpmGih0v/SOOuBikFgwHbQX7gXZ3q+JX5+cXGt/6K0Ml\n" +
			"d+Anor6jvk9PFlASqZ/YintxcGLOBnq330jeRQkzNSbNDFfaurJpJdHwaC9gS8Al\n" +
			"G1WnwxDVYDmZovPTNOhOkrh9drhzyKcFHI64cYaX4SbGwPpKeS98/t50wJEKSIP7\n" +
			"+yHcoGHXY7rb6XSWAuIYjkoChJX3O0Mne0ft2QuLxvoZgfD3ape9WHd/355KxwLt\n" +
			"J0o4JmR2jel8Fnp1DNGF9SX3mW80ZRAMVxByYRbOGdkS8TbaHeeE955ZNua4G5Kp\n" +
			"mwR20wkCgYEA6ziNNcfvqHVEMOC+NqhcIUPuXI0lB+Yo7LUh1i+6ogkBmfszfhF7\n" +
			"KLh2zorb2PlLI2YZK5HnCPhQTFT34KX5cE8VGVgmNjiwDCKinAlwbgVn22/onZyL\n" +
			"yayhnD3TwV1zuLTwmWWMPxJ5qxVEqsp70B5Bve/jW70DLWHL2zsbgT0CgYEAyvDa\n" +
			"uzr+RzeizFDE37g0ZFV60x2WkVTqCxfpsK5FyjDIXWI6x0GGOwUDX4vdh1chf2PI\n" +
			"/wTqoOwfCVSv66t8X9n6FmVCMTBSsofb7QNUYf38GcqogKnE+XFgKWQs3WXhmJ+H\n" +
			"U61ThcPqDoLn9lFv9Sfe5vAMj0aWZ1RL80F+t0cCgYBDMwXMnpEOPGo92IcfEFw5\n" +
			"uLGJSV2R8yQv89N14G7eSdpmRkjAtHfN+10dxrfmNdpMg6efNfCeNGqkVAX47RHl\n" +
			"DwLFGdTY2rsYzHBOSbgQBoVUQJVhKc8Zz7Lf8x3i+TLNSwwNluBJSJrv0ISskmpq\n" +
			"26oGfFkk5UvaFD1sJWfHNQKBgQCop8wQJ/Hku98HD+TdzFd2PvfpdcCJHVm4SHJF\n" +
			"DEAhzbLmsNkKeXEVoY/xGurLLGVBWmtTTQG1JSaR+ZiOMiEWA7BsZlDBUliX2IK0\n" +
			"kbzSB2M5CAxLiUuSxl9VZtRGVwJKdeHgb75m/6+HydD/CyURR8ZehIbbl50tWIga\n" +
			"LIcjxwKBgQC/qV1c4CXkiWRD5uhDr8K28gQu3polRPIAmu5yDvi6VuxO9GNBjfdm\n" +
			"cFwXrUDjCCOc1ySbNTKp340jUdC2MEueNAMtKOThxlI2HqTb0Vk484zb0gBbOU9P\n" +
			"gu8GB0eNlSD4L1UuEy1uKaIsQs3mVEimF25nsthm+8RfUd/Psicd6g==\n" +
			"-----END RSA PRIVATE KEY-----";
	
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
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
					showAlert(PayDemoActivity.this, "支付成功: " + payResult);
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					showAlert(PayDemoActivity.this, "支付失败: " + payResult);
				}
				break;
			}
			case SDK_AUTH_FLAG: {
				@SuppressWarnings("unchecked")
				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
				String resultStatus = authResult.getResultStatus();

				// 判断resultStatus 为“9000”且result_code
				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
					// 获取alipay_open_id，调支付时作为参数extern_token 的value
					// 传入，则支付账户为该授权账户
					showAlert(PayDemoActivity.this, "授权成功: " + authResult);
				} else {
					// 其他状态值则为授权失败
					showAlert(PayDemoActivity.this, "授权失败: " + authResult);
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		requestPermission();
	}

	/**
	 * 获取权限使用的 RequestCode
	 */
	private static final int PERMISSIONS_REQUEST_CODE = 1002;

	/**
	 * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
	 * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
	 * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
	 */
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

	/**
	 * 权限获取回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_CODE: {

				// 用户取消了权限弹窗
				if (grantResults.length == 0) {
					showToast(this, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
					return;
				}

				// 用户拒绝了某些权限
				for (int x : grantResults) {
					if (x == PackageManager.PERMISSION_DENIED) {
						showToast(this, "无法获取支付宝 SDK 所需的权限, 请到系统设置开启");
						return;
					}
				}

				// 所需的权限均正常获取
				showToast(this, "支付宝 SDK 所需的权限已经正常获取");
			}
		}
	}


	/**
	 * 支付宝支付业务示例
	 */
	public void payV2(View v) {
//		if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
//			showAlert(this, "错误: 需要配置 PayDemoActivity 中的 APPID 和 RSA_PRIVATE");
//			return;
//		}
//
//		/*
//		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
//		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
//		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
//		 *
//		 * orderInfo 的获取必须来自服务端；
//		 */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//		Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//		String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//		String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//		final String orderInfo = orderParam + "&" + sign;
		
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
	}

	/**
	 * 支付宝账户授权业务示例
	 */
	public void authV2(View v) {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			showAlert(this, "错误: 需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID");
			return;
		}

		/*
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险； 
		 * 
		 * authInfo 的获取必须来自服务端；
		 */
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
		String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
		
		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
		final String authInfo = info + "&" + sign;
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(PayDemoActivity.this);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(authInfo, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}
	
	/**
	 * 获取支付宝 SDK 版本号。
	 */
	public void showSdkVersion(View v) {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		showAlert(this, "支付宝 SDK 版本: " + version);
	}

	/**
	 * 将 H5 网页版支付转换成支付宝 App 支付的示例
	 */
	public void h5Pay(View v) {
		WebView.setWebContentsDebuggingEnabled(true);
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();

		/*
		 * URL 是要测试的网站，在 Demo App 中会使用 H5PayDemoActivity 内的 WebView 打开。
		 *
		 * 可以填写任一支持支付宝支付的网站（如淘宝或一号店），在网站中下订单并唤起支付宝；
		 * 或者直接填写由支付宝文档提供的“网站 Demo”生成的订单地址
		 * （如 https://mclient.alipay.com/h5Continue.htm?h5_route_token=303ff0894cd4dccf591b089761dexxxx）
		 * 进行测试。
		 * 
		 * H5PayDemoActivity 中的 MyWebViewClient.shouldOverrideUrlLoading() 实现了拦截 URL 唤起支付宝，
		 * 可以参考它实现自定义的 URL 拦截逻辑。
		 */
		String url = "https://m.taobao.com";
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}

	private static void showAlert(Context ctx, String info) {
		showAlert(ctx, info, null);
	}

	private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
		new AlertDialog.Builder(ctx)
				.setMessage(info)
				.setPositiveButton("确定", null)
				.setOnDismissListener(onDismiss)
				.show();
	}

	private static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}


	String data_http="alipay_sdk=alipay-sdk-php-20180705&app_id=2018092961486812&biz_content=%7B%22body%22%3A%22%E6%94%AF%E4%BB%98%E5%AE%9D%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95%22%2C%22subject%22%3A%22App%E6%94%AF%E4%BB%98%E5%AE%9D%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95%22%2C%22out_trade_no%22%3A%2286879933923493220383050078389021%22%2C%22total_amount%22%3A%220.01%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=https%3A%2F%2Fomoapi.yuanxinkangfu.com%2Fcapp%2Fpayment-notify%2Falipay.html&sign_type=RSA2&timestamp=2018-11-26+19%3A41%3A35&version=1.0&sign=QRet2xmDzQS4hX35pneeb80oGnHIqFv4XuqiUzNvxAIpWs%2BUJJQ6VcoiCDUEr49eRZqydobH%2B%2FuytoFdjBfhjgmA8sEbXAfdXe7O%2FPUDYzMTrkBKr%2FBlUE9u8z0Urwk%2FhQ9lNTFfmajj5pp%2FjxFFNUpzzZnmLlpm4uZHkCwcJKey%2B1uvQrmqVu3kMA0GPQE5SbfRgZ0uE7MDPSrr30sM%2Bq6BGQTfSwi4SKHWg2obWUG32KX38oedztOPQzJEtJPWpue5EzrACzzUgV3Yp4b4GDmjSy2S0Xfs%2FkY%2Bsjo2eQNwXVVJ3WW5U5v70mHZ3WrZa79dgSxDKLzd5aKFKhppmg%3D%3D";
}
