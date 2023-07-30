package ch.letsGo.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.letsGo.ai.dal.ChatAIDao;
import ch.letsGo.ai.dal.ChatHumanDao;
import ch.letsGo.ai.model.Chat;
import ch.letsGo.ai.model.Fact;
import ch.letsGo.ai.model.Message;

public class ChatHumanActivity extends AppCompatActivity {
    private static final int MAX_MESSAGES = 10;
    private static final int RECEIVE_DELAY = 1000;
    private static final int GUESS_ACTIVITY_DELAY = 5000;
    private ChatHumanDao chatHumanDao;
    private LinearLayout chatArea;
    private ScrollView scrollView;
    private TextView limitTextView;
    private List<Message> messages = new ArrayList<>();
    private String userId;
    private String chatId;
    private String otherUserId;
    private boolean isMyTurn;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        chatId = intent.getStringExtra("chatId");

        chatHumanDao = new ChatHumanDao(this);

        limitTextView = findViewById(R.id.limit_text_view);
        scrollView = (ScrollView) findViewById(R.id.chat_area_scroll_view);
        chatArea = findViewById(R.id.chat_area);

        if (userId.equals("1")){
            isMyTurn = true;
            otherUserId = "2";
        }else{
            isMyTurn = false;
            otherUserId = "1";
            handler.post(receive);
        }
    }

    private void send() throws JSONException {
        chatHumanDao.updateChat(chatId, new Chat("full", messages), new ChatHumanDao.ChatCallBack(){
            @Override
            public void onSuccess(Chat chat) {

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Runnable receive = new Runnable() {
        @Override
        public void run() {
            chatHumanDao.getChatById(chatId, new ChatHumanDao.ChatCallBack(){
                @Override
                public void onSuccess(Chat chat) {
                    List<Message> newMessageList = chat.getMessageList();
                    if (newMessageList.size() > messages.size()){
                        Message message = newMessageList.get(newMessageList.size() - 1);
                        messages.add(message);
                        addMessage(message);
                        isMyTurn = true;
                        handler.removeCallbacks(receive);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Intent intent = new Intent(ChatHumanActivity.this, LoadingActivity.class);
                    startActivity(intent);
                    finish();

                    e.printStackTrace();
                }
            });

            handler.postDelayed(this, RECEIVE_DELAY);
        }
    };

    private void addMessage(Message message) {
        LayoutInflater inflater = getLayoutInflater();
        View messageView;

        if (message.getSender().equals(userId)) {
            messageView = inflater.inflate(R.layout.message_right, chatArea, false);
        } else {
            messageView = inflater.inflate(R.layout.message_left, chatArea, false);
        }

        TextView messageText = messageView.findViewById(R.id.message_text);
        messageText.setText(message.getText());

        chatArea.addView(messageView);

        limitTextView.setText(String.format("%s/%s", messages.size(), MAX_MESSAGES));

        if (messages.size() >= MAX_MESSAGES) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ChatHumanActivity.this, GuessActivity.class);
                    intent.putExtra("isHuman", true);
                    startActivity(intent);
                    finish();
                }
            }, GUESS_ACTIVITY_DELAY);
        }

        scrollDown();
    }

    public void onSendButtonClick(View view) {
        if (!isMyTurn || messages.size() >= MAX_MESSAGES ) return;
        Log.d("Message", messages.toString());

        EditText inputField = findViewById(R.id.input_field);
        String messageText = inputField.getText().toString();
        inputField.setText("");
        Message message = new Message(userId, messageText.trim());
        Log.d("Message", messages.toString());
        messages.add(message);
        addMessage(message);
        try {
            send();
        } catch (JSONException e) {
            // handle the exception here
            e.printStackTrace();
        }

        isMyTurn = false;
        handler.post(receive);
    }

    private void showError(String message) {
        new AlertDialog.Builder(ChatHumanActivity.this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChatHumanActivity.this, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void scrollDown() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void deleteChat() {
        chatHumanDao.deleteChat(chatId, new ChatHumanDao.ChatCallBack(){
            @Override
            public void onSuccess(Chat chat) {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(receive);
        deleteChat();
    }
}