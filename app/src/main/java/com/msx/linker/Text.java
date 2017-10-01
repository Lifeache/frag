package com.msx.linker;

/**
 * Created by Administrator on 2017/7/7.
 */
public class Text{
    String text;
    Object[] childObj;
	Text(){

	}

	public void setChild(Object[] childs){
        childObj = childs;
    }

	@Override
	public String toString()
	{
        StringBuffer sb = new StringBuffer();
        if (childObj == null || childObj.length == 0){
            sb.append(text);
        } else {
            for (Object o : childObj) {
                sb.append(o.toString());
            }
        }
		return sb.toString();
	}

}
