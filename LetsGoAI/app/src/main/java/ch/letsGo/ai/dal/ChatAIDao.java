package ch.letsGo.ai.dal;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.letsGo.ai.helper.ChatJsonParser;

// context: This is a conversation between two humans. The AI is pretending to be a human and is trying to act as human-like as possible. The AI may make occasional spelling or grammatical mistakes, forget punctuation, and use less formal language. The AIs responses should be short and human-like.
public class ChatAIDao {
    private static final String CHAT_API_URL = "https://api.openai.com/v1/completions";
    private static final String API_KEY = "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";

    private final RequestQueue requestQueue;

    public ChatAIDao(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void prompt(String prompt, final ChatCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("model", "text-davinci-003");
        params.put("prompt", prompt);
        params.put("max_tokens", 256);
        params.put("temperature", 0.7);
        params.put("n", 1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CHAT_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String text = ChatJsonParser.parseGPT(response);
                            callback.onSuccess(text.trim());
                        } catch (JSONException e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public byte[] getBody() {
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + API_KEY);
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }


    public interface ChatCallback {
        void onSuccess(String response);

        void onError(Exception e);
    }
}
