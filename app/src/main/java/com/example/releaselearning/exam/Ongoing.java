package com.example.releaselearning.exam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.releaselearning.Constant;
import com.example.releaselearning.Entity.Class;
import com.example.releaselearning.Entity.Exam;
import com.example.releaselearning.Entity.Teacher;
import com.example.releaselearning.R;
import com.example.releaselearning.exam.adapter.ExamAdapter;
import com.example.releaselearning.homeWork.HomeWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Ongoing extends Fragment {
    private List<com.example.releaselearning.Entity.Exam> list;
    private String stuId;
    private Button button;
    private WebView webView;
    private TextView textView;
    private int state = Constant.BEGIN_EXAM;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //获取布局文件
        View view = inflater.inflate(R.layout.activity_exam_ongoing_detail, null);
        //获取布局文件中的控件对象
        ListView listView = view.findViewById(R.id.lv_exam);
        //给控件对象设置必要的属性(给listview设置item)
        stuId = getActivity().getIntent().getStringExtra("id");
        button = view.findViewById(R.id.exam_ongong_detail_button);
        webView = view.findViewById(R.id.exam_ongong_detail_wv);
        textView = view.findViewById(R.id.exam_ongong_detail_tv);
        textView.setText("准备好后点击开始考试");
        button.setOnClickListener(view1 -> {

            if(state == Constant.BEGIN_EXAM){
//                Constant.URLEXAM ;//监考网页
                textView.setText("作答完毕后点击结束考试");
                button.setText("结束考试");
                webView.loadUrl(Constant.URLEXAM );
                state = Constant.END_EXAM;

            }else{
                textView.setText("准备好后点击开始考试");
                button.setText("开始考试");
                webView.loadUrl("");
                state = Constant.BEGIN_EXAM;
            }

        });


//        //获取学生考试数据
//        list = getData(stuId);
//        ExamAdapter adapter = new ExamAdapter(getContext(),R.layout.fragment_exam_item,list);
//        listView.setAdapter(adapter);
//
//        // tvMsg.setText("设置页面");
//        //给某些控件对象添加事件监听器
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv = view.findViewById(R.id.tv_examId);
//                String examId = tv.getText().toString();
//                Intent intent = getActivity().getIntent();
//                intent.putExtra("examId",examId);
//
//                //跳转到考试详情页面
//                intent.setClass(getActivity(), HomeWork.class);
//                startActivity(intent);
//            }
//        });

        //返回布局文件对象
        return view;

    }



    private List<com.example.releaselearning.Entity.Exam> getData(String stuId) {
        List<com.example.releaselearning.Entity.Exam> works = new ArrayList<>();
        String URL = Constant.URLExam+"/getExamAllByStuId/"+stuId;
        OkHttpClient okHttpClient = new OkHttpClient();
        System.out.println(URL);

        Request request2 = new Request.Builder()
                .url(URL)
                .build();
        Call call = okHttpClient.newCall(request2);
        call.enqueue(new Callback() {
            // 请求失败
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("okhttp" ,"请求失败");
                e.printStackTrace();
            }
            //请求成功
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    //处理数据
                    String responseStr = response.body().string();
                    Log.e("responseStr",responseStr);

                    JSONArray jsonArray = JSON.parseArray(responseStr);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String status = jsonObject.getString("status");
                        if (status.equals("正在考试")) {
                            String ExamId = jsonObject.getString("ExamId");
                            String examContent = jsonObject.getString("examContent");
                            JSONObject clatemp = jsonObject.getJSONObject("classId");
                            String classId = clatemp.getString("classId");
                            JSONObject teacherIdtemp = clatemp.getJSONObject("teacherId");
                            String teacherId = teacherIdtemp.getString("teacherId");
                            String tname = teacherIdtemp.getString("name");
                            String tpassword = teacherIdtemp.getString("password");

                            Teacher teacher = new Teacher(teacherId,tname,tpassword);
                            Class cla = new Class(classId,teacher);
                            com.example.releaselearning.Entity.Exam exam = new Exam(ExamId,examContent,cla,status);
                            works.add(exam);
                        }


                    }
                }

            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return works;
    }
}
