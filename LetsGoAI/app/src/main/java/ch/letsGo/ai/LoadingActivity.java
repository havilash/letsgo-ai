package ch.letsGo.ai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ch.letsGo.ai.dal.ChatHumanDao;
import ch.letsGo.ai.dal.FactDao;
import ch.letsGo.ai.model.Chat;
import ch.letsGo.ai.model.Fact;

public class LoadingActivity extends AppCompatActivity {
    private static final int SWITCH_ICON_DELAY = 3000; // 3s
    private static final int FETCH_FACT_DELAY = 7500; // 7.5s
    private static final int SEARCH_CHAT_DELAY = 2000; // 2s
    private static final int MINIMUM_DELAY = 7000; // 7s
    private static final int MAXIMUM_DELAY = 20000; // 20s
    private static final int SEARCH_TIMEOUT = 30000; // 30s

    private ChatHumanDao chatHumanDao;

    private Handler handler = new Handler();
    private ImageView iconImageView;
    private TextView factTextView;
    private FactDao factDao;
    private boolean isAccountIcon = true;
    private boolean cancelChanging = false;  // cancel changing to ai
    private String myChatId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        factDao = new FactDao(this);
        chatHumanDao = new ChatHumanDao(this);

        iconImageView = findViewById(R.id.icon_image_view);
        factTextView = findViewById(R.id.fact_text_view);

        handler.post(switchIconRunnable);
        handler.post(fetchFactRunnable);

        boolean humanOrAi = Math.random() < 0.5;  // human = true, ai = false
        humanOrAi = true;
        Log.d("humanOrAi", String.valueOf(humanOrAi));
        if (humanOrAi) {
            findChat();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!cancelChanging) {
                        deleteChat();
                        Intent intent = new Intent(LoadingActivity.this, ChatAIActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, SEARCH_TIMEOUT);
        } else {
            int delay = (int)(Math.random() * (MAXIMUM_DELAY - MINIMUM_DELAY + 1) + MINIMUM_DELAY);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoadingActivity.this, ChatAIActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, delay);
        }
    }

    private void createChat() throws JSONException {
        chatHumanDao.postChat(new Chat("searching", new ArrayList<>()), new ChatHumanDao.ChatCallBack(){
            @Override
            public void onSuccess(Chat chat) {
                myChatId = chat.getId();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // find a chat with searching status else create one and search for another player
    private void findChat() {
        chatHumanDao.getChats(new ChatHumanDao.ChatsCallBack(){
            @Override
            public void onSuccess(List<Chat> chats) throws JSONException {
                boolean found = false;
                for (Chat chat : chats) {
                    String id = chat.getId();
                    if (chat.getStatus().equals("searching")) {
                        updateChat(id);
                        found = true;
                        cancelChanging = true;
                        Intent intent = new Intent(LoadingActivity.this, ChatHumanActivity.class);
                        intent.putExtra("chatId", id);
                        intent.putExtra("userId", "2");
                        startActivity(intent);
                        finish();
                        break;
                    }
                }
                if(!found) {
                    createChat();
                    handler.post(searchPlayer);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    };

    // update chat with full (2 players) status
    private void updateChat(String id) throws JSONException {
        chatHumanDao.updateChat(id, new Chat("full", new ArrayList<>()), new ChatHumanDao.ChatCallBack(){
            @Override
            public void onSuccess(Chat chat) {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // chat is searching for a second player
    private Runnable searchPlayer = new Runnable() {
        @Override
        public void run() {
            if (myChatId == null) {
                handler.postDelayed(this, SEARCH_CHAT_DELAY);
                return;
            }
            chatHumanDao.getChatById(myChatId, new ChatHumanDao.ChatCallBack(){
                @Override
                public void onSuccess(Chat chat) {
                    if (chat.getStatus().equals("full")){
                        myChatId = chat.getId();
                        cancelChanging = true;
                        Intent intent = new Intent(LoadingActivity.this, ChatHumanActivity.class);
                        intent.putExtra("chatId", myChatId);
                        intent.putExtra("userId", "1");
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });

            handler.postDelayed(this, SEARCH_CHAT_DELAY);
        }
    };

    // loading icon
    private Runnable switchIconRunnable = new Runnable() {
        @Override
        public void run() {
            // Switch the icon and start an animation
            if (isAccountIcon) {
                iconImageView.setImageResource(R.drawable.baseline_computer_24);
                iconImageView.getLayoutParams().width = (int) (200 * getResources().getDisplayMetrics().density);
                iconImageView.getLayoutParams().height = (int) (200 * getResources().getDisplayMetrics().density);
                iconImageView.requestLayout();
                iconImageView.startAnimation(AnimationUtils.loadAnimation(LoadingActivity.this, android.R.anim.fade_in));
            } else {
                iconImageView.setImageResource(R.drawable.baseline_account_circle_24);
                iconImageView.getLayoutParams().width = (int) (256 * getResources().getDisplayMetrics().density);
                iconImageView.getLayoutParams().height = (int) (256 * getResources().getDisplayMetrics().density);
                iconImageView.requestLayout();
                iconImageView.startAnimation(AnimationUtils.loadAnimation(LoadingActivity.this, android.R.anim.fade_in));
            }
            isAccountIcon = !isAccountIcon;

            // Post the runnable with delay
            handler.postDelayed(this, SWITCH_ICON_DELAY);
        }
    };

    // fetch facts/jokes
    private Runnable fetchFactRunnable = new Runnable() {
        @Override
        public void run() {
            if (Math.random() < 0.5) {
                factDao.getFact(new FactDao.FactCallback() {
                    @Override
                    public void onSuccess(Fact fact) {
                        updateFactTextView(fact);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                double rand = Math.random();
                if (rand < 0.33) {
                    factDao.getJoke(new FactDao.FactCallback() {
                        @Override
                        public void onSuccess(Fact joke) {
                            updateFactTextView(joke);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else if (rand < 0.66) {
                    factDao.getDadJoke(new FactDao.FactCallback() {
                        @Override
                        public void onSuccess(Fact joke) {
                            updateFactTextView(joke);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    factDao.getChuckNorrisJoke(new FactDao.FactCallback() {
                        @Override
                        public void onSuccess(Fact joke) {
                            updateFactTextView(joke);
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            // Post the runnable with delay
            handler.postDelayed(this, FETCH_FACT_DELAY);
        }
    };

    private void updateFactTextView(Fact fact) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                factTextView.setText(fact.getText());
                Animation fadeIn = AnimationUtils.loadAnimation(LoadingActivity.this, android.R.anim.fade_in);
                fadeIn.setDuration(500);
                factTextView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        factTextView.startAnimation(fadeOut);
    }

    private void deleteChat() {
        chatHumanDao.deleteChat(myChatId, new ChatHumanDao.ChatCallBack(){
            @Override
            public void onSuccess(Chat chat) {
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void destroy() {
        handler.removeCallbacks(switchIconRunnable);
        handler.removeCallbacks(fetchFactRunnable);
        handler.removeCallbacks(searchPlayer);
        cancelChanging = true;
//        if (myChatId != null) {
//            deleteChat();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }
}
