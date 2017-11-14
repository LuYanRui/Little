package com.didi.little;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MySortAdapter extends BaseAdapter implements SectionIndexer,Filterable {
	private List<UserModel> list = null;
	private Context context = null;

	public MySortAdapter(Context context, List<UserModel> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_main, parent, false);
			ViewUtils.inject(mHolder, convertView);
			convertView.setTag(mHolder);
		}
		else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		UserModel userModel = list.get(position);
		mHolder.textView_item_username.setText(userModel.getUesrname());
        mHolder.textView_item_userhead.setText(userModel.getUesrname().substring(0,1));
		mHolder.textView_item_number.setText(userModel.getUsernumber());

		int section = getSectionForPosition(position);
		if (position == getPositionForSection(section)) {
			mHolder.textView_item_firstletter.setVisibility(View.VISIBLE);
			mHolder.textView_item_firstletter.setText(userModel
					.getFirstLetter());
		} else {
			mHolder.textView_item_firstletter.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		@ViewInject(R.id.textView_item_username)
		private TextView textView_item_username;
		@ViewInject(R.id.textView_item_firstletter)
		private TextView textView_item_firstletter;
		@ViewInject(R.id.text_item_userhead)
		private TextView textView_item_userhead;
		@ViewInject(R.id.textView_item_number)
		private TextView textView_item_number;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String firstLetter = list.get(i).getFirstLetter();
			char firstChar = firstLetter.charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getFirstLetter().charAt(0);
	}

	@Override
	public Filter getFilter() {
		return null;
	}
}
