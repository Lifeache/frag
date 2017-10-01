package com.msx.frag;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.msx.linker.SourceLinkException;

import org.dom4j.DocumentException;

import java.io.File;

public class Tab3Fragment extends Fragment {

	Button b ;
	Button testButton;
	EditText t;
	Handler handler = new Handler();
	ScrollableSourceLinker source;
    public Tab3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab3, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      	b = (Button)view.findViewById(R.id.button3);
		t = (EditText)view.findViewById(R.id.edit3);
		testButton = (Button)view.findViewById(R.id.testButton);
		testButton.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
                    if (source != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                final String strr = source.getScrollablePackage(source.getPackageCount() - 1).next();
                                handler.post(new Runnable(){

                                    @Override
                                    public void run() {
                                        // TODO: Implement this method
//								int i = t.getSelectionStart();
//								SpannableString ss = new SpannableString("你要写的内容");
//								ss.setSpan(new ForegroundColorSpan(Color.RED), 0, ss.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//								t.getText().insert(i,ss);


                                        //source.getScrollablePackage(source.getPackageCount() - 1).next();
                                        t.setText(strr);
                                    }
                                });
                            }
                        }).start();
                    }

				}

			
		});
		b.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					SharedPreferences sp = view.getContext().getSharedPreferences("sp",Activity.MODE_PRIVATE);
					SharedPreferences.Editor e = sp.edit();
					e.putString("text",t.getText().toString());
					e.commit();
					if (source == null)
					{
						File file = new File("/storage/emulated/0/C.MSX/frag.xml");
						source = new ScrollableSourceLinker();
						try
						{
							source.parseSourceFile(file);
						}
						catch (DocumentException de)
						{}
						catch (SourceLinkException se)
						{}
						Tab1Fragment f =(Tab1Fragment)getFragmentManager().findFragmentByTag(Tab1Fragment.class.getName());
						f.setSource(source);
					}
					final String str = source.current() + "\npackage:" + source.getCurrentIndex();
					handler.post(new Runnable(){

							@Override
							public void run()
							{
								// TODO: Implement this method
								t.setText(str);
							}

						
					});
					
					
				}

			
		});
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
