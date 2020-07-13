package pe.andy.bookholic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
}
