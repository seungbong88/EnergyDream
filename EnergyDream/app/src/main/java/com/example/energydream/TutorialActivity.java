package com.example.energydream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class TutorialActivity  extends AppCompatActivity {
    private ViewPager m_pager;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        m_pager = findViewById(R.id.pager);
        m_pager.setAdapter(new PagerAdapterClass(getApplicationContext()));

    }

    private View.OnClickListener clickButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int infoFirst = 1;
            SharedPreferences a = getSharedPreferences("a", MODE_PRIVATE);
            SharedPreferences.Editor edit = a.edit();
            edit.putInt("First", infoFirst);
            edit.commit();
            finish();
        }
    };

    private class PagerAdapterClass extends PagerAdapter{
        private LayoutInflater inflater;
        public PagerAdapterClass(Context c){
            super();
            inflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return 5;
        }
        public Object instantiateItem(View pager, int position){
            View view = null;
            if(position==0){
                view = inflater.inflate(R.layout.fsv_one, null);
                ImageView image = view.findViewById(R.id.image1);
                Picasso.with(view.getContext()).load(R.drawable.tutorial1).fit().into(image);
            }
            else if(position==1){
                view = inflater.inflate(R.layout.fsv_two, null);
                ImageView image = view.findViewById(R.id.image2);
                Picasso.with(view.getContext()).load(R.drawable.tutorial2).fit().into(image);
            }
            else if(position==2){
                view = inflater.inflate(R.layout.fsv_three, null);
                ImageView image = view.findViewById(R.id.image3);
                Picasso.with(view.getContext()).load(R.drawable.tutorial3).fit().into(image);
            }else if(position==3){
                view = inflater.inflate(R.layout.fsv_four, null);
                ImageView image = view.findViewById(R.id.image4);
                Picasso.with(view.getContext()).load(R.drawable.tutorial4).fit().into(image);
            }else{
                view = inflater.inflate(R.layout.fsv_five, null);
                ImageView image = view.findViewById(R.id.image5);
                Picasso.with(view.getContext()).load(R.drawable.tutorial5).fit().into(image);
                view.findViewById(R.id.tutorial_btn).setOnClickListener(clickButton);

            }
            ((ViewPager)pager).addView(view,0);
            return view;

        }
        public void destroyItem(View pager, int position, Object view){
            ((ViewPager)pager).removeView((View)view);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
    }
}
