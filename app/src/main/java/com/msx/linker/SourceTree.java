package com.msx.linker;

/**
 * Created by Administrator on 2017/7/1.
 */

public abstract class SourceTree {
    public String next(int deep,boolean isLeafModel){
        String nowPath = null;
        if (isLeafModel){
            nowPath = nextByLeafModel(deep);
        } else{
            nowPath = nextByRootModel(deep);
        }
        return nowPath;
    }

    private String nextByLeafModel(int deep){

        return null;
    }

    private String nextByRootModel(int deep){
        String nowPath = null;

        return nowPath;
    }

    public String previous(int deep,boolean isLeafModel){
        String nowPath = null;

        return nowPath;
    }

    private String previousByLeafModel(int deep){
        String nowPath = null;

        return nowPath;
    }

    private String previousByRootModel(int deep){

        return null;
    }
}
