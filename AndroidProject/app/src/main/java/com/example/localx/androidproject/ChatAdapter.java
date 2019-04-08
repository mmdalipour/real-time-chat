package com.example.localx.androidproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.localx.androidproject.DataModel.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;

    public ChatAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = messages.get(position);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nicknameText, messageText;

        ViewHolder(View itemView) {
            super(itemView);

            nicknameText = itemView.findViewById(R.id.nickname_txt);
            messageText = itemView.findViewById(R.id.message_txt);
        }

        void bind(Message message) {
            nicknameText.setText(String.format("%s => ", message.getUser()));
            messageText.setText(message.getText());
            if(message.getMessageColor() == 1) {
                nicknameText.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorDesMessage, null));
            }else {
                nicknameText.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorOwnMessage, null));
            }
        }
    }
}
