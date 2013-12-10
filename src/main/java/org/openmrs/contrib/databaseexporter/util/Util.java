/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.contrib.databaseexporter.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.openmrs.contrib.databaseexporter.Configuration;
import org.openmrs.contrib.databaseexporter.TableRow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {

	public static String toString(Collection<?> c) {
		StringBuilder ret = new StringBuilder();
		if (c != null) {
			for (Object o : c) {
				ret.append(ret.length() == 0 ? "" : ",").append(o);
			}
		}
		return ret.toString();
	}

	public static String toString(Object[] c) {
		if (c != null) {
			return toString(Arrays.asList(c));
		}
		return "";
	}

	public static List<String> toList(String s, String separator) {
		List<String> ret = new ArrayList<String>();
		for (String element : s.split(separator)) {
			ret.add(element);
		}
		return ret;
	}

	public static boolean isEmpty(Object o) {
		return o == null || o.equals("");
	}

	public static boolean notEmpty(Object o) {
		return !isEmpty(o);
	}

	public static <T> T firstNotNull(T... values) {
		for (T val : values) {
			if (notEmpty(val)) {
				return val;
			}
		}
		return null;
	}

	public static <T> T nvl(T o, T valueIfNull) {
		if (isEmpty(o)) {
			return valueIfNull;
		}
		return o;
	}

	public static String nvlStr(Object o, String valueIfNull) {
		if (isEmpty(o)) {
			return valueIfNull;
		}
		return o.toString();
	}

	public static boolean areEqualStr(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		return nvlStr(o1, "").equals(nvlStr(o2, ""));
	}

	public static String formatDate(Date d, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(d);
	}

	public static String loadResource(String path) {
		// First try to load from file
		String contents = null;
		try {
			contents = FileUtils.readFileToString(new File(path), "UTF-8");
		}
		catch (Exception e) {}

		// If that didn't work, try loading from classpath
		if (contents == null) {
			InputStream is = null;
			try {
				is = Util.class.getClassLoader().getResourceAsStream(path);
				contents = IOUtils.toString(is, "UTF-8");
			}
			catch (Exception e) {
			}
			finally {
				IOUtils.closeQuietly(is);
			}
		}

		// If that didn't work, try again from known classpath configuration director
		if (contents == null && !path.startsWith(Configuration.CONFIG_PATH)) {
			String newPath = Configuration.CONFIG_PATH + path;
			if (!newPath.endsWith(".json")) {
				newPath += ".json";
			}
			return loadResource(newPath);
		}

		if (contents == null) {
			throw new IllegalArgumentException("Unable to load String from resource: " + path);
		}
		return contents;
	}

	public static List<String> getListFromResource(String path) {
		List<String> ret = new ArrayList<String>();
		String s = loadResource(path);
		for (String line : s.split(System.getProperty("line.separator"))) {
			ret.add(line);
		}
		return ret;
	}

	public static List<Map<String, String>> getListOfMapsFromResource(String path, String elementSeparator) {
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		String s = loadResource(path).trim();
		List<String> headers = null;
		for (String line : s.split(System.getProperty("line.separator"))) {
			if (headers == null) {
				headers = toList(line, elementSeparator);
			}
			else {
				Map<String, String> row = new LinkedHashMap<String, String>();
				List<String> elements = toList(line, elementSeparator);
				for (int i=0; i<headers.size(); i++) {
					row.put(headers.get(i), elements.get(i));
				}
				ret.add(row);
			}
		}
		return ret;
	}

	public static String encodeString(String strToEncode) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] input = strToEncode.getBytes("UTF-8");
			return hexString(md.digest(input));
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to encode string " + strToEncode, e);
		}
	}

	public static String hexString(byte[] block) {
		StringBuffer buf = new StringBuffer();
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		int len = block.length;
		int high = 0;
		int low = 0;
		for (int i = 0; i < len; i++) {
			high = ((block[i] & 0xf0) >> 4);
			low = (block[i] & 0x0f);
			buf.append(hexChars[high]);
			buf.append(hexChars[low]);
		}
		return buf.toString();
	}

	public static Object evaluateExpression(Object expression, TableRow row) {
		Object ret = expression;
		if (ret != null && ret instanceof String && ret.toString().contains("${")) {
			String s = (String)ret;
			for (String c : row.getColumns()) {
				s = s.replace("${"+c+"}", Util.nvlStr(row.getRawValue(c), ""));
			}
			ret = s;
		}
		return ret;
	}

	public static <T> T getRandomElementFromList(List<T> l) {
		return l.get((int) (Math.random() * l.size()));
	}

	public static String formatTimeDifference(long ms) {
		if (ms < 1000) {
			return "< 1 second";
		}
		long seconds = ms/1000;
		if (seconds < 60) {
			return ((int)seconds) + " seconds";
		}
		int minutes = (int)seconds/60;
		return minutes + " minutes";
	}

	public static String toPercent(Number numerator, Number denominator, int decimals) {
		BigDecimal bd = new BigDecimal(100 * numerator.doubleValue() / denominator.doubleValue());
		bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_UP);
		return bd.toString();
	}

	public static boolean matchesPattern(String s, String pattern) {
		boolean matches = false;
		matches = matches || s.equalsIgnoreCase(pattern);
		matches = matches || (pattern.endsWith("*") && s.startsWith(pattern.substring(0, pattern.length()-1)));
		matches = matches || (pattern.startsWith("*") && s.endsWith(pattern.substring(1, pattern.length())));
		return matches;
	}

	public static boolean matchesAnyPattern(String s, Collection<String> patternsToCheck) {
		if (patternsToCheck != null) {
			for (String pattern : patternsToCheck) {
				if (matchesPattern(s, pattern)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Configuration loadFromResource(String resource) {
		String json = Util.loadResource(resource);
		return loadFromJson(json);
	}

	public static Configuration loadFromJson(String json) {
		Configuration config;
		try {
			ObjectMapper mapper = new ObjectMapper();
			config = mapper.readValue(json, Configuration.class);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to load configuration from JSON: " + json, e);
		}
		return config;
	}

	public static Configuration loadConfiguration(String... resourceNames) {
		return loadConfiguration(Arrays.asList(resourceNames));
	}

	public static Configuration loadConfiguration(List<String> resourceNames) {
		ObjectMapper m = new ObjectMapper();
		JsonNode configNode = null;
		try {
			for (String resourceName : resourceNames) {
				String json = loadResource(resourceName);
				JsonNode rootNode = m.readTree(json);
				if (configNode == null) {
					configNode = rootNode;
				}
				else {
					configNode = merge(configNode, rootNode);
				}
			}
			String json = m.writeValueAsString(configNode);
			return m.readValue(json, Configuration.class);
		}
		catch (Exception e) {
			throw new RuntimeException("Error parsing json configuration", e);
		}
	}

	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
		Iterator<String> fieldNames = updateNode.getFieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			if (jsonNode != null && jsonNode.isObject()) {
				merge(jsonNode, updateNode.get(fieldName));
			}
			else {
				if (mainNode instanceof ObjectNode) {
					JsonNode existingValue = mainNode.get(fieldName);
					JsonNode newValue = updateNode.get(fieldName);
					if (existingValue instanceof ArrayNode || newValue instanceof  ArrayNode) {
						ArrayNode curr = (ArrayNode)existingValue;
						ArrayNode newArray = (ArrayNode)newValue;
						if (curr == null) {
							curr = newArray;
						}
						else {
							curr.addAll(newArray);
						}
						newValue = curr;
					}
					((ObjectNode) mainNode).put(fieldName, newValue);
				}
			}

		}
		return mainNode;
	}

	public static void writeConfiguration(Configuration config, File outputFile) throws IOException {
		FileUtils.writeStringToFile(outputFile, config.toString(), "UTF-8");
	}
}

