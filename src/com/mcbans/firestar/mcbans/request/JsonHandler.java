package com.mcbans.firestar.mcbans.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mcbans.firestar.mcbans.ActionLog;
import com.mcbans.firestar.mcbans.MCBans;
import com.mcbans.firestar.mcbans.org.json.JSONException;
import com.mcbans.firestar.mcbans.org.json.JSONObject;

public class JsonHandler {
    private final MCBans plugin;
    private final ActionLog log;

    public JsonHandler(MCBans plugin) {
        this.plugin = plugin;
        this.log = plugin.getLog();
    }

    public JSONObject get_data(String json_text) {
        try {
            return new JSONObject(json_text);
        } catch (JSONException e) {
            if (plugin.getConfigs().isDebug()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, String> mainRequest(HashMap<String, String> items) {
        HashMap<String, String> out = new HashMap<String, String>();
        String url_req = this.urlparse(items);
        String json_text = this.request_from_api(url_req);
        JSONObject output = this.get_data(json_text);
        if (output != null) {

            Iterator<String> i = output.keys();
            if (i != null) {
                while (i.hasNext()) {
                    String next = i.next();
                    try {
                        out.put(next, output.getString(next));
                    } catch (JSONException e) {
                        if (plugin.getConfigs().isDebug()) {
                            log.severe("JSON Error On Retrieve");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return out;
    }

    public JSONObject hdl_jobj(HashMap<String, String> items) {
        String urlReq = urlparse(items);
        String jsonText = request_from_api(urlReq);
        return get_data(jsonText);
    }

    public String request_from_api(String data) {
        try {
            if (plugin.getConfigs().isDebug()) {
                log.info("Sending request!");
            }
            URL url = new URL("http://" + plugin.apiServer + "/v2/" + plugin.getConfigs().getApiKey());
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            StringBuilder buf = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                buf.append(line);
            }
            String result = buf.toString();
            if (plugin.getConfigs().isDebug()) {
                log.info(result);
            }
            wr.close();
            rd.close();
            return result;
        } catch (Exception e) {
            if (plugin.getConfigs().isDebug()) {
                if (plugin != null) {
                    log.severe("Fetch Data Error");
                }
                e.printStackTrace();
            }
            return "";
        }
    }

    public String request_from_api(String data, String server) {
        try {
            if (plugin.getConfigs().isDebug()) {
                log.info("Sending request!");
            }
            URL url = new URL("http://" + server + "/v2/" + plugin.getConfigs().getApiKey());
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            StringBuilder buf = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                buf.append(line);
            }
            String result = buf.toString();
            if (plugin.getConfigs().isDebug()) {
                log.info(result);
            }
            wr.close();
            rd.close();
            return result;
        } catch (Exception e) {
            if (plugin.getConfigs().isDebug()) {
                if (plugin != null) {
                    log.severe("Fetch Data Error");
                }
                e.printStackTrace();
            }
            return "";
        }
    }

    public String urlparse(HashMap<String, String> items) {
        String data = "";
        try {
            for (Map.Entry<String, String> entry : items.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();
                if (data.equals("")) {
                    data = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
                } else {
                    data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(val, "UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            if (plugin.getConfigs().isDebug()) {
                e.printStackTrace();
            }
        }
        return data;
    }
}