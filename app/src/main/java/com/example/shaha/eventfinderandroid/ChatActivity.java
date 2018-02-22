package com.example.shaha.eventfinderandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shaha.eventfinderandroid.Adapters.MessageAdapter;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    protected static CloudTableClient tableClient;
    protected static CloudTable table;
    protected final static String tableName = "messagesForEvents";
    private MyEvent curEvent;
    private static boolean tableIsReady = false;
    private ListView msgListView;
    private List<EventMessage> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get the intent and the event name
        Intent intent = getIntent();
        curEvent = intent.getParcelableExtra("Event");

        TextView eventNameTv = (TextView) findViewById(R.id.chat_event_name);
        eventNameTv.setText(curEvent.getEventName());

        msgListView = findViewById(R.id.chat_msg_list_view);

        //create the table first if not exist
        setUpTableTask task = new setUpTableTask();
        task.execute();

        ImageButton sendBtn = findViewById(R.id.chat_send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
    }

    private class setUpTableTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            setupCloud();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tableIsReady = true;

            RetrieveMessagesTask task = new RetrieveMessagesTask();
            task.execute();
        }
    }

    private void setupCloud() {
        try {
            // Setup the cloud storage account.
            CloudStorageAccount account = CloudStorageAccount
                    .parse(MainActivity.storageConnectionString);

            // Create a table service client.
            tableClient = account.createCloudTableClient();

            // Retrieve a reference to a table.
            // Append a random UUID to the end of the table name so that this
            // sample can be run more than once in quick succession.
            table = tableClient.getTableReference(tableName);

            // Create the table if it doesn't already exist.
            table.createIfNotExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendMsg() {
        //get the message first
        EditText msgText = findViewById(R.id.chat_msg_edit_text);
        String msg = msgText.getText().toString();
        if (msg != null) {
            msgText.setText("");
            MessageEntity messageEntity = new MessageEntity(curEvent.getEventID(), curEvent.getEventName());
            messageEntity.setMessage(msg);
            messageEntity.setUser("Unknown");
            if (tableIsReady) {
                //perform insert on a thread
                InsertMsgTask task = new InsertMsgTask();
                task.execute(messageEntity);
            }
        }
    }

    private class InsertMsgTask extends AsyncTask<MessageEntity, Void, Void> {

        @Override
        protected Void doInBackground(MessageEntity... messageEntities) {
            try {
                insertEntity(messageEntities[0]);
            } catch (StorageException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void insertEntity(MessageEntity entity) throws StorageException {
        // Note: the limitations on an insert operation are
        // - the serialized payload must be 1 MB or less
        // - up to 252 properties in addition to the partition key, row key and
        // timestamp. 255 properties in total
        // - the serialized payload of each property must be 64 KB or less

        // Create an operation to add the new customer to the messages table.
        TableOperation insertMsg = TableOperation.insert(entity);

        // Submit the operation to the table service.
        table.execute(insertMsg);
    }

    private class getUsernameTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    public void retrieveMessages() {
        // Retrieve all entities in a partition.
        // Create a filter condition where the partition key is "Smith".
        String partitionFilter = TableQuery.generateFilterCondition(
                "PartitionKey", TableQuery.QueryComparisons.EQUAL, String.valueOf(curEvent.getEventID()));

        // Specify a partition query, using "Smith" as the partition key filter.
        TableQuery<MessageEntity> partitionQuery = TableQuery.from(
                MessageEntity.class).where(partitionFilter);

        // Loop through the results, displaying information about the entity.
        for (MessageEntity entity : table.execute(partitionQuery)) {
            //create Entity
            messages.add(new EventMessage(entity.getMessage(), entity.getUser()));
        }
    }

    private class RetrieveMessagesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            retrieveMessages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MessageAdapter mAdapter = new MessageAdapter(ChatActivity.this, messages);
            msgListView.setAdapter(mAdapter);
        }
    }
}
