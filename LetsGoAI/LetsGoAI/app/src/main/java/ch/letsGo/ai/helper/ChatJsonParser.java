package ch.letsGo.ai.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ch.letsGo.ai.model.Chat;
import ch.letsGo.ai.model.Message;

public class ChatJsonParser {
    public static String parseGPT(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String text = jsonObject.getJSONArray("choices").getJSONObject(0).getString("text");
        return text.trim();
    }

    public static Chat parseChat(String json) throws JSONException {
        Log.d("asdadasddddddddsssssssssssssssssssssssss", json);
        JSONObject jsonObject = new JSONObject(json);
        String id = jsonObject.getString("_id");
        String status = jsonObject.getString("status");
        JSONArray messages = jsonObject.getJSONArray("messages");
        List<Message> messageList = new ArrayList<>();

        for (int i = 0; i < messages.length(); i++) {
            JSONObject messageObject = messages.getJSONObject(i);
            String sender = messageObject.getString("sender");
            String text = messageObject.getString("text");
            Message message = new Message(sender, text);
            messageList.add(message);
        }

        return new Chat(id, status, messageList);
    }
    public static List<Chat> parseChats(String json) throws JSONException {
        JSONArray chatsJsonArray = new JSONArray(json);
        List<Chat> chatList = new ArrayList<>();

        for (int i = 0; i < chatsJsonArray.length(); i++) {
            JSONObject chatJsonObject = chatsJsonArray.getJSONObject(i);
            String id = chatJsonObject.getString("_id");
            String status = chatJsonObject.getString("status");
            JSONArray messagesJsonArray = chatJsonObject.getJSONArray("messages");
            List<Message> messageList = new ArrayList<>();

            for (int j = 0; j < messagesJsonArray.length(); j++) {
                JSONObject messageJsonObject = messagesJsonArray.getJSONObject(j);
                String sender = messageJsonObject.getString("sender");
                String text = messageJsonObject.getString("text");
                Message message = new Message(sender, text);
                messageList.add(message);
            }

            Chat chat = new Chat(id, status, messageList);
            chatList.add(chat);
        }

        return chatList;
    }

    public static Chat parseInsertedChatId(String json) throws JSONException {
        JSONObject responseJsonObject = new JSONObject(json);
        JSONObject chatResponseJsonObject = responseJsonObject.getJSONObject("chat");
        String insertedId = chatResponseJsonObject.getString("insertedId");
        return new Chat(insertedId);
    }



}



