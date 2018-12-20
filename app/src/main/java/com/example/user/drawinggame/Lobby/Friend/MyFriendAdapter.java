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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.drawinggame.R;
import com.example.user.drawinggame.connections.php.SendMsgThread;
import com.example.user.drawinggame.database_classes.Friend;
import com.example.user.drawinggame.database_classes.Player;
import com.example.user.drawinggame.utils.UI;

import java.util.List;

public class MyFriendAdapter extends BaseAdapter {

    private Context context;

    private Player player;

    private List<Friend[]> myFriendList;

    private final String sDefaultPath = "data/user/0/com.example.user.drawinggame/app_";
    private final String sFriendPhotoPath = "friends_photo";

    public MyFriendAdapter(Context context, Player player, List<Friend[]> myFriendList) {
        this.context = context;
        this.player = player;
        this.myFriendList = myFriendList;
    }


    private class ViewHolder {
        ImageView imageViewFriend1;
        TextView textViewName1;
        ImageView imageViewFriend2;
        TextView textViewName2;
        ImageView imageViewFriend3;
        TextView textViewName3;
    }

    @Override
    public int getCount() {
        return myFriendList.size();
    }

    @Override
    public Object getItem(int position) {
        return myFriendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myFriendList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_friend_list_item, null);
            holder = new MyFriendAdapter.ViewHolder();
            holder.imageViewFriend1 = (ImageView) convertView.findViewById(R.id.imageViewFriend1);
            holder.textViewName1 = (TextView) convertView.findViewById(R.id.textViewName1);
            holder.imageViewFriend2 = (ImageView) convertView.findViewById(R.id.imageViewFriend2);
            holder.textViewName2 = (TextView) convertView.findViewById(R.id.textViewName2);
            holder.imageViewFriend3 = (ImageView) convertView.findViewById(R.id.imageViewFriend3);
            holder.textViewName3 = (TextView) convertView.findViewById(R.id.textViewName3);
            convertView.setTag(holder);
        } else {
            holder = (MyFriendAdapter.ViewHolder) convertView.getTag();
        }

        Friend[] friends = (Friend[]) getItem(position);

        try {
            holder.textViewName1.setText(friends[0].getUserName());
            holder.textViewName1.setVisibility(View.VISIBLE);
            holder.imageViewFriend1.setImageBitmap(UI.getBitmapFromStorage(sDefaultPath + sFriendPhotoPath, String.valueOf(friends[0].getUserID())));
            holder.imageViewFriend1.setVisibility(View.VISIBLE);

            holder.textViewName2.setText(friends[1].getUserName());
            holder.textViewName2.setVisibility(View.VISIBLE);
            holder.imageViewFriend2.setImageBitmap(UI.getBitmapFromStorage(sDefaultPath + sFriendPhotoPath, String.valueOf(friends[1].getUserID())));
            holder.imageViewFriend2.setVisibility(View.VISIBLE);


            holder.textViewName3.setText(friends[2].getUserName());
            holder.textViewName3.setVisibility(View.VISIBLE);
            holder.imageViewFriend3.setImageBitmap(UI.getBitmapFromStorage(sDefaultPath + sFriendPhotoPath, String.valueOf(friends[2].getUserID())));
            holder.imageViewFriend3.setVisibility(View.VISIBLE);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        holder.imageViewFriend1.setOnClickListener(new FriendPhotoClickListener(position));
        holder.imageViewFriend2.setOnClickListener(new FriendPhotoClickListener(position));
        holder.imageViewFriend3.setOnClickListener(new FriendPhotoClickListener(position));


        return convertView;
    }

    private class FriendPhotoClickListener implements View.OnClickListener {
        private int position;
        private Friend[] friends;

        public FriendPhotoClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            friends = (Friend[]) getItem(position);

            switch (view.getId()) {
                case R.id.imageViewFriend1:
                    Log.i("friend name", friends[0].getUserName());
                    sendFriendMessage(friends[0]);
                    break;

                case R.id.imageViewFriend2:
                    Log.i("friend name", friends[1].getUserName());
                    sendFriendMessage(friends[1]);
                    break;

                case R.id.imageViewFriend3:
                    Log.i("friend name", friends[2].getUserName());
                    sendFriendMessage(friends[2]);
                    break;

            }
        }
    }

    private void sendFriendMessage(final Friend friend) {
        View viewSendFriendMsg = LayoutInflater.from(context).inflate(R.layout.send_friend_msg, null);

        TextView textViewTitle = viewSendFriendMsg.findViewById(R.id.textViewTitle);
        textViewTitle.setText("傳給" + friend.getUserName() + ":");

        final EditText editTextMsg = viewSendFriendMsg.findViewById(R.id.editTextMsg);

        Button buttonSend = viewSendFriendMsg.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String say = editTextMsg.getText().toString();
                SendMsgThread smt = new SendMsgThread(player, say, friend.getUserID(), 0);
                smt.start();

                editTextMsg.setText("");

                int count = 0;

                while (!smt.isDone()) {
                    if (count > 100) {
                        Log.e("send msg", "timeout");
                        break;
                    }
                    count++;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(smt.isSuccess()) {
                    Toast.makeText(context,"已傳送",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"傳送失敗",Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(viewSendFriendMsg)
                .create();

        UI.showImmersiveModeDialog(dialog, true);
        dialog.getWindow().setLayout(UI.width / 2, UI.height / 2);
    }

}
