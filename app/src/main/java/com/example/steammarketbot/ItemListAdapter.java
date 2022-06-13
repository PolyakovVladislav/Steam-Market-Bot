package com.example.steammarketbot;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.steammarketbot.core.items.Item;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Item> items;

    ItemListAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inflater.inflate(R.layout.item_list_main, viewGroup, false);
        }

        Item item = items.get(i);

        ((ImageView) view.findViewById(R.id.ivItemImage)).setImageBitmap(BitmapFactory.decodeFile(item.getImage().getAbsolutePath()));
        ((TextView) view.findViewById(R.id.tvItemName)).setText(item.getItemName());
        ((EditText) view.findViewById(R.id.etCount)).setText(String.valueOf(item.getCount()));
        ((EditText) view.findViewById(R.id.etBudget)).setText(String.valueOf(item.getBudget()));
        SwitchCompat switchBuyOn = view.findViewById(R.id.switchBuyOn);
        switchBuyOn.setChecked(item.isBuyOn());
        switchBuyOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        ((EditText) view.findViewById(R.id.etYouRecieve)).setText(String.valueOf(item.getYouReceive()));
        ((EditText) view.findViewById(R.id.etBuyersPay)).setText(String.valueOf(item.getBuyerPay()));
        SwitchCompat switchSellOn = view.findViewById(R.id.switchSellOn);
        switchSellOn.setChecked(item.isSellOn());
        switchSellOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        return view;
    }
}
