package com.example.android.miwok;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rodrigo.marques on 23/03/2017.
 */

public class WordAdapter extends ArrayAdapter<Word> {

    private int mColorResourceId;

    public WordAdapter(@NonNull Context context, ArrayList<Word> words, int colorResourceId ) {
        super(context, 0,words);
        mColorResourceId = colorResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View listView, @NonNull ViewGroup parent) {

        if(listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Word word = getItem(position);

        TextView defaultTranslate = (TextView) listView.findViewById(R.id.default_text_view);
        TextView miwokTranslate = (TextView) listView.findViewById(R.id.miwok_text_view);
        ImageView imageView = (ImageView) listView.findViewById(R.id.image);
        LinearLayout linearLayout = (LinearLayout) listView.findViewById(R.id.text_container);

        int color = ContextCompat.getColor(getContext(), mColorResourceId);

        linearLayout.setBackgroundColor(color);

        miwokTranslate.setText(word.getmMiwokTranslation());
        defaultTranslate.setText(word.getmDefaultTranslation());

        if(word.hasImage()){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(word.getmImageResourceId());
        }else{
            imageView.setVisibility(View.GONE);
        }

        return listView;
    }
}
