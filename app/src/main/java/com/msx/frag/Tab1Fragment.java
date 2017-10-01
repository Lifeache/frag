package com.msx.frag;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;

import com.msx.linker.HttpSource;
import com.msx.linker.ImgPath;
import com.msx.linker.Marks;
import com.msx.linker.Page;
import com.msx.linker.Scrollable;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Tab1Fragment extends Fragment {

	View view;
	ZoomImageView zoomImageView;
	ScrollableSourceLinker source;
	PopupWindow popMenu;
	Marks marks;
    public Tab1Fragment() {
    }

	public void setSource(ScrollableSourceLinker source)
	{
		this.source = source;
	}

	public ScrollableSourceLinker getSource()
	{
		return source;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("arr","onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("arr","onCreateView");
		return inflater.inflate(R.layout.fragment_tab1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		Log.d("arr","onViewCreated");
		this.view = view;
		zoomImageView = (ZoomImageView)view.findViewById(R.id.text1);
		initPopMenu();
		initMarks();
		initImageView();
//		zoomImageView.setGestureDetector(new GestureDetector(view.getContext(),new SimpleOnGestureListener(){
//				@Override
//				public boolean onDoubleTap(MotionEvent e)
//				{
//					if (source != null){
//						marks.mark(source.current());
//						LinearLayout line = (LinearLayout)getActivity().findViewById(R.id.top);
//						if (line.getVisibility() == View.VISIBLE){
//							line.setVisibility(View.GONE);
//						} else {
//							line.setVisibility(View.VISIBLE);
//						}
//
//					}
//					return true;
//				}
//				@Override
//				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
//				{
//					if ((e1.getRawX() - e2.getRawX()) > 100
//						&& Math.abs(velocityX) > 180
//						&& Math.abs(e2.getRawY() - e1.getRawY()) < 100)
//					{//向左滑动
//						if (source != null){
//							display(source.next());
//						}
//						Log.d("arr","左滑" + velocityX);
//						return true;
//					}
//					if ((e2.getRawX() - e1.getRawX()) > 100
//						&& Math.abs(velocityX) > 180
//						&& Math.abs(e2.getRawY() - e1.getRawY()) < 100)
//					{ //向右滑动
//						if (source != null){
//							display(source.prev());
//						}
//						Log.d("arr","右滑" + velocityX);
//						return true;
//					}
//					//下滑
//					if (e2.getRawY() - e1.getRawY() > 100 && Math.abs(velocityY) > 2500
//						&& Math.abs(e2.getRawX() - e1.getRawX()) < 100)
//					{
//						if (source != null){
//							Scrollable<String> sc = source.getCurrentScrollable();
//							if (sc instanceof ImgPath){
//								ImgPath img = (ImgPath)sc;
//								display(img.nextPath(img.getDeep() - 2));
//							} else {
//								display(source.next());
//							}
//						}
//						Log.d("arr","下滑" + velocityY);
//						return true;
//					}
//					//上滑
//					if (e1.getRawY() - e2.getRawY() > 100 && Math.abs(velocityY) > 2500
//						&& Math.abs(e2.getRawX() - e1.getRawX()) < 100)
//					{
//						if (source != null){
//							Scrollable<String> sc = source.getCurrentScrollable();
//							if (sc instanceof ImgPath){
//								ImgPath img = (ImgPath)sc;
//								display(img.previousPath(img.getDeep() - 2));
//							} else {
//								display(source.prev());
//							}
//						}
//						Log.d("arr","上滑" + velocityY);
//						return true;
//					}
//					return super.onFling(e1, e2, velocityX, velocityY);
//				}
//
//				@Override
//				public void onLongPress(MotionEvent me){
//					popMenu.showAsDropDown(zoomImageView,(int)me.getX(),(int)me.getY() - zoomImageView.getHeight());
//				}
//			}
//								  ));
		SharedPreferences sp = view.getContext().getSharedPreferences("sp",Activity.MODE_PRIVATE);
		
		}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
		Log.d("arr","onAttach");
    }

    @Override
    public void onDetach() {
		Log.d("arr","onDetach");
        super.onDetach();
    }

    private void initPopMenu(){
		final View pv = LayoutInflater.from(view.getContext()).inflate(R.layout.pop_menu,null);
		popMenu = new PopupWindow(pv,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,true);
		popMenu.setContentView(pv);
		ListView listView = (ListView)pv.findViewById(R.id.popmenuListView1);
		String[] sd = new String[]{"下一图包","上一图包","下一资源包","上一资源包"};
		listView.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1,sd));
		listView.setOnItemClickListener(new OnItemClickListenerAction());
	}

	private void initMarks(){
		marks = Marks.getInstance();
		try
		{
			marks.parseMarksFile(new File("/storage/emulated/0/C.MSX/mark.xml"));
		}
		catch (DocumentException e)
		{
			e.printStackTrace();
		}
	}

	private void initImageView(){
		zoomImageView.setGestureActionListener(new GestureActionListener() {
			@Override
			public void onDoubleTap(MotionEvent me) {
				if (source != null){
					marks.mark(source.current());
					LinearLayout line = (LinearLayout)getActivity().findViewById(R.id.top);
					if (line.getVisibility() == View.VISIBLE){
						line.setVisibility(View.GONE);
					} else {
						line.setVisibility(View.VISIBLE);
					}

				}
			}

			@Override
			public void onSwipeUp() {
				prevImagePackage();
				Log.d("arr","上滑");
			}

			@Override
			public void onSwipeDown() {
				nextImagePackage();
				Log.d("arr","下滑");
			}

			@Override
			public void onSwipeLeft() {
				if (source != null){
					display(source.next());
				}
				Log.d("arr","左滑");
			}

			@Override
			public void onSwipeRight() {
				if (source != null){
					display(source.prev());
				}
				Log.d("arr","右滑");
			}

			@Override
			public void onLongPress(MotionEvent me) {
				popMenu.showAsDropDown(zoomImageView,(int)me.getX(),(int)me.getY() - zoomImageView.getHeight());
			}
		});
	}

	public boolean display(String path){
        if (path == null){
            return  false;
        }
		ImgThread it = new ImgThread();
		it.path = path;
		it.start();
		while(!it.isOver){}
		return !it.isError;
	}

	private void nextImagePackage(){
		if (source != null){
			Scrollable<String> sc = source.getCurrentScrollable();
			if (sc instanceof ImgPath){
				ImgPath img = (ImgPath)sc;
				String path = img.next(1);
				if (path == null){
					Log.d("arr","next null;next source package");
					Scrollable<String> s = source.nextScrollablePackage();
					if (s != null){
						path = s.current();
					}
				}
				display(path);
			} else if (sc instanceof HttpSource){
				HttpSource httpS = (HttpSource)sc;
				if (httpS.getHasNP()){
					httpS.getAddList().get(httpS.getAddList().size() - 1).next();
				} else {
					httpS.getAddList().get(httpS.getAddList().size() - 2).next();
				}
				ArrayList<Page> pages = httpS.getPageList();
				for (int i = 1; i < pages.size(); i++){
					pages.get(i).setPath("");
				}
				httpS.setPathBase("");
				httpS.setCanScroll(false);
				String path = httpS.next();
				if (path.equals("")){
					Log.d("arr","next null;next source package");
					Scrollable<String> s = source.nextScrollablePackage();
					if (s != null){
						path = s.current();
					}
				}
				display(path);
			} else {
				display(source.next());
			}
		}
	}

	public void prevImagePackage(){
		if (source != null){
			Scrollable<String> sc = source.getCurrentScrollable();
			if (sc instanceof ImgPath){
				ImgPath img = (ImgPath)sc;
				String path = img.prev(1);
				if (path == null){
					Scrollable<String> s = source.prevScrollablePackage();
					if (s != null){
						path = s.current();
					}
				}
				display(path);
			} else if (sc instanceof HttpSource){
				HttpSource httpS = (HttpSource)sc;
				if (httpS.getHasNP()){
					httpS.getAddList().get(httpS.getAddList().size() - 1).previous();
				} else {
					httpS.getAddList().get(httpS.getAddList().size() - 2).previous();
				}
				ArrayList<Page> pages = httpS.getPageList();
				for (int i = 1; i < pages.size(); i++){
					pages.get(i).setPath("");
				}
				httpS.setPathBase("");
				httpS.setCanScroll(false);
                String path = httpS.prev();
                if (path.equals("")){
                    Log.d("arr","prev null;prev source package");
                    Scrollable<String> s = source.prevScrollablePackage();
                    if (s != null){
                        path = s.current();
                    }
                }
                display(path);
            } else {
				display(source.prev());
			}
		}
	}

	
	Handler handler = new Handler();
	
	class ImgThread extends Thread
		{
			String path;
			boolean isOver;
			boolean isError;
			@Override
			public void run()
			{
				try
				{
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					if (conn.getResponseCode() == 200){
						isError = false;
						isOver = true;
						InputStream inputStream = conn.getInputStream();
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						handler.post(new Runnable(){

								@Override
								public void run()
								{
									// TODO: Implement this method
									zoomImageView.loadImgae(bitmap);
								}


							});
					}else{
						isError = true;
						isOver = true;
						
					}
				}
				catch (MalformedURLException e)
				{}
				catch (IOException e)
				{}
			}
		}

	/**
     * Created by Administrator on 2017/7/1.
     */
	class OnItemClickListenerAction implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0){
				nextImagePackage();
			} else if (position == 1){
				prevImagePackage();
			} else if (position == 2){
				if (source != null){
					Scrollable<String> p = source.nextScrollablePackage();
					if (p != null){
						display(p.current());
					}
				}
			} else if (position == 3){
				if (source != null){
					Scrollable<String> p = source.prevScrollablePackage();
					if (p != null){
						display(p.current());
					}
				}
			}
			popMenu.dismiss();
		}
	}
}


