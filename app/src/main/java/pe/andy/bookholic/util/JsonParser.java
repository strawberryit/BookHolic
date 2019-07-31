package pe.andy.bookholic.util;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JsonParser {

	private static DateFormat formatter = SimpleDateFormat.getDateTimeInstance();

    /**
     * 결과를 특정 Class의 리스트에 맞게 파싱한다.
     * @param clazz : 변환 될 클래스
     * @param keys : root element가 될 키 이름
     * @return 파싱된 결과 인스턴스
     * @throws IOException
     */
    public static <T> List<T> parseJsonList(String json, Class<T> clazz, String... keys) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(formatter);
		
		JsonNode rootNode = mapper.readTree(json);
		for (String k : keys){
			rootNode = rootNode.get(k);
		}
		
		List<T> list = new ArrayList<>();
		for (JsonNode node : rootNode){
			T item = mapper.readValue(node.traverse(), clazz);
			list.add(item);
		}
		return list;
    }
    
    public static List<Map<String, String>> parseJsonKVList(String json, String... keys) throws JsonProcessingException, IOException {
    	TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
    	
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(formatter);
		
		JsonNode rootNode = mapper.readTree(json);
		for (String k : keys){
			rootNode = rootNode.get(k);
		}
		
		List<Map<String, String>> list = new ArrayList<>();
		for (JsonNode node : rootNode){
			Map<String, String> item = mapper.readValue(node.traverse(), typeRef);
			list.add(item);
		}
		return list;
    }
    
    public static int parseIntValue(String json, String... keys) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(formatter);
		
		JsonNode rootNode = mapper.readTree(json);
		for (String k : keys){
			rootNode = rootNode.get(k);
		}
		
		return rootNode.asInt();
    }

	public static int parseOnlyInt(String text){
		try {
			return Integer.parseInt(RegExUtils.replacePattern(text, "\\D*", ""));
		} catch (Exception e){
			return -1;
		}
	}

	/**
	 * element에서 하위객체를 선택하여 숫자로 파싱한다.
	 * @param e: 파싱할 객체
	 * @param selector: CSS Selector
	 * @return 파싱된 수 또는 에러가 발생하면 -1
	 */
	public static int parseOnlyIntFrom(Element e, String selector){
		return parseOnlyIntFrom(e, selector, "");
	}

	/**
	 * element에서 하위객체를 선택하여 숫자로 파싱한다.
	 * @param e: root dom
	 * @param selector: CSS Selector
	 * @param removeRegex: 찾은 dom의 text에서 제거할 부분에 대한 RegEx 패턴
	 * @return 파싱된 수 또는 에러가 발생하면 -1
	 */
	public static int parseOnlyIntFrom(Element e, String selector, String removeRegex){

		try {
			String text = e.select(selector).first().text();
			text = RegExUtils.replacePattern(text, removeRegex, "");
			return parseOnlyInt(text);
		} catch (Exception exception){
			return -1;
		}
	}

	public static String getTextOfFirstElement(Element e, String css){
		return getTextOfElement(e, css, 0);
	}

	public static String getTextOfElement(Element e, String css, int index){
		return StringUtils.trimToEmpty(
				e.select(css)
						.get(index)
						.text()
		);
	}

	public static String getTextOf(@NonNull Elements elems, int index) {
		return Optional.of(elems)
				.filter(list -> list.size() > index)
				.map(list -> list.get(index))
				.map(Element::text)
				.orElse("");
	}

	public static String getAttrOfFirstElement(Element e, String css, String attr){
		return getAttrOfElement(e, css, 0, attr);
	}

	public static String getAttrOfElement(Element e, String css, int index, String attr){
		return StringUtils.trimToEmpty(
				e.select(css)
						.get(index)
						.attr(attr)
		);
	}
}
