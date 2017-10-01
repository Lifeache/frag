package com.msx.linker;
import android.util.Log;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Marks implements SourcePackage<String>,Scrollable<String>
{

	@Override
	public String next()
	{
		if (markList != null){
			if (listIndex + 1 >= markList.size()){
				return null;
			} else {
				listIndex ++;
				return markList.get(listIndex);
			}
		}
		return null;
	}

	@Override
	public String prev()
	{
		if (markList != null){
			if (listIndex - 1 < 0){
				return null;
			} else {
				listIndex --;
				return markList.get(listIndex);
			}
		}
		return null;
	}

	@Override
	public String current()
	{
		if (markList != null) {
			return markList.get(listIndex);
		}
		return null;
	}

	int listIndex = 0;

	ArrayList<String> markList;
	
	final static String TAG_NAME = "marks";

	final static String TAG_NAME_TEXT = "mark";

	Document document;
	
	Element root;
	
	File markFile;
	
	private final static Marks mark = new Marks();
	
	private Marks(){
		
		markList = new ArrayList<String>();
	}
	
	public static Marks getInstance(){
		return mark;
	}
	
	public void parseMarksFile(File file) throws DocumentException{
		SAXReader reader = new SAXReader();
		Log.d("arr","mark0:" + toString());
		document = reader.read(file);
		markFile = file;
		root = document.getRootElement();
		if (root == null){
			root = document.addElement(Marks.TAG_NAME);
		}
		List<Element> list = root.elements();
		for (Element el : list)
		{
			if (el.getName().equals(Marks.TAG_NAME_TEXT))
			{
				String text = el.getTextTrim().trim();
				if (!isExistInMarksList(text)) {
					markList.add(text);
				}
			}
		}
		
	}

	boolean isExistInMarksList(String mark){
		for (String str : markList) {
			if (str.equals(mark)){
				return true;
			}
		}
		return false;
	}

	public void mark(String markText){
		Log.d("arr","mark1:" + toString());
		if (isExistInMarksList(markText)){
			return;
		}
		try
		{
			FileWriter sw = new FileWriter(markFile);
			XMLWriter xw = new XMLWriter(sw,OutputFormat.createPrettyPrint());
			root.addElement(Marks.TAG_NAME_TEXT).setText(markText);
			Log.d("arr","markel:" + document.getRootElement());
			xw.write(document);
			xw.flush();
			xw.close();
		}
		catch (IOException e)
		{}
	}
}
