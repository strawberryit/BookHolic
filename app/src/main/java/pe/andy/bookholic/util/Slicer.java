package pe.andy.bookholic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Slicer {

	String text;
	String regExp;
	List<String> slices;
	boolean isTrim = false;
	
	public Slicer(String text, String regExp){
		this.text = StringUtils.defaultString(text);
		this.regExp = StringUtils.defaultString(regExp);
		
		// slice it
		slices = new ArrayList<>(Arrays.asList(this.text.split(this.regExp)));
	}
	
	public Slicer trim(){
		this.isTrim = true;
		return this;
	}
	
	public String get(int index) {

		if (slices == null)
			return null;
			
		if (slices.size() > index ){
			if (isTrim)
				return StringUtils.trimToEmpty(slices.get(index));
			else
				return slices.get(index);
		}
		
		if (isTrim)
			return "";
		else
			return null;
	}
	
	public String pop() {
		
		if (slices == null)
			return null;

		if (slices != null && slices.size() > 0){
			if (isTrim)
				return StringUtils.trimToEmpty(slices.remove(0));
			else
				return slices.remove(0);
		}
		
		if (isTrim)
			return "";
		else
			return null;

	}
}
