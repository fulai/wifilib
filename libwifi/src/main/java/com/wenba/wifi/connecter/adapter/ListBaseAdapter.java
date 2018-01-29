package com.wenba.wifi.connecter.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenba.wifi.connecter.R;
import com.wenba.wifi.connecter.model.ScanResultInfo;

import java.util.List;

/**
 * Created by Dengmao on 18/1/18.
 */

public class ListBaseAdapter extends BaseAdapter {
    private List<ScanResultInfo> mScanResultInfos;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public ListBaseAdapter(List<ScanResultInfo> mScanResults, Context context) {
        this.mScanResultInfos = mScanResults;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setScanResults(List<ScanResultInfo> mScanResults) {
        this.mScanResultInfos = mScanResults;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.simple_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScanResult result = mScanResultInfos.get(position).getScanResult();
        viewHolder.textView1.setText(result.SSID);
        String status = mScanResultInfos.get(position).getStatus();
        if (TextUtils.isEmpty(status)) {
            viewHolder.textView2.setVisibility(View.GONE);
        } else {
            viewHolder.textView2.setVisibility(View.VISIBLE);
            viewHolder.textView2.setText(status);
        }
        return convertView;
    }

    public void updataView(int posi, ListView listView, String status) {
        int visibleFirstPosi = listView.getFirstVisiblePosition();
        int visibleLastPosi = listView.getLastVisiblePosition();
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
            View view = listView.getChildAt(posi - visibleFirstPosi);
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.textView2.setVisibility(View.VISIBLE);
            holder.textView2.setText(status);
            ScanResultInfo scanResultInfo = mScanResultInfos.get(posi);
            scanResultInfo.setStatus(status);
        } else {
            ScanResultInfo scanResultInfo = mScanResultInfos.get(posi);
            scanResultInfo.setStatus(status);
        }
    }

    @Override
    public int getCount() {
        return mScanResultInfos != null ? mScanResultInfos.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mScanResultInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        private TextView textView1;
        private TextView textView2;

        public ViewHolder(View view) {
            textView1 = view.findViewById(R.id.txt1);
            textView2 = view.findViewById(R.id.txt2);
        }

    }


}
