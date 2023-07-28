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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ch.letsGo.ai.dal.ChatAIDao;
import ch.letsGo.ai.model.Message;

public class ChatAIActivity extends AppCompatActivity {
    private static final String AI_CONTEXT = "You’re having a casual chat with a human. The AI is pretending to be human and aims for natural responses. It may make mistakes and use informal language. The AI’s responses should be brief and informal. Don't write anything that precedes the following sign: :. The AI should never reveal that it’s an AI.\n\n";
    private static final int MAX_MESSAGES = 10;
    private static final int AI_MESSAGE_DELAY_MIN = 0;
    private static final int AI_MESSAGE_DELAY_MAX = 1000;
    private static final int AI_MESSAGE_LETTER_DELAY = 150;
    private static final int GUESS_ACTIVITY_DELAY = 5000;
    private ChatAIDao chatAIDao;
    private LinearLayout chatArea;
    private ImageButton sendButton;
    private List<Message> messages = new ArrayList<>();
    private TextView limitTextView;
    private ScrollView scrollView;
    private String userId;
    private Handler handler = new Handler();
    private boolean isUserTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        chatAIDao = new ChatAIDao(this);

        limitTextView = findViewById(R.id.limit_text_view);
        scrollView = findViewById(R.id.chat_area_scroll_view);
        chatArea = findViewById(R.id.chat_area);
        sendButton = findViewById(R.id.send_button);

        isUserTurn = Math.random() < 0.5;

        if (!isUserTurn) {
            sendAIMessage();
        }
    }

    private void delayedSuccess(String response) {
        int responseLength = response.length();
        int delay = (int) (Math.random() * (AI_MESSAGE_DELAY_MAX - AI_MESSAGE_DELAY_MIN) + AI_MESSAGE_DELAY_MIN);
        delay = delay + responseLength * AI_MESSAGE_LETTER_DELAY;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = new Message("ai", response);
                messages.add(message);
                addMessage(message);

                isUserTurn = true;
            }
        }, delay);
    }

    private void sendAIMessage() {
        int delay = (int) (Math.random() * (AI_MESSAGE_DELAY_MAX - AI_MESSAGE_DELAY_MIN) + AI_MESSAGE_DELAY_MIN);

        String prompt = AI_CONTEXT;
        for (Message message : messages) {
            prompt += String.format("%s: %s\n\n", message.getSender(), message.getText());
        }

        chatAIDao.prompt(prompt, new ChatAIDao.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                delayedSuccess(response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                isUserTurn = false;
                showError("An error occurred. Please try again.");
            }

        });
    }

    private void showError(String message) {
        new AlertDialog.Builder(ChatAIActivity.this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ChatAIActivity.this, LoadingActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void addMessage(Message message) {
        LayoutInflater inflater = getLayoutInflater();
        View messageView;

        if (message.getSender().equals("human")) {
            messageView = inflater.inflate(R.layout.message_right, chatArea, false);
        } else {
            messageView = inflater.inflate(R.layout.message_left, chatArea, false);
        }

        TextView messageText = messageView.findViewById(R.id.message_text);
        messageText.setText(message.getText());

        chatArea.addView(messageView);

        limitTextView.setText(String.format("%s/%s", messages.size(), MAX_MESSAGES));

        if (messages.size() == MAX_MESSAGES) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ChatAIActivity.this, GuessActivity.class);
                    intent.putExtra("isHuman", false);
                    startActivity(intent);
                    finish();
                }
            }, GUESS_ACTIVITY_DELAY);
        }

        scrollDown();
    }

    public void onSendButtonClick(View view) {
        if (!isUserTurn) return;

        EditText inputField = findViewById(R.id.input_field);
        String messageText = inputField.getText().toString();
        inputField.setText("");
        Message message = new Message("human", messageText.trim());
        messages.add(message);
        addMessage(message);

        isUserTurn = false;
        sendAIMessage();
    }

    private void scrollDown() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
