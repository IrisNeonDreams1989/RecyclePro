package com.example.recyclepro.activities;

import static com.example.recyclepro.services.PriceCalculationService.convertRatingToPercentage;
import static com.example.recyclepro.services.PriceCalculationService.costingPrice;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.recyclepro.R;
import com.example.recyclepro.models.ConfigRate;
import com.example.recyclepro.models.Rating;

public class RecyclingAssessmentFragment extends Fragment {
    public static final String TAG= RecyclingAssessmentFragment.class.getName();
    private TextView tvInformation;
    private String productID;
    private String customerName;
    private String phone;
    private String productName;
    private String productBattery;
    private String productCaseDescribe;
    private String productPurchasedDate;
    private String productDescribe;
    private SeekBar sbBattery;
    private SeekBar sbCase;
    private SeekBar sbPurchaseDate;
    private SeekBar sbDescription;
    private TextView tvRating1, tvRating2, tvRating3, tvRating4, tvFinalPrice;
    private Button btnSave;
    private ImageButton btnBack;
    private EditText etPrice;
    private Rating tiLeGiaPin;
    private Rating tiLeGiaVo;
    private Rating tiLeGiaHoatDong;
    private Rating tiLeGiaManHinh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_recycling_assessment, container, false);
        tvInformation=view.findViewById(R.id.tvInformation);
        Bundle bundleReceive=getArguments();
        productID = bundleReceive.getString("productID");
        Log.d("CheckBundle", "product ID: "+productID);
        customerName= bundleReceive.getString("customerName");
        Log.d("CheckBundle", "customer name: "+customerName);
        phone= bundleReceive.getString("phone");
        Log.d("CheckBundle", "phone: "+phone);
        productName = bundleReceive.getString("productName");
        Log.d("CheckBundle", "product name: "+productName);
        productBattery= bundleReceive.getString("productBattery");
        Log.d("CheckBundle", "product battery: "+productBattery);
        productCaseDescribe= bundleReceive.getString("productCaseDescribe");
        Log.d("CheckBundle", "product case describe: "+productCaseDescribe);
        productPurchasedDate= bundleReceive.getString("productPurchasedDate");
        Log.d("CheckBundle", "product purchased date: "+ productPurchasedDate);
        productDescribe= bundleReceive.getString("productDescribe");
        Log.d("CheckBundle", "product describe: "+ productDescribe);
        tvInformation.setText("product ID: " + productID + "\n" +
                "customer name: " + customerName + "\n" +
                "phone: " + phone + "\n" +
                "product name: " + productName + "\n" +
                "product battery: " + productBattery + "\n" +
                "product case describe: " + productCaseDescribe + "\n" +
                "product purchased date: " + productPurchasedDate + "\n" +
                "product describe: " + productDescribe);
        sbBattery=view.findViewById(R.id.sbBattery);
        sbCase=view.findViewById(R.id.sbCase);
        sbPurchaseDate=view.findViewById(R.id.sbPurchasedDate);
        sbDescription=view.findViewById(R.id.sbDescribe);
        tvRating1=view.findViewById(R.id.rating1);
        tvRating2=view.findViewById(R.id.rating2);
        tvRating3=view.findViewById(R.id.rating3);
        tvRating4=view.findViewById(R.id.rating4);
        etPrice=view.findViewById(R.id.etPrice);
        btnSave=view.findViewById(R.id.btnSave);
        btnBack=view.findViewById(R.id.btnBack);
        tvFinalPrice=view.findViewById(R.id.tvFinalPrice);
        sbBattery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Xử lý sự kiện khi giá trị của slider thay đổi
                int rating = progress + 1;
                //Tự lấy dữ liệu từ sqlite đưa vào
                ConfigRate configRateBattery = new ConfigRate(
                        -0.1, //Nếu không có pin thì tăng 10% của 20% (battery: 0.2)
                        0.6, //Nếu pin cháy nổ, phồng giảm 60% của 20% (battery: 0.2)
                        0.3, //Nếu pin chai, phồng giảm 60% của 20% (battery: 0.2)
                        -0.4, // Nếu pin còn trên hoạt động tốt trên mức 80, tăng 60% của 20% (battery: 0.2)
                        -1 // Nếu pin còn hoạt động tốt như mới, tăng 100% của 20% (battery: 0.2)
                );
                tiLeGiaPin = convertRatingToPercentage(rating, configRateBattery, 0.2 /* Dua theo ti set mac dinh hoac tuy chon mien sao tong cua cac moc la 1 */);
                // Cập nhật giá trị hiển thị
                tvRating1.setText("Rating: " + rating);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Xử lý khi bắt đầu chạm vào slider
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Xử lý khi kết thúc chạm vào slider
            }
        });
        sbCase.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Xử lý sự kiện khi giá trị của slider thay đổi
                int rating = progress + 1;
                //Tự lấy dữ liệu từ sqlite đưa vào
                ConfigRate configRateCase = new ConfigRate(
                        1, // Nếu bị nát - thủng - không còn, giảm 100% của 10% (case: 0.1)
                        0.5, // Nếu vỏ bị xước nhiều, qua sửa chửa, giảm 50% của 10% (case: 0.1)
                        0.3, // Nếu vỏ xước ít, đã qua sửa chửa, giảm 20% của 10% (case: 0.1)
                        -0.6, // Nếu vỏ có xước ít không qua sửa chửa, tăng 20% của 10% (case: 0.1)
                        -1 //Như mới, tăng 100% của 10% (case: 0.1)
                );
                tiLeGiaVo = convertRatingToPercentage(rating, configRateCase, 0.1 /* Dua theo ti set mac dinh hoac tuy chon mien sao tong cua cac moc la 1 */);
                // Cập nhật giá trị hiển thị
                tvRating2.setText("Rating: " + rating);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Xử lý khi bắt đầu chạm vào slider
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Xử lý khi kết thúc chạm vào slider
            }
        });
        sbPurchaseDate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Xử lý sự kiện khi giá trị của slider thay đổi
                int rating = progress + 1;


                //Tự lấy dữ liệu từ sqlite đưa vào
                ConfigRate configRateMonitor = new ConfigRate(
                        1, //Nếu không có màn hình, vỡ, thủng, giảm 100% của 30%  (monitor: 0.3)
                        0.4, //Nếu màn hình bị sọc, bị liệt, bóng mờ, xước nhiều, qua sửa chữa, giảm 40% của 30%  (monitor: 0.3)
                        -0.2, //Nếu màn hình xước nhiều, chưa qua sửa chữa, tăng 20% của 30% (monitor: 0.3)
                        -0.6, //Bị xước ít như mới, tăng 60% của 30% (monitor: 0.3)
                        -1 //Như mới, tăng 100% của 30% (monitor: 0.3)
                );
                tiLeGiaManHinh = convertRatingToPercentage(rating, configRateMonitor, 0.3 /* Dua theo ti set mac dinh hoac tuy chon mien sao tong cua cac moc la 1 */);
                // Cập nhật giá trị hiển thị
                tvRating3.setText("Rating: " + rating);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Xử lý khi bắt đầu chạm vào slider
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Xử lý khi kết thúc chạm vào slider
            }
        });
        sbDescription.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Xử lý sự kiện khi giá trị của slider thay đổi
                int rating = progress + 1;


                //Tự lấy dữ liệu từ sqlite đưa vào
                ConfigRate configRateStatus = new ConfigRate(
                        1, //Nếu không hoạt động, vào nước , cháy nổ, biến dạng không nhận dạng được, giảm 100% của 40% (status: 0.4)
                        0.6, //Nếu bị lỗi, không lên nguồn, hoạt động gì hết, giảm 60% của 40%  (status: 0.4)
                        0.5, //Nếu gặp lỗi treo máy, panic, thiếu linh kiện, giảm 50% của 40%  (status: 0.4)
                        -0.6, //Hoạt động bình thường, một vài chức năng nhỏ không hoạt không đáng kể, tăng 50% của 40% (status: 0.4)
                        -1 //Hoạt động bình thường như mới, tăng 100% của 40% (status: 0.4)
                );
                tiLeGiaHoatDong = convertRatingToPercentage(rating, configRateStatus, 0.4 /* Dua theo ti set mac dinh hoac tuy chon mien sao tong cua cac moc la 1 */);


                // Cập nhật giá trị hiển thị
                tvRating4.setText("Rating: " + rating);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Xử lý khi bắt đầu chạm vào slider
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Xử lý khi kết thúc chạm vào slider
            }
        });
        btnSave.setOnClickListener(v -> {
            String giaNiemYet = etPrice.getText().toString();
            // Kiểm tra nếu có bất kỳ seekbar nào chưa được kéo
            if (tiLeGiaPin == null || tiLeGiaVo == null || tiLeGiaHoatDong == null || tiLeGiaManHinh == null) {
                Toast.makeText(getContext(), "Bạn chưa đánh giá đủ các tiêu chí", Toast.LENGTH_SHORT).show();
            } else if (giaNiemYet.isEmpty()) {
                Toast.makeText(getContext(), "Bạn chưa nhập giá", Toast.LENGTH_SHORT).show();
            } else {
                //Tính ra giá cuối cùng thông qua hàm
                double finalPrice = costingPrice(Double.parseDouble(giaNiemYet), 0.15 /* Gia chi tra co ban */, tiLeGiaPin, tiLeGiaVo, tiLeGiaManHinh, tiLeGiaHoatDong);

                // In ra giá cuối cùng để kiểm tra
                Log.d(TAG, "Final Price: " + finalPrice);
                tvFinalPrice.setText(String.valueOf(finalPrice));
            }
        });
        btnBack.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }

}

