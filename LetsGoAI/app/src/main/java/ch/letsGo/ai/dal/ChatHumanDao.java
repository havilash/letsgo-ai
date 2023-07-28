package ch.letsGo.ai.dal;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.letsGo.ai.helper.ChatJsonParser;
import ch.letsGo.ai.model.Chat;
import ch.letsGo.ai.model.Message;

public class ChatHumanDao {
    private static final String BASE_CHAT_URL = "http://192.168.55.15:8000/api/v1/chat/";
    private static final String CREATE_CHAT = BASE_CHAT_URL + "new";

    private final RequestQueue requestQueue;

    public ChatHumanDao(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }


    public void getChats(final ChatsCallBack callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_CHAT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Chat> chat = ChatJsonParser.parseChats(response);
                            callback.onSuccess(chat);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void getChatById(final String id, final ChatCallBack callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_CHAT_URL + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(id);
                        try {
                            Chat chat = ChatJsonParser.parseChat(response);
                            callback.onSuccess(chat);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void postChat(final Chat chat, final ChatCallBack callback) throws JSONException {
        Map<String, Object> params = new HashMap<>();
        params.put("status", chat.getStatus());
        JSONArray messagesJsonArray = new JSONArray();
        for (Message message : chat.getMessageList()) {
            JSONObject messageJsonObject = new JSONObject();
            messageJsonObject.put("sender", message.getSender());
            messageJsonObject.put("text", message.getText());
            messagesJsonArray.put(messageJsonObject);
        }
        params.put("messages", messagesJsonArray);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_CHAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Chat chat = ChatJsonParser.parseInsertedChatId(response);
                            callback.onSuccess(chat);
                        } catch (Exception e) {
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                byte[] body = new JSONObject(params).toString().getBytes();
                return body;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void updateChat(final String id, final Chat chat, final ChatCallBack callback) throws JSONException {
        Map<String, Object> params = new HashMap<>();
        params.put("status", chat.getStatus());
        JSONArray messagesJsonArray = new JSONArray();
        for (Message message : chat.getMessageList()) {
            JSONObject messageJsonObject = new JSONObject();
            messageJsonObject.put("sender", message.getSender());
            messageJsonObject.put("text", message.getText());
            messagesJsonArray.put(messageJsonObject);
        }
        params.put("messages", messagesJsonArray);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, BASE_CHAT_URL + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Chat chat = ChatJsonParser.parseChat(response);
                            callback.onSuccess(chat);
                        } catch (Exception e) {
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                byte[] body = new JSONObject(params).toString().getBytes();
                return body;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void deleteChat(final String id, final ChatCallBack callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, BASE_CHAT_URL + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Chat chat = ChatJsonParser.parseChat(response);
                            callback.onSuccess(chat);
                        } catch (Exception e) {
                            callback.onError(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });
        requestQueue.add(stringRequest);
    }



    public interface ChatCallBack {
        void onSuccess(Chat chat);

        void onError(Exception e);
    }

    public interface ChatsCallBack {
        void onSuccess(List<Chat> chat) throws JSONException;

        void onError(Exception e);
    }
}
