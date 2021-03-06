//package com.example.leeyh.abroadapp.background;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.support.v4.content.LocalBroadcastManager;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import static com.example.leeyh.abroadapp.constants.StaticString.BROADCAST;
//
//public class ServiceEventInterface implements SocketRequestListener {
//
//    private Context mContext;
//    private OnResponseReceivedListener2 mResponseListener;
//    private BroadcastReceiver mBroadcastReceiver;
//
//    public ServiceEventInterface(Context context) {
//        this.mContext = context;
//        onReceivedResponse();
////        onSocketRouted();
//    }
//
//    @Override
//    public void socketRouting(String nameSpace) {
////        Intent service = new Intent(mContext, BackgroundService.class);
////        service.putExtra(ROUTING, nameSpace);
////        mContext.startService(service);
//    }
//
//    @Override
//    public void socketEmitEvent(String event, JSONObject data) {
//
//    }
//
//    @Override
//    public void socketEmitEvent(String event, JSONArray data) {
//
//    }
//
////    @Override
//    public void socketEmitEvent(String event, String data) {
////        Intent service = new Intent(mContext, BackgroundService.class);
////        service.putExtra(EMIT_EVENT, event);
////        service.putExtra(JSON_DATA, data);
////        mContext.startService(service);
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//    }
//
//    private void onReceivedResponse() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(BROADCAST);
//        mBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                mResponseListener.onResponseReceived(intent);
//            }
//        };
//        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, intentFilter);
//    }
//
//    public void unRegisterReceivedResponse() {
//        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
//    }
//
//
//
//    public void setResponseListener(OnResponseReceivedListener2 listener) {
//        this.mResponseListener = listener;
//    }
//
//    @Override
//    public void socketOnEvent(String onEvent) {
////        Intent service = new Intent(mContext, BackgroundService.class);
////        service.putExtra(ON_EVENT, onEvent);
////        mContext.startService(service);
//    }
//
//    @Override
//    public void closeSocket() {
////        Intent service = new Intent(mContext, BackgroundService.class);
////        service.putExtra(DISCONNECT, DISCONNECT);
////        mContext.startService(service);
//    }
//
//
//}
