package com.example.recyclepro.dynamoDB;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamoDBManager {

    private Context context;
    public DynamoDBManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }
    public boolean checkDynamoDBConnection() {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            // Thực hiện công việc mạng trong một luồng mới
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ListTablesResult tables = ddbClient.listTables();
                        for (String tableName : tables.getTableNames()) {
                            Log.d("check464", "Table name: " + tableName);
                        }
                    } catch (Exception e) {
                        // Log exception for debugging
                        Log.e("check464", "Error checking DynamoDB connection: " + e.getMessage());
                    }
                }
            }).start();

            // Trả về true vì việc khởi động công việc mạng đã được bắt đầu
            return true;
        } catch (Exception e) {
            // Log exception for debugging
            Log.e("DynamoDBManager", "Error checking DynamoDB connection: " + e.getMessage());
            return false;
        }
    }
    private void initializeDynamoDB() {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAZQ3DNSDFWIYOZ3WD", "I7d0DUQViEuX897VsULvlESCI8fb3zq7iTRxZHB/");

            ddbClient = new AmazonDynamoDBClient(credentials);
            ddbClient.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1)); // Set the region

            ListTablesResult tables = ddbClient.listTables();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private AmazonDynamoDBClient ddbClient;
    public void SubmitProductInformationforRecycling(String productID, String customerName,String phone,String productName, String caseDescribe, String purchasedDate, String battery, String screen, String state, String email, String time) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một mục mới
                        Map<String, AttributeValue> item = new HashMap<>();
                        item.put("_id", new AttributeValue().withS(productID));
                        item.put("customerName", new AttributeValue().withS(customerName));
                        item.put("phone", new AttributeValue().withS(phone));
                        item.put("productName", new AttributeValue().withS(productName));
                        item.put("caseDescribe", new AttributeValue().withS(caseDescribe));
                        item.put("purchasedDate", new AttributeValue().withS(purchasedDate));
                        item.put("battery", new AttributeValue().withS(battery));
                        item.put("screen", new AttributeValue().withS(screen));
                        item.put("state", new AttributeValue().withS(state));
                        item.put("email", new AttributeValue().withS(email));
                        item.put("time", new AttributeValue().withS(time));
                        // Tạo yêu cầu chèn mục vào bảng
                        PutItemRequest putItemRequest = new PutItemRequest()
                                .withTableName("RecyclingProducts")
                                .withItem(item);
                        // Thực hiện yêu cầu chèn mục và nhận kết quả
                        PutItemResult result = ddbClient.putItem(putItemRequest);

                    } catch (Exception e) {
                        Log.e("Error", "Exception occurred: ", e);
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            // Log exception for debugging
            Log.e("DynamoDBManager", "Error checking DynamoDB connection: " + e.getMessage());
        }
    }
    public void loadProduct(String state, LoadRecyclingProductListListener listener) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một yêu cầu truy vấn
                        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
                        Condition condition = new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ.toString())
                                .withAttributeValueList(new AttributeValue().withS(state));
                        scanFilter.put("state", condition);

                        ScanRequest scanRequest = new ScanRequest("RecyclingProducts").withScanFilter(scanFilter);
                        ScanResult scanResult = ddbClient.scan(scanRequest);

                        // Xử lý kết quả
                        for (Map<String, AttributeValue> item : scanResult.getItems()) {
                            String id = item.get("_id").getS();
                            String customerName = item.get("customerName").getS();
                            String productName = item.get("productName").getS();
                            String battery=item.get("battery").getS();
                            String caseDescribe=item.get("caseDescribe").getS();
                            String screen=item.get("screen").getS();
                            String phone=item.get("phone").getS();
                            String purchasedDate=item.get("purchasedDate").getS();
                            String time=item.get("time").getS();
                            String email=item.get("email").getS();
                            // Cập nhật giao diện
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("sequence506", "stage 1 in dynamoDB"+id+customerName+phone+productName+battery+caseDescribe+purchasedDate+screen);
                                    listener.onFound(id, customerName,phone, productName,  battery, caseDescribe, purchasedDate,  screen, time, email);
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface LoadRecyclingProductListListener {
        void onFound(String id,String customerName,String phone, String productName, String battery, String caseDescribe,String purchasedDate,String screen, String time, String email);
    }
    public void createAccount(String email, String password, String userName,String role) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một mục mới
                        Map<String, AttributeValue> item = new HashMap<>();
                        item.put("email", new AttributeValue().withS(email));
                        item.put("password", new AttributeValue().withS(password));
                        item.put("username", new AttributeValue().withS(userName));
                        item.put("role", new AttributeValue().withS(role));
                        // Tạo yêu cầu chèn mục vào bảng
                        PutItemRequest putItemRequest = new PutItemRequest()
                                .withTableName("Users")
                                .withItem(item);

                        // Thực hiện yêu cầu chèn mục và nhận kết quả
                        PutItemResult result = ddbClient.putItem(putItemRequest);

                    } catch (Exception e) {
                        Log.e("Error", "Exception occurred: ", e);
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            // Log exception for debugging
            Log.e("DynamoDBManager", "Error checking DynamoDB connection: " + e.getMessage());
        }
    }
    public boolean checkExistedAccount(String email) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            // Tạo một biến boolean để lưu kết quả kiểm tra
            AtomicBoolean isExisted = new AtomicBoolean(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một yêu cầu truy vấn
                        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
                        Condition condition = new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ.toString())
                                .withAttributeValueList(new AttributeValue().withS(email));
                        scanFilter.put("email", condition);

                        ScanRequest scanRequest = new ScanRequest("Users").withScanFilter(scanFilter);
                        ScanResult scanResult = ddbClient.scan(scanRequest);

                        // Kiểm tra xem có kết quả từ scan hay không
                        if (!scanResult.getItems().isEmpty()) {
                            // Nếu có, đánh dấu là email đã tồn tại và thoát khỏi thread
                            isExisted.set(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(); // Khởi chạy thread

            // Chờ cho thread kết thúc
            Thread.sleep(1000); // Đợi 1 giây

            // Trả về kết quả kiểm tra
            return isExisted.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi xảy ra
        }
    }
    public interface LoadUsersCallback {
        void onLoginSuccess(String role);
        void onLoginFailure();
    }
    public boolean loadUsers(String email, String password, LoadUsersCallback callback) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            // Tạo một biến boolean để lưu kết quả kiểm tra
            AtomicBoolean isAuthenticated = new AtomicBoolean(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một yêu cầu truy vấn
                        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
                        Condition condition = new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ.toString())
                                .withAttributeValueList(new AttributeValue().withS(email));
                        scanFilter.put("email", condition);

                        ScanRequest scanRequest = new ScanRequest("Users").withScanFilter(scanFilter);
                        ScanResult scanResult = ddbClient.scan(scanRequest);

                        for (Map<String, AttributeValue> item : scanResult.getItems()) {
                            String storedPassword = item.get("password").getS(); // Lấy mật khẩu đã lưu
                            Log.d("Check464", storedPassword);
                            // Kiểm tra mật khẩu
                            if (storedPassword.equals(password)) {
                                // Mật khẩu đúng, đánh dấu xác thực thành công và thoát khỏi vòng lặp
                                String role = item.get("role").getS();
                                callback.onLoginSuccess(role);
                                isAuthenticated.set(true);
                                return;
                            } else {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            Thread.sleep(1000);


            return isAuthenticated.get();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void SubmitProductPrice(String reviewID, String productID, String customerName, String phone, String productName,
                                   String caseDescribe, String uptime, String battery,
                                   String screen, String state,
                                   String batteryRating, String batteryCondition,
                                   String caseRating, String caseCondition,
                                   String uptimeRating, String uptimeCondition,
                                   String screenRating, String screenCondition,
                                   String finalPrice, String time, String avgRating, String email) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một mục mới
                        Map<String, AttributeValue> item = new HashMap<>();
                        item.put("_id", new AttributeValue().withS(reviewID));
                        //product
                        Map<String, AttributeValue> product = new HashMap<>();
                        product.put("_id", new AttributeValue().withS(productID));
                        product.put("battery", new AttributeValue().withS(battery));
                        product.put("caseDescribe", new AttributeValue().withS(caseDescribe));
                        product.put("customerName", new AttributeValue().withS(customerName));
                        product.put("phone", new AttributeValue().withS(phone));
                        product.put("productName", new AttributeValue().withS(productName));
                        product.put("uptime", new AttributeValue().withS(uptime));
                        product.put("screen", new AttributeValue().withS(screen));
                        product.put("state", new AttributeValue().withS(state));
                        item.put("product", new AttributeValue().withM(product));
                        //battery
                        Map<String, AttributeValue> battery= new HashMap<>();
                        battery.put("batteryRating", new AttributeValue().withN(batteryRating));
                        battery.put("batteryCondition", new AttributeValue().withS(batteryCondition));
                        item.put("battery", new AttributeValue().withM(battery));
                        //case
                        Map<String, AttributeValue> deviceCase= new HashMap<>();
                        deviceCase.put("caseRating", new AttributeValue().withN(caseRating));
                        deviceCase.put("caseCondition", new AttributeValue().withS(caseCondition));
                        item.put("case", new AttributeValue().withM(deviceCase));
                        //uptime
                        Map<String, AttributeValue> uptime= new HashMap<>();
                        uptime.put("uptimeRating", new AttributeValue().withN(uptimeRating));
                        uptime.put("uptimeCondition", new AttributeValue().withS(uptimeCondition));
                        item.put("uptime", new AttributeValue().withM(uptime));
                        //screen
                        Map<String, AttributeValue> screen= new HashMap<>();
                        screen.put("screenRating", new AttributeValue().withN(screenRating));
                        screen.put("screenCondition", new AttributeValue().withS(screenCondition));
                        item.put("screen", new AttributeValue().withM(screen));
                        //price
                        item.put("finalPrice", new AttributeValue().withN(finalPrice));
                        //time
                        item.put("time", new AttributeValue().withS(time));
                        //avgRating
                        item.put("avgRating", new AttributeValue().withN(avgRating));
                        //email
                        item.put("email", new AttributeValue().withS(email));
                        // Tạo yêu cầu chèn mục vào bảng
                        PutItemRequest putItemRequest = new PutItemRequest()
                                .withTableName("ProductPrices")
                                .withItem(item);
                        // Thực hiện yêu cầu chèn mục và nhận kết quả
                        PutItemResult result = ddbClient.putItem(putItemRequest);

                    } catch (Exception e) {
                        Log.e("Error", "Exception occurred: ", e);
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            // Log exception for debugging
            Log.e("DynamoDBManager", "Error checking DynamoDB connection: " + e.getMessage());
        }
    }

    public void loadAssessmentCompleted(LoadAssessmentCompletedListListener listener) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một yêu cầu truy vấn
                        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
                        // Thêm các điều kiện scan vào để truy vấn dữ liệu
                        Condition condition = new Condition().withComparisonOperator(ComparisonOperator.NOT_NULL);
                        scanFilter.put("_id", condition); // Đảm bảo "_id" tồn tại
                        ScanRequest scanRequest = new ScanRequest("ProductPrices").withScanFilter(scanFilter);
                        ScanResult scanResult = ddbClient.scan(scanRequest);

                        // Xử lý kết quả
                        List<Map<String, AttributeValue>> items = scanResult.getItems();
                        for (Map<String, AttributeValue> item : items) {
                            // Lấy giá trị các trường trong item
                            String id = item.get("_id").getS();
                            String time = item.get("time").getS();
                            Double finalPrice = Double.valueOf(item.get("finalPrice").getN());
                            Double avgRating = Double.valueOf(item.get("avgRating").getN());
                            Map<String, AttributeValue> productMap = item.get("product").getM();
                            // Lấy giá trị các trường trong productMap
                            String productId = productMap.get("_id").getS();
                            String customerName = productMap.get("customerName").getS();
                            String phone = productMap.get("phone").getS();
                            String productName = productMap.get("productName").getS();
                            // Xử lý các trường khác tương tự
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override

                                public void run() {

                                    if(listener != null) {
                                        listener.onLoadCompleted(id,customerName, productName, finalPrice, time, avgRating);
                                    }
                                }
                            });
                            // Do something with the retrieved data

                        }

                        // Gọi listener để thông báo đã load xong

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface LoadAssessmentCompletedListListener {
        void onLoadCompleted(String id, String customerName,String productName, double finalPrice, String time, double avgRating);
    }
    public void updateStateOfProduct(String productID,String state) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Tạo một mục mới
                        Map<String, AttributeValue> key = new HashMap<>();
                        key.put("_id", new AttributeValue().withS(productID));

                        Map<String, AttributeValueUpdate> updates = new HashMap<>();
                        updates.put("state", new AttributeValueUpdate().withValue(new AttributeValue().withS(state)).withAction(AttributeAction.PUT));
                        // Tạo yêu cầu cập nhật
                        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                                .withTableName("RecyclingProducts")
                                .withKey(key)
                                .withAttributeUpdates(updates);

                        // Thực hiện yêu cầu cập nhật và nhận kết quả
                        UpdateItemResult result = ddbClient.updateItem(updateItemRequest);

                    } catch (Exception e) {
                        Log.e("Error", "Exception occurred: ", e);
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            // Log exception for debugging
            Log.e("DynamoDBManager", "Error checking DynamoDB connection: " + e.getMessage());
        }
    }
    public void loadRecyclingSubmission(String email, LoadRecyclingSubmissionListListener listener) {
        try {
            if (ddbClient == null) {
                initializeDynamoDB();
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Log.d("PayAttention", "run"+email);
                        // Tạo một yêu cầu truy vấn
                        HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
                        Condition condition = new Condition()
                                .withComparisonOperator(ComparisonOperator.EQ.toString())
                                .withAttributeValueList(new AttributeValue().withS(email));
                        scanFilter.put("email", condition);
                        ScanRequest scanRequest = new ScanRequest("RecyclingProducts").withScanFilter(scanFilter);
                        ScanResult scanResult = ddbClient.scan(scanRequest);

                        // Xử lý kết quả
                        for (Map<String, AttributeValue> item : scanResult.getItems()) {
                            String id = item.get("_id").getS();
                            String productName = item.get("productName").getS();
                            String time = item.get("time").getS();
                            String state = item.get("state").getS();
                            // Cập nhật giao diện
                            Handler handler = new Handler(Looper.getMainLooper());
                            Log.d("PayAttention", id+productName+time+state);
                            handler.post(new Runnable() {
                                @Override

                                public void run() {
                                  listener.onFound(id, productName,time,state);
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start(); // Khởi chạy thread
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface LoadRecyclingSubmissionListListener {
        void onFound(String id,String productName,String time, String state);
    }

}
