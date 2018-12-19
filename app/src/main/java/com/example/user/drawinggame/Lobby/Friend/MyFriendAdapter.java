package com.example.user.drawinggame.Lobby.Friend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.drawinggame.Lobby.Message.MessageAdapter;
import com.example.user.drawinggame.R;
import com.example.user.drawinggame.database_classes.Friend;

import java.util.List;

public class MyFriendAdapter extends BaseAdapter {

    private Context context;

    private List<Friend[]> myFriendList;

    public MyFriendAdapter(Context context, List<Friend[]> myFriendList) {
        this.context = context;
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
            holder.textViewName2.setText(friends[1].getUserName());
            holder.textViewName2.setVisibility(View.VISIBLE);
            holder.textViewName3.setText(friends[2].getUserName());
            holder.textViewName3.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
