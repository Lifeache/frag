package com.msx.linker;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SourceLinker
{

	PathChecker pathChecker;

	protected ArrayList<SourcePackage<?>> srcPackageList;

	public SourceLinker(){
		pathChecker = new PathChecker();
	}

	public SourcePackage<?> getPackage(int index){
		return  srcPackageList.get(index);
	}

	public int getPackageCount(){
		return srcPackageList.size();
	}

	public void setPathChecker(PathChecker pathChecker)
	{
		this.pathChecker = pathChecker;
	}

	public PathChecker getPathChecker()
	{
		return pathChecker;
	}
	
	public void parseSourceFile(File file) throws DocumentException, SourceLinkException{
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element elr = document.getRootElement();
		List<Element> list = elr.elements();
		srcPackageList = new ArrayList<SourcePackage<?>>();
		for (Element el : list)
		{
			if (el.getName().equals(ImgPath.TAG_NAME))
			{
				ImgPath i = new ImgPath();
				i.parse(el);
				srcPackageList.add(i);
			} else if(el.getName().equals(Marks.TAG_NAME))
			{
				srcPackageList.add(Marks.getInstance());
			}else if (el.getName().equals(HttpSource.Companion.getTAG_NAME())){
				HttpSource httpSource = new HttpSource();
                httpSource.parse(el);
				srcPackageList.add(httpSource);
			}
		}
	}

}
