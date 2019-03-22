package com.vivant.annecharlotte.go4lunch.chat;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.firestore.MessageHelper;
import com.vivant.annecharlotte.go4lunch.firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.firestore.Message;
import com.vivant.annecharlotte.go4lunch.firestore.User;
import com.vivant.annecharlotte.go4lunch.R;
import com.vivant.annecharlotte.go4lunch.authentification.BaseActivity;
import com.vivant.annecharlotte.go4lunch.utils.MyDateFormat;
import java.util.Objects;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    // FOR DESIGN
    private RecyclerView recyclerView;
    private TextView textViewRecyclerViewEmpty;
    private TextInputEditText editTextMessage;
    private ImageButton restaurantMessage;
    private ImageButton goodMessage;

    // FOR DATA
    private ChatAdapter chatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String currentChatName;
    private String today;

    // STATIC DATA FOR CHAT
    private static final String CHAT_NAME_RESTAURANT = "restaurant";
    private static final String CHAT_NAME_GOOD = "goodtime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button sendMessageBtn;

        recyclerView = findViewById(R.id.activity_user_chat_recycler_view);
        textViewRecyclerViewEmpty = findViewById(R.id.activity_user_chat_text_view_recycler_view_empty);
        editTextMessage = findViewById(R.id.activity_user_chat_message_edit_text);
        sendMessageBtn = findViewById(R.id.activity_user_chat_send_button);
        goodMessage = findViewById(R.id.activity_user_chat_good_chat_button);
        restaurantMessage = findViewById(R.id.activity_user_chat_restaurant_chat_button);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendMessage();
            }
        });

        restaurantMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChatButtons(restaurantMessage);
            }
        });

        goodMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickChatButtons(goodMessage);
            }
        });

        today = (new MyDateFormat()).getTodayDate();

        this.configureRecyclerView(CHAT_NAME_RESTAURANT);
        this.configureToolbar();
        this.getCurrentUserFromFirestore();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_chat; }

    // --------------------
    // ACTIONS
    // --------------------

    public void onClickSendMessage() {
        if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null){
                        // SEND A TEXT MESSAGE
                MessageHelper.createMessageForChat(Objects.requireNonNull(editTextMessage.getText()).toString(), this.currentChatName, modelCurrentUser, today).addOnFailureListener(this.onFailureListener());
                this.editTextMessage.setText("");
        }
    }

    public void onClickChatButtons(ImageButton imageButton) {
        switch (Integer.valueOf(imageButton.getTag().toString())){
            case 10:
                this.configureRecyclerView(CHAT_NAME_RESTAURANT);
                break;
            case 20:
                this.configureRecyclerView(CHAT_NAME_GOOD);
                break;
        }
    }

    // --------------------
    // REST REQUESTS
    // --------------------

    private void getCurrentUserFromFirestore(){
        UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    // --------------------
    // UI
    // --------------------

    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        if (chatName.equals(CHAT_NAME_RESTAURANT)) {
            this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllTodayMessageForChat(this.currentChatName, today)), Glide.with(this), this, Objects.requireNonNull(this.getCurrentUser()).getUid());
        } else {
            this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(MessageHelper.getAllMessageForChat(this.currentChatName)), Glide.with(this), this, Objects.requireNonNull(this.getCurrentUser()).getUid());
        }


        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.chatAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    // --------------------
    // CALLBACK
    // --------------------

    @Override
    public void onDataChanged() {
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


