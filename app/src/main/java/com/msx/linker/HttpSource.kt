package com.msx.linker

import android.util.Log
import org.dom4j.Element
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import org.jsoup.nodes.Element as JElement

/**
 * Created by Administrator on 2017/7/6.
 */
class HttpSource : SourcePackage<String>,Scrollable<String>{

    var canScroll = false
    val pageList = ArrayList<Page>()
    var pathBase = ""
    val addList = ArrayList<Add>()
    var nextIndex = -1
    var prevIndex = -1
    var index = 0
    var hasNP = false
    var limit = 0
    companion object {
        val TAG_NAME = "httpsrc"
    }
    fun HttpSource(){

    }
    fun parse(httpEl : Element){
        val elements : MutableList<Any?> = httpEl.elements()
        elements.filter {
                val el : Element = it as Element
                el.name == Page.TAG_NAME
        }

                .forEachIndexed  { index,it ->
                    val el : Element = it as Element
                    val page = Page()
                    page.path = el.attributeValue("path") ?: ""
                    page.type = el.attributeValue("type") ?: ""
                    when(page.type){
                        "next" -> nextIndex = index
                        "prev" -> prevIndex = index
                    }
                    val innerEls = el.elements()
                    innerEls.forEach {
                        val iEL = it as Element
                        when (iEL.name) {
                            "path" -> {
                                iEL.elements().forEach {
                                    val iell = it as Element
                                    when(iell.name){
                                        "add" ->{
                                            val add = Add()
                                            add.on = Integer.valueOf(iell.attributeValue("on","0"))
                                            add.limitLow = Integer.valueOf(iell.attributeValue("limit_low","0"))
                                            add.limitHigh = Integer.valueOf(iell.attributeValue("limit_high","-1"))
                                            add.gap = Integer.valueOf(iell.attributeValue("gap","1"))
                                            page.pathList.add(add)
                                            addList.add(add)
                                        }
                                        "text" -> {
                                            page.pathList.add(iell.textTrim.trim())
                                        }
                                    }
                                }
                            }
                            Select.TAG_NAME -> {
                                val select = Select()
                                select.value = iEL.attributeValue("value", "")
                                select.index = Integer.valueOf(iEL.attributeValue("index", "0"))
                                iEL.elements().forEach {
                                    val iell = it as Element
                                    when(iell.name){
                                        "index" ->{
                                            val addE = iell.elements().first() as Element
                                            val add = Add()
                                            add.on = Integer.valueOf(addE.attributeValue("on","0"))
                                            add.limitLow = Integer.valueOf(addE.attributeValue("limit_low","0"))
                                            add.limitHigh = Integer.valueOf(addE.attributeValue("limit_high","-1"))
                                            add.gap = Integer.valueOf(addE.attributeValue("gap","1"))
                                            select.indexAdd = add
                                            addList.add(add)
                                        }
                                    }
                                }
                                page.list.add(select)
                            }
                            Atrr.TAG_NAME -> {
                                val atrr = Atrr()
                                atrr.value = iEL.attributeValue("value", "")
                                page.list.add(atrr)
                            }
                            "text" -> {
                                val text = Text()
                                val els = iEL.elements()
                                if (els.size != 0) {
                                    val childs = Array<Any>(els.size) {}
                                    var i = 0
                                    els.forEach {
                                        val ell = it as Element
                                        when (ell.name) {
                                            "text" -> {
                                                val text0 = Text()
                                                text0.text = ell.textTrim.trim()
                                                childs[i++] = text0
                                            }
                                            Atrr.TAG_NAME -> {
                                                val atrr = Atrr()
                                                atrr.value = ell.attributeValue("value", "")
                                                childs[i++] = atrr
                                            }
                                        }
                                    }
                                    text.childObj = childs
                                } else {
                                    text.text = iEL.textTrim.trim()
                                }
                                page.list.add(text)
                            }
                        }
                    }
                    pageList.add(page)
                }
        if (nextIndex != -1 && prevIndex != -1){
            hasNP = true
            limit = if (nextIndex > prevIndex) prevIndex else nextIndex - 2
        } else {
            limit = pageList.size - 2
        }
    }

    fun run(int : Int){
        var limt = 0
        if (nextIndex == -1 || prevIndex == -1){
            limt = pageList.size - 1
        } else {
            limt = if (nextIndex > prevIndex) prevIndex else nextIndex - 2
        }
        for (i in int..limt){
            if (pageList[i].path == "") pageList[i].path = pathBase
            pathBase = pageList[i].load()
        }
    }

    override fun next(): String {

        if (!canScroll){
            var str = ""
            Log.d("arr","next:begin!")
            for (i in 0..limit){
                if (pageList[i].path == "") pageList[i].path = str
                str = pageList[i].load()
                Log.d("arr", "next[$i]:$str")
            }
            pathBase = str
            pageList[limit + 1].path = pathBase
            str = pageList[limit + 1].load()
            canScroll = true
            return str
        }  else {
            if (hasNP) {
                pageList[nextIndex].path = pathBase
                pathBase = pageList[nextIndex].load()
            } else {
                pageList[limit].path = pathBase
                pathBase = pageList[limit].load()
                addList[addList.size - 1].next()
            }
            pageList[limit + 1].path = pathBase
            return pageList[limit + 1].load()
        }
    }

    override fun prev(): String {
        if (!canScroll){
            var str = ""
            Log.d("arr","prev:begin!")
            for (i in 0..limit){
                if (pageList[i].path == "") pageList[i].path = str
                str = pageList[i].load()
                Log.d("arr", "prev[$i]:$str")
            }
            pathBase = str
            pageList[limit + 1].path = pathBase
            str = pageList[limit + 1].load()
            canScroll = true
            return str
        }  else {
            if (hasNP) {
                pageList[prevIndex].path = pathBase
                pathBase = pageList[prevIndex].load()
            } else {
                pageList[limit].path = pathBase
                pathBase = pageList[limit].load()
                addList[addList.size - 1].previous()
            }
            pageList[limit + 1].path = pathBase
            return pageList[limit + 1].load()
        }
    }

    override fun current(): String {
        if (!canScroll){
            var str = ""
            Log.d("arr","current:begin!")
            for (i in 0..limit){
                if (pageList[i].path == "") pageList[i].path = str
                str = pageList[i].load()
                Log.d("arr", "current[$i]:$str")
            }
            pathBase = str
            pageList[limit + 1].path = pathBase
            str = pageList[limit + 1].load()
            canScroll = true
            return str
        }  else {
            pageList[limit + 1].path = pathBase
            return pageList[limit + 1].load()
        }
    }

}

class Page{
    companion object {
        val TAG_NAME : String = "page"
    }
    var path = ""
    get() {
        if (pathList.isEmpty()){
            return field
        } else {
            var r = ""
            pathList.forEach {
                r += it.toString()
            }
            return r
        }
    }
    var type = ""
    var list = ArrayList<Any>()
    var src : String? = ""
    val pathList = ArrayList<Any>()

    fun load() : String{
        val pt = PageThread()
        pt.start()
        while (!pt.isOver){}
        Log.d("arr","load over")
        return pt.rs
    }
   inner class PageThread : Thread() {
       var isOver:Boolean = false
       var rs = ""
       override fun run(){
           if (path == ""){
               isOver = true
               return
           }
           Log.d("arr","load:" + path)
           val d : Document = Jsoup.parse(URL(path),2000)
           var jsoupEl = d.body()
           Log.d("arr","load:size:" + list.size)
           for (s in list){
               when (s){
                   is Select -> {
                       Log.d("arr","load:select")
                       jsoupEl = s.select(jsoupEl as org.jsoup.nodes.Element)
                   }
                   is Atrr -> {
                       src = s.atrr(jsoupEl as org.jsoup.nodes.Element)
                       rs = if (src == null) "" else src as String
                       isOver = true
                       return
                   }
                   is Text -> {
                       Log.d("arr","load:text")
                       s.childObj.forEach {
                           if ( it is Atrr){
                               it.atrr(jsoupEl as org.jsoup.nodes.Element)
                           }
                       }
                       rs = s.toString()
                       isOver = true
                       return
                   }
               }
           }
           isOver = true
       }
    }
}
class Select{
    var value = ""
    var index = 0
    get() {
        if (indexAdd == null){
            return field
        } else{
            return Integer.valueOf(indexAdd.toString())
        }
    }
    var indexAdd : Add? = null
    companion object {
        val TAG_NAME = "select"
    }
    fun Select(){

    }
    fun select(jEl : JElement):JElement?{
        Log.d("arr", "select:value[$index]:$value")
        return jEl.select(value)[index]
    }
}
class Atrr{
    var value = ""
    var text = ""
    companion object{
        val TAG_NAME = "attr"
    }
    fun atrr(jEL : JElement) : String?{
        text = jEL.attr(value)
        return text
    }

    override fun toString(): String {
        return text
    }
}