package com.msx.frag;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Tab2Fragment extends Fragment {


    public Tab2Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        YouZoomImageView youZoomImageView = (YouZoomImageView)view.findViewById(R.id.youimg);
        Drawable bm = getResources().getDrawable(R.drawable.my51);
        youZoomImageView.setImageDrawable(bm);
        Log.d("arr","Set Image!vc:wh:" + youZoomImageView.getWidth() + "," + youZoomImageView.getHeight());
//        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressbar2);
//        final WebView tv = (WebView) view.findViewById(R.id.tv2);
//        new Handler().postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					progressBar.setVisibility(View.GONE);
//					tv.setVisibility(View.VISIBLE);
//				}
//			}, 2000);
//		tv.setWebViewClient(new WebViewClient());
//		tv.getSettings().setJavaScriptEnabled(true);
//		tv.requestFocus();// 触摸焦点起作用mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);//取消滚动条
//		tv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置允许js弹出alert对话框
//		tv.post(new Runnable(){
//
//				@Override
//				public void run()
//				{
//					// TODO: Implement this method!
//					tv.loadUrl("http://haowanba.com/cardh.php?name=1724841&pwd=xiang529&action=passport&_do=&time=1493563458");
//				}
//
//
//			});
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
