package com.example.user.drawinggame.Lobby.Friend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.drawinggame.MainActivity;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.AddFriendThread;
import com.example.user.drawinggame.connections.php.SearchThread;
import com.example.user.drawinggame.database_classes.Message;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


// http://theopentutorials.com/tutorials/android/listview/android-custom-listview-with-image-and-text-using-baseadapter/
public class FriendAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messageList;
    private Player player;

    private ViewHolder holder;

    private class ViewHolder {
        ImageView imageViewPhoto;
        TextView textViewName;
        TextView textViewMessage;
        TextView textViewTime;

        ImageView imageViewAccept;
        ImageView imageViewDecline;
    }

    public FriendAdapter(Context context, List<Message> messageList, Player player) {
        this.context = context;
        this.messageList = messageList;
        this.player = player;
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

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item, null);
            holder = new ViewHolder();
            holder.imageViewPhoto = (ImageView) convertView.findViewById(R.id.imageViewPhoto);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewMessage = (TextView) convertView.findViewById(R.id.textViewMessage);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);

            holder.imageViewAccept = (ImageView) convertView.findViewById(R.id.imageViewAccept);
            holder.imageViewDecline = (ImageView) convertView.findViewById(R.id.imageViewDecline);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = (Message) getItem(position);

        Player player = new Player(message.getSenderID());
        SearchThread st = new SearchThread(player);
        st.start();


        holder.textViewName.setText(String.valueOf(message.getSenderName()));
        holder.textViewMessage.setText(message.getMsgContent());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dt = new Date(message.getMsgTime());
        String dts = sdf.format(dt);
        holder.textViewTime.setText(dts);

        holder.imageViewAccept.setOnClickListener(new ReplyListener(position));
        holder.imageViewDecline.setOnClickListener(new ReplyListener(position));

        while (!st.isDone) {
            Log.e("wait", "for searching");
        }
        new UI.DownloadImageTask(holder.imageViewPhoto).execute(player.getPicURL());

        return convertView;
    }

    public class ReplyListener implements View.OnClickListener {
        private int position;

        public ReplyListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageViewAccept:
                    Log.e(String.valueOf(position), "accept");
                    Log.e(String.valueOf(position), "decline");
                    AlertDialog acceptDialog = new AlertDialog.Builder(context)
                            .setTitle("確定要刪除''" + messageList.get(position).getSenderName() + "''的好友邀請嗎?")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AddFriendThread aft = new AddFriendThread(player.getUserID(), messageList.get(position).getSenderID());
                                    aft.start();

                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    if (aft.isSuccess()) {
                                        MainActivity.appDatabase.messageDao().deleteMessage(messageList.get(position));
                                        messageList.remove(position);
                                        notifyDataSetChanged();

                                        // ******************確認好友邀請
                                    }

                                }
                            })
                            .create();
                    UI.showImmersiveModeDialog(acceptDialog, true);

                    break;

                case R.id.imageViewDecline:
                    Log.e(String.valueOf(position), "decline");
                    AlertDialog declineDialog = new AlertDialog.Builder(context)
                            .setTitle("確定要刪除''" + messageList.get(position).getSenderName() + "''的好友邀請嗎?")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.appDatabase.messageDao().deleteMessage(messageList.get(position));
                                    messageList.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .create();
                    UI.showImmersiveModeDialog(declineDialog, true);

                    break;
            }
        }
    }


}
