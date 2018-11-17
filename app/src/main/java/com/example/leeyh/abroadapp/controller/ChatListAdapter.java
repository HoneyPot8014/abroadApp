package com.example.leeyh.abroadapp.controller;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.leeyh.abroadapp.model.ChatListModel;
import com.example.leeyh.abroadapp.view.ChatListItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.leeyh.abroadapp.constants.StaticString.JOIN_MEMBERS;
import static com.example.leeyh.abroadapp.constants.StaticString.LAST_MESSAGE;
import static com.example.leeyh.abroadapp.constants.StaticString.ROOM_NAME;
import static com.example.leeyh.abroadapp.constants.StaticString.USER_ID;

public class ChatListAdapter extends BaseAdapter {

//    List<ChatListModel> items = new ArrayList<>();
    JSONArray items = new JSONArray();

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return items.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ChatListItemView itemView;

        if (view == null) {
            itemView = new ChatListItemView(viewGroup.getContext());
        } else {
            itemView = (ChatListItemView) view;
        }

//        ChatListModel item = items.get(i);
        try {
            JSONObject item = items.getJSONObject(i);
            itemView.setChatListLastMessage(item.getString(LAST_MESSAGE));
            JSONArray joinMembers = (JSONArray) item.get(JOIN_MEMBERS);
            itemView.setChatListProfileImage(joinMembers.getString(0), viewGroup.getContext());
            Log.d("채팅2", "getView: " + joinMembers);
            itemView.setChatListRoomNickName(joinMembers.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemView;
    }

    public void addItem(JSONObject jsonObject) {
//        try {
//            JSONArray joinMembers = new JSONArray(jsonObject.optString(JOIN_MEMBERS));
//            String roomName = jsonObject.optString(ROOM_NAME);
//            String joinMembersToString = joinMembers.toString();
//            String message = jsonObject.optString(LAST_MESSAGE);
//            items.add(new ChatListModel(roomName, joinMembers.getString(0), joinMembersToString, message));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public void addList(JSONArray chattingList) {
        items = chattingList;
    }
}
