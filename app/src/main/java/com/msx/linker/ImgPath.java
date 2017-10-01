package com.msx.linker;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class ImgPath implements SourcePackage<String>,Scrollable<String>
{
	final static String TAG_NAME = "imgpath";
	
	ArrayList<Object> list;

	Add[] adds;
	
	int deep;
	
	boolean needChecker;
	
	ImgPath(){
		
	}
	
	public void parse(Element imgEl) throws SourceLinkException{
		if (imgEl == null || !imgEl.getName().equals(TAG_NAME)){
			throw new SourceLinkException();
		}
		List<Element> elList = imgEl.elements();
		list = new ArrayList<Object>();
		deep = imgEl.elements("add").size();
		adds = new Add[deep];
		int i = 0;
		for (Element el : elList)
		{
			if (el.getName().equals("text"))
			{
				Text t = new Text();
				t.text = el.getTextTrim().trim();
				list.add(t);
			}
			else if (el.getName().equals("add"))
			{
				Add a = new Add();
				a.on = Integer.valueOf(el.attributeValue("on", "1"));
				a.last = a.on;
				a.gap = Integer.valueOf(el.attributeValue("gap", "1"));
				a.limitLow = Integer.valueOf(el.attributeValue("limit_low", "1"));
				String high = el.attributeValue("limit_high", Add.NO_SRC);
				if (Add.NO_SRC.equals(high)){
					a.limitHigh = -1;
					needChecker = true;
				} else {
					a.limitHigh = Integer.valueOf(high);
				}
				list.add(a);
				adds[i++] = a;
			}
			else
			{
			}
		}
	}

	public int getDeep(){
		return  deep;
	}

	public String getPath(int index){
		int i = 1;
		StringBuilder path = new StringBuilder();
		for(Object obj : list){
			if (obj.getClass().equals(Add.class)){
				Add a = (Add)obj;
				if (i++ == deep){
					if (index < 0){
						index = 0;
					}
					a.on = a.limitLow + index * a.gap;
					if (a.on > a.limitHigh){
						a.on = a.limitHigh;
					}
				}
				path.append(a.toString());
			} else if (obj.getClass().equals(Text.class)){
				path.append(obj.toString());
			}
		}
		return path.toString();
	}
	
	public String nextPath(){
		return nextPath(deep - 1);
	}
	
	public String nextPath(int changeInt){
		StringBuilder path = new StringBuilder();
		for(int i = changeInt;i >= 0 && i < adds.length; i --){
			if (!adds[i].next()){
				break;
			}
		}
		for(int i = changeInt + 1;i < adds.length; i++){
			adds[i].on = adds[i].limitLow;
		}
		for(Object obj : list){
			path.append(obj.toString());
		}
		return path.toString();
	}
	
	public String previousPath(){
		return previousPath(deep - 1);
	}
	
	public String previousPath(int changeInt){
		StringBuilder path = new StringBuilder();
		for(int i = changeInt;i >= 0 && i < adds.length; i --){
			if (!adds[i].previous()){
				break;
			}
		}
		for(int i = changeInt + 1;i < adds.length; i++){
			adds[i].on = adds[i].limitLow;
		}
		for(Object obj : list){
			path.append(obj.toString());
		}
		return path.toString();
	}
	
	public String currentPath(){
		StringBuilder path = new StringBuilder();
		for(Object obj : list){
			path.append(obj.toString());
		}
		return path.toString();
	}

	public String lastPath(){
		StringBuilder path = new StringBuilder();
		for (int i = 0; i < adds.length; i++) {
			adds[i].last();
		}
		for(Object obj : list){
			path.append(obj.toString());
		}
		return path.toString();
	}

	public String next(int deep){
		if (!needChecker) {
			return  nextPath();
		}
		PathChecker checker = new PathChecker();
		String newPath = null;
		for (int i = adds.length - 1 - deep; i >= 0; i--) {
			newPath = nextPath(i);
			if (!checker.check(newPath)){//进位
				lastPath();
				newPath = null;
			} else {
				break;
			}
		}
		return newPath;
	}

	public String prev(int deep){
		if (!needChecker) {
			return  previousPath();
		}
		PathChecker checker = new PathChecker();
		String newPath = null;
		for (int i = adds.length - 1 - deep; i >= 0; i--) {
			newPath =previousPath(i);
			if (!checker.check(newPath)){//借位
				lastPath();
				newPath = null;
			} else {
				break;
			}
		}
		return newPath;
	}

	@Override
	public String next() {
		return next(0);
	}

	@Override
	public String prev() {
		return prev(0);
	}

	@Override
	public String current() {
		return currentPath();
	}
}

