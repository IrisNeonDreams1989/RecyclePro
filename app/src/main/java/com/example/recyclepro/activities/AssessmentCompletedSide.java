package com.example.recyclepro.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclepro.R;
import com.example.recyclepro.adapter.AssessmentCompletedAdapter;
import com.example.recyclepro.adapter.ProductAdapter;
import com.example.recyclepro.dynamoDB.DynamoDBManager;
import com.example.recyclepro.models.AssessmentCompleted;
import com.example.recyclepro.models.Product;

import java.util.ArrayList;

public class AssessmentCompletedSide extends AppCompatActivity {
    private RecyclerView rcvAssessmentCompleted;
    private DynamoDBManager dynamoDBManager;
    private ArrayList<AssessmentCompleted> listAssessmentCompleted;
    private AssessmentCompletedAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assessment_completed_side);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.assessmentCompletedSide), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rcvAssessmentCompleted=findViewById(R.id.rcvAssessmentCompleted);
        dynamoDBManager=new DynamoDBManager(this);
        listAssessmentCompleted=new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAssessmentCompleted.setLayoutManager(linearLayoutManager);
        adapter= new AssessmentCompletedAdapter(listAssessmentCompleted);

        rcvAssessmentCompleted.setAdapter(adapter);
        listAssessmentCompleted.clear();
        dynamoDBManager.loadAssessmentCompleted(new DynamoDBManager.LoadAssessmentCompletedListListener() {
            @Override
            public void onLoadCompleted(String id,String customerName, String productName, double finalPrice, String time, double avgRating) {
                Log.d("CheckOnLoadCompleted", customerName+productName+finalPrice+time+avgRating);
                AssessmentCompleted assessmentCompleted=new AssessmentCompleted(id, customerName, productName, time, finalPrice, avgRating);
                listAssessmentCompleted.add(assessmentCompleted);
                adapter.notifyDataSetChanged();
            }
        });

    }
}