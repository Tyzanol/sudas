package com.example.sudas.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sudas.R;

public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessage;
    public LinearLayout mContainer;
//    public TextView mMatchName;
//    public ImageView mMatchImage;

    public ChatsViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mMessage = (TextView) itemView.findViewById(R.id.message);
        mContainer = (LinearLayout) itemView.findViewById(R.id.container);
        }
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
//        b.putString("matchId", mMatchId.getText().toString());
//        intent.putExtras(b);
//        view.getContext().startActivity(intent);
    }
}
