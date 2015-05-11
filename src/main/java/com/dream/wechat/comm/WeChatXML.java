package com.dream.wechat.comm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

public class WeChatXML extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;
	private String root;
	private String rootValue;

	public static final String TEXT_MSG = "text";
	public static final String IMG_MSG = "image";
	public static final String VOICE_MSG = "voice";
	public static final String VIDEO_MSG = "video";
	public static final String SHORTVIDEO_MSG = "shortvideo";
	public static final String LOCATION_MSG = "location";
	public static final String LINK_MSG = "link";
	public static final String EVENT_MSG = "event";
	public static final String MUSIC_MSG = "music";
	public static final String NEWS_MSG = "news";
	
	public static final String EVENT_SUBSCRIBE = "subscribe";
	public static final String EVENT_UNSUNSCRIBE = "unsubscribe";
	public static final String EVENT_LOCATION = "LOCATION";
	public static final String EVENT_SCAN = "SCAN";
	public static final String EVENT_CLICK = "CLICK";
	public static final String EVENT_VIEW = "VIEW";	
	
	public static final String CDATA="cdata";
	public static final String TEXT="text";
	public static final String SUBITEM="subitem";
	

	public String getRootValue() {
		return rootValue;
	}

	public void setRootValue(String rootValue) {
		this.rootValue = rootValue;
	}

	public Object get(String key) {
		Object[] arr = (Object[])super.get(key);
		if( arr == null ) new Exception("Access key that does not exist");
		return arr[1];
	}
	
	public String getType(String key)  {
		Object[] arr = (Object[])super.get(key);
		if( arr == null ) new Exception("Access key that does not exist");
		return (String)arr[0];	
	}
	
	public void setValue(String key, WeChatXML i) {
		Object[] arr = (Object[])super.get(key);
		if( arr == null ) new Exception("Access key that does not exist");
		arr[0] = SUBITEM;
		arr[1] = i;
	}
	
	@SuppressWarnings("unchecked")
	public void addCDATA(String key, String value) {
		String[] arr = {CDATA, null};
		arr[1] = value;
		this.put(key, arr);
	}
	
	@SuppressWarnings("unchecked")
	public void addText(String key, String value) {
		String[] arr = {TEXT, null};
		arr[1] = value;
		this.put(key, arr);
	}
	
	@SuppressWarnings("unchecked")
	public void addItem(String key, WeChatXML value) throws Exception {
		if( super.get(key) == null ) {
			Vector<WeChatXML> v = new Vector<WeChatXML>();
			v.add(value);
			Object[] arr = {SUBITEM, v};
			super.put(key, arr);
		} else {
			Object[] arr = (Object[])super.get(key);
			if( !SUBITEM.equals((String)arr[0])) throw new Exception("Node type wrong");
			Vector<WeChatXML> v = (Vector<WeChatXML>)arr[1];
			v.add(value);
		}
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		 return toXML(sb);
	}
	
	public int getEleCount(String name) {
		Object[] arr = (Object[])super.get(name);
		if( !SUBITEM.equals((String)arr[0])) return 0;
		return ((Vector<WeChatXML>)arr[1]).size();
	}
	
	@SuppressWarnings("unchecked")
	public String toXML(StringBuffer sb) {
		sb.append("<"); sb.append(getRoot()); sb.append(">");
		if( rootValue != null )
			sb.append(rootValue.trim());
		for( String o : this.keySet()) {
			Object[] arr = (Object[])super.get(o);
			sb.append("<"); sb.append(o); sb.append(">");
			if(SUBITEM.equals((String)arr[0])) {
				Vector<WeChatXML> v = (Vector<WeChatXML>)arr[1];
				for( WeChatXML i: v) {
					i.toXML(sb);
				}
			} else if(TEXT.equals((String)arr[0])) {
				sb.append((String)arr[1]);
			} else {
				sb.append("<![CDATA["); sb.append((String)arr[1]); sb.append("]]>");
			}
			sb.append("</"); sb.append(o); sb.append(">");
		}
		sb.append("</"); sb.append(getRoot()); sb.append(">");
		return sb.toString();
	}	
	
	@SuppressWarnings("unchecked")
	private static boolean isCDATA(Element e) {
		for(Node n: (List<Node>)e.content())
			if(org.w3c.dom.Node.CDATA_SECTION_NODE == n.getNodeType())
				return true;
		return false;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static WeChatXML fromXML(Element e) throws Exception {
		WeChatXML x = new WeChatXML();
		Iterator i = e.elementIterator();
		while( i.hasNext() ) {
			Element ee = (Element)i.next();
			List l = ee.elements();
			if( l.size() == 0 ) {
				if (isCDATA(ee)) {
					x.addCDATA(ee.getName(), ee.getStringValue().trim());
				} else { 
					x.addText(ee.getName(), ee.getStringValue().trim());
				}
			} else {
				for( Element ie : (List<Element>)l) {
					WeChatXML xx = fromXML(ie);
					x.addItem(ee.getName(), xx);	
				}
			}
		}
		if( e.elements().size() == 0 )
			x.setRootValue(e.getStringValue());
		x.setRoot(e.getName());
		return x;
	}
	
	public static WeChatXML fromXML(String xml) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
			Element rootElt = doc.getRootElement();
			return fromXML(rootElt);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	public static WeChatXML fromXML(InputStream is) throws Exception {
		String xml = IOUtils.toString(is, "utf-8");
		return fromXML(xml);
	}	
}