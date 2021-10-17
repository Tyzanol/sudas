package com.example.sudas.positions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sudas.R;

import java.util.List;

public class positionCardAdapter extends ArrayAdapter<Position> {

    Context context;

    public positionCardAdapter(Context context, int resourceId, List<Position> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Position position_card_item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_position_card, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView location = (TextView) convertView.findViewById(R.id.location);

        title.setText(position_card_item.getTitle());
        location.setText(position_card_item.getLocation());
        return convertView;
    }
}
