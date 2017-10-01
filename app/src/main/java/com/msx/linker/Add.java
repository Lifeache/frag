package com.msx.linker;

/**
 * Created by Administrator on 2017/7/8.
 */
public class Add
{
	final static String NO_SRC = "no_src";
	int on;
	int last;
	int gap;
	int limitLow;
	int limitHigh;

	@Override
	public String toString()
	{
		return "" + on;
	}

	public boolean next(){
		last = on;
		if (limitHigh != -1 && on + gap > limitHigh)
		{//产生进位标志
			on = limitLow;
			return true;
		}
		else
		{
			on += gap;
		}
		return false;
	}

	public boolean previous(){
		last = on;
		if (on - gap < limitLow)
		{//产生进位标志
			if (limitHigh == -1){
				on = limitLow;
			} else{
				on = limitHigh;
			}
			return true;
		}
		else
		{
			on -= gap;
		}
		return false;
	}

	public void last(){
		on = last;
	}
}
