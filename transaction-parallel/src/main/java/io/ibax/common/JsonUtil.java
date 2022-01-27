package io.ibax.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	private String path = "config/contracts.json";
	private BufferedReader br = null;

	public Map<String, String[]> readJsonFile() {
		Map<String, String[]> funcsMap = new HashMap<String, String[]>();
		try {
			br = new BufferedReader(new InputStreamReader(JsonUtil.class.getClassLoader().getResourceAsStream(path)));
			String line = br.readLine();
			StringBuilder sb = new StringBuilder();
			while (line != null) {
				sb.append(line + "\r\n");
				line = br.readLine();
			}
			Map<String, Object> jsonMap = JSON.parseObject(sb.toString());

			String[] funcsArray = {};
			for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
				JSONArray jsonArray = JSONObject.parseArray(entry.getValue().toString());

				funcsArray = jsonArray.toArray(funcsArray);
				funcsMap.put(entry.getKey(), funcsArray);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return funcsMap;
	}

}
