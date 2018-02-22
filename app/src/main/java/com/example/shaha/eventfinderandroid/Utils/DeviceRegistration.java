package com.example.shaha.eventfinderandroid.Utils;

import android.os.AsyncTask;

import com.example.shaha.eventfinderandroid.DeviceEntity;
import com.example.shaha.eventfinderandroid.MainActivity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;

/**
 * Created by shaha on 22/02/2018.
 */

public class DeviceRegistration {
    protected static CloudTableClient tableClient;
    protected static CloudTable table;
    protected final static String tableName = "registeredDevices";
    private static String deviceId;

    public static void registerDevice(String deviceRegId) {
        deviceId = deviceRegId;

        //create azure table
        CreateRegTableTask task = new CreateRegTableTask();
        task.execute();
    }

    private static class CreateRegTableTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            setupCloud();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            
        }
    }

    private static class insertDeviceIdTask extends AsyncTask<DeviceEntity,Void,Void>{

        @Override
        protected Void doInBackground(DeviceEntity... deviceEntities) {

            return null;
        }
    }

    private static void setupCloud() {
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
}
