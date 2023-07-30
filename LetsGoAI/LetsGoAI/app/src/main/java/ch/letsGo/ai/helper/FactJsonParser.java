package ch.letsGo.ai.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.letsGo.ai.model.Fact;

public class FactJsonParser {
    public static Fact parseFact(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String text = jsonObject.getString("fact");
        return new Fact(text);
    }

    public static Fact parseJoke(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String text = jsonObject.getString("joke");
        return new Fact(text);
    }

    public static Fact parseDadJoke(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String text = jsonObject.getString("joke");
        return new Fact(text);
    }

    public static Fact parseChuckNorrisJoke(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String text = jsonObject.getString("joke");
        return new Fact(text);
    }
}
