package com.example.demo.FTPserver.utiles;

import java.io.*;
import java.util.*;

public class Properties {

    private static String POUND = "#";
    private static String EQUAL = "=";

  
    private List<String> keyList = new LinkedList<>();

    private Map<String, String> valueMap = new LinkedHashMap<>();

    public Map<String, String> getPropertyMap() {
        return valueMap;
    }

    public String getProperty(String key) {
        return valueMap.get(key);
    }

    public void setProperty(String key, String value) {
       
        if (!valueMap.containsKey(key)) {
            keyList.add(key);
        }
        valueMap.put(key, value);
    }

    public void delProperty(String key) {
        if (valueMap.containsKey(key)) {
            keyList.removeIf(o -> o.equals(key));
            valueMap.remove(key);
        }
    }

    public synchronized void load(InputStream stream) throws IOException {
        keyList.clear();
        valueMap.clear();
        try (Reader isr = new InputStreamReader(stream);
             BufferedReader reader = new BufferedReader(isr)) {
            while (reader.ready()) {
                readLine(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLine(String line) {
        if (!line.trim().startsWith(POUND) && line.contains(EQUAL)) {
            String k = line.substring(0, line.indexOf(EQUAL)).trim();
            String v = line.substring(line.indexOf(EQUAL) + 1).trim();
            valueMap.put(k, v);
            keyList.add(k);
        } else {
            keyList.add(line);
        }
    }

    public void store(OutputStream stream) {
        try (Writer writer = new OutputStreamWriter(stream);
             BufferedWriter bw = new BufferedWriter(writer)) {
            for (String key : keyList) {
                if (valueMap.containsKey(key)) {
                    bw.write(key + EQUAL + valueMap.get(key));
                } else {
                    bw.write(key);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> keySet() {
        return valueMap.keySet();
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return valueMap.entrySet();
    }

    @Override
    public String toString() {
        return valueMap.toString();
    }
}
