package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class TabFragment4 extends Fragment {
    private Context context = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        final LinearLayout historyContainer = (LinearLayout) view.findViewById(R.id.historyContainer);



        for(int i= 0; i<11; i++) {

            LinearLayout imageContainer = new LinearLayout(context);
            imageContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams1.setMargins(0,15,0,0);

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics())));


            imageView.setBackgroundColor(Color.BLUE);
            imageContainer.addView(imageView);

            LinearLayout imageInfoContainer = new LinearLayout(context);
            imageInfoContainer.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(10, 0, 0, 0);

            TextView textView = new TextView(context);
            textView.setText("Department: Meeting");
            imageInfoContainer.addView(textView);

            TextView textView1 = new TextView(context);
            textView1.setText("Upload date: 1/23/17");
            imageInfoContainer.addView(textView1);

            TextView textView2 = new TextView(context);
            textView2.setText("Details: ");
            imageInfoContainer.addView(textView2);

            imageContainer.addView(imageInfoContainer, layoutParams);

            historyContainer.addView(imageContainer, layoutParams1);

        }




        return view;
    }


}