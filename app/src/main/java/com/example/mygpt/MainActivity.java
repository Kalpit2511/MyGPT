package com.example.mygpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeText;
    EditText editText;
    ImageButton sendButton;
    List<message> messageList;
    messageAdapter msgAdapter;

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeText = findViewById(R.id.welcome_text);
        editText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        // Setup Recycler view
        msgAdapter = new messageAdapter(messageList);
        recyclerView.setAdapter(msgAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = editText.getText().toString().trim();
            addToChat(question,message.SENT_BY_ME);
            editText.setText("");
            callAPI(question);
            welcomeText.setVisibility(View.GONE);

        });

    }

    // Method for adding user method to messageList on UI thread
    void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new message(message,sentBy));
                msgAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(msgAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,message.SENT_BY_BOT);
    }

    // Method for setting up and calling API
    void callAPI(String question){

        messageList.add(new message("Typing....",message.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(new JSONObject().put("role", "user").put("content", question));
            jsonBody.put("messages", messagesArray);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Code for calling API
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);// Converting JSON body to Request Body
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer #your secret key")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Response failure due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());

                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
                else {
                    addResponse("Response failure on "+response.body().string());
                }
            }
        });
    }
}