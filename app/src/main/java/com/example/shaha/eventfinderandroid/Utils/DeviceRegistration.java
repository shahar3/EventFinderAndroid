package com.example.shaha.eventfinderandroid.Utils;

import android.os.AsyncTask;

import com.example.shaha.eventfinderandroid.DeviceEntity;
import com.example.shaha.eventfinderandroid.MainActivity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

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
            prepareDevice();
        }

        private void prepareDevice() {
            DeviceEntity deviceEntity = new DeviceEntity(deviceId);
            InsertDeviceIdTask task = new InsertDeviceIdTask();
            task.execute(deviceEntity);
        }
    }

    private static class InsertDeviceIdTask extends AsyncTask<DeviceEntity, Void, Void> {

        @Override
        protected Void doInBackground(DeviceEntity... deviceEntities) {
            try {
                insertDevice(deviceEntities[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void insertDevice(DeviceEntity entity) throws StorageException {
            // Create an operation to add the new customer to the messages table.
            TableOperation insertMsg = TableOperation.insert(entity);

            // Submit the operation to the table service.
            table.execute(insertMsg);
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
