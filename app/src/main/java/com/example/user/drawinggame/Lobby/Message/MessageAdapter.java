package com.example.user.drawinggame.Lobby.Message;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.GetPictureThread;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.database_classes.Friend;
import com.example.user.drawinggame.database_classes.Message;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.util.Date;
import java.util.List;


// http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messageList;

    private final String sDefaultPath = "data/user/0/com.example.user.drawinggame/app_";
    private final String sFriendPhotoPath = "friends_photo";

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageViewPhoto;
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTime;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messageList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item, null);

            convertView.findViewById(R.id.imageViewAccept).setVisibility(View.INVISIBLE);
            convertView.findViewById(R.id.imageViewDecline).setVisibility(View.INVISIBLE);


            holder = new ViewHolder();
            holder.imageViewPhoto = (ImageView) convertView.findViewById(R.id.imageViewPhoto);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = (Message) getItem(position);

        holder.textViewName.setText(String.valueOf(message.getSenderName()));
        holder.textViewMessage.setText(message.getMsgContent());
        holder.textViewTime.setText(message.getMsgTime());
        holder.imageViewPhoto.setImageBitmap(UI.getBitmapFromStorage(sDefaultPath + sFriendPhotoPath, String.valueOf(message.getSenderID())));

        return convertView;
    }
}
