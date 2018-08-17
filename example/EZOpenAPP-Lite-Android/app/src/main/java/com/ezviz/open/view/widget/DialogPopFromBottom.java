package com.ezviz.open.view.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;
import com.ezviz.open.R;
public class DialogPopFromBottom extends Dialog {

	private RecyclerView mRecyclerView;
	private Context mContext;
	public List<Integer> mList = new ArrayList<Integer>();
	/**
	 * 是否包含取消item
	 */
	public boolean isHasCancel = true;

	public ListAdapter mAdapter;

	public OnBottomItemClickListener mOnBottomItemClickListener;

	public void setOnBottomItemClickListener(OnBottomItemClickListener onBottomItemClickListener) {
		mOnBottomItemClickListener = onBottomItemClickListener;
	}

	public interface OnBottomItemClickListener{
		public void onDialogItemClick(int resId);
	}


	public DialogPopFromBottom(Context context) {
		super(context, R.style.BottomDialog);
		mContext = context;
	}

	/**
	 * @param list
	 * @param hasCancel	是否底部最后一个item为取消
	 */
	public void setList(List<Integer> list,boolean hasCancel) {
		mList = list;
		this.isHasCancel = hasCancel;
		if (isHasCancel){
			mList.add(R.string.cancel);
		}
		if (mAdapter != null){
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * @param context
	 * @param onBottomItemClickListener
	 */
	public DialogPopFromBottom(Context context,OnBottomItemClickListener onBottomItemClickListener) {
		super(context, R.style.BottomDialog);
		this.mOnBottomItemClickListener = onBottomItemClickListener;
		mContext = context;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
		setContentView(R.layout.bottom_list_dialog);
		mRecyclerView = (RecyclerView) findViewById(R.id.dialog_recyclerview);
		LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
		mRecyclerView.setLayoutManager(mLinearLayoutManager);
		mRecyclerView.addItemDecoration(new RecycleViewItemDivider(mContext,R.drawable.recycle_item_divider));
		mRecyclerView.setHasFixedSize(true);
		mAdapter = new ListAdapter();
		mRecyclerView.setAdapter(mAdapter);
	}

	class ViewHold extends RecyclerView.ViewHolder{
		TextView mTextView;
		public ViewHold(View itemView) {
			super(itemView);
			mTextView = (TextView) itemView.findViewById(R.id.item_text);
			mTextView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mList.get(getAdapterPosition()) != R.string.cancel){
						if (mOnBottomItemClickListener != null){
							mOnBottomItemClickListener.onDialogItemClick(mList.get(getAdapterPosition()));
						}
					}
					dismiss();
				}
			});
		}
	}
	class ListAdapter extends RecyclerView.Adapter<ViewHold>{
		@Override
		public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.bottom_list_dialog_item,null);
			return new ViewHold(view);
		}

		@Override
		public void onBindViewHolder(ViewHold holder, int position) {
			holder.mTextView.setText(mList.get(position));
			if (isHasCancel && position == mList.size() - 1){
				holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.device_top_item_text_normal));
				holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.main_back));
			}else{
				holder.mTextView.setTextColor(mContext.getResources().getColor( R.color.topbar_title_color));
				holder.mTextView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
			}
		}
		@Override
		public int getItemCount() {
			return mList.size();
		}
	}
}
