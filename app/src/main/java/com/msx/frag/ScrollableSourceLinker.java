package com.msx.frag;

import android.util.Log;

import com.msx.linker.Scrollable;
import com.msx.linker.SourceLinkException;
import com.msx.linker.SourceLinker;
import com.msx.linker.SourcePackage;

import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/1.
 */

public class ScrollableSourceLinker extends SourceLinker implements Scrollable<String>{

    ArrayList<Scrollable<String>> scrollableArrayList;

    int packageInt = 0;

    boolean isFirstMovement = true;

    @Override
    public void parseSourceFile(File file) throws DocumentException, SourceLinkException {
        super.parseSourceFile(file);
        scrollableArrayList = new ArrayList<Scrollable<String>>();
        for (int i = 0; i < srcPackageList.size(); i++) {
            SourcePackage p = srcPackageList.get(i);
            Log.d("arr","look:p= " + p.getClass());
            Log.d("arr","look:s= " + Scrollable.class);

            if (p instanceof Scrollable){
                Log.d("arr","yes");
                scrollableArrayList.add((Scrollable<String>)p);
            } else {
                Log.d("arr","no");
            }

        }
        srcPackageList = null;
    }

    @Override
    public String next() {
        String nowPath = null;
        if (isFirstMovement)
        {
            isFirstMovement = false;
            nowPath = current();
        } else{
            nowPath = getScrollablePackage(packageInt).next();
            if (nowPath == null) {
                Scrollable<String> scrollable = nextScrollablePackage();
                if (scrollable == null) {
                    nowPath = null;
                } else {
                    nowPath = scrollable.current();
                }
            }
        }
        return nowPath;
    }

    @Override
    public String prev() {
        String nowPath = null;
        if (isFirstMovement)
        {
            isFirstMovement = false;
            nowPath = current();
        } else{
            nowPath = getScrollablePackage(packageInt).prev();
            if (nowPath == null) {
                Scrollable<String> scrollable = prevScrollablePackage();
                if (scrollable == null) {
                    nowPath = null;
                } else {
                    nowPath = scrollable.current();
                }
            }
        }
        return nowPath;
    }

    @Override
    public String current() {
        return getScrollablePackage(packageInt).current();
    }

    public Scrollable<String> prevScrollablePackage(){
        if (packageInt - 1 >= 0){
            return getScrollablePackage(--packageInt);
        } else {
            return null;
        }
    }

    public Scrollable<String> nextScrollablePackage(){
        if (packageInt + 1 < getScrollablePackageCount()){
            return getScrollablePackage(++packageInt);
        } else {
            return null;
        }
    }

    @Override
    public int getPackageCount() {
        return getScrollablePackageCount();
    }

    @Override
    public SourcePackage<?> getPackage(int index) {
        return (SourcePackage<?>) scrollableArrayList.get(index);
    }

    public int getCurrentIndex(){

        return packageInt;
    }

    public int getScrollablePackageCount(){
       return scrollableArrayList.size();
    }

    public Scrollable<String> getScrollablePackage(int index){
        return scrollableArrayList.get(index);
    }
    public Scrollable<String> getCurrentScrollable(){
        return scrollableArrayList.get(packageInt);
    }
}
