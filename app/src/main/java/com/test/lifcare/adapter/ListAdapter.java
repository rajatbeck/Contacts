package com.test.lifcare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.test.lifcare.R;
import com.test.lifcare.model.Data;

import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rajatbeck on 3/25/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private final SortedList<Data> mSortedList = new SortedList<>(Data.class, new SortedList.Callback<Data>() {
        @Override
        public int compare(Data a, Data b) {
            return mComparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Data oldItem, Data newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Data item1, Data item2) {
            return item1.getPhoneId() == item2.getPhoneId();
        }
    });

    private final Comparator<Data> mComparator;
    private Context mContext;

    public ListAdapter(Context mContext, Comparator<Data> mComparator) {
        this.mComparator = mComparator;
        this.mContext = mContext;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_phone_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

        Data data = mSortedList.get(position);

        if (data.getmBitmap() != null) {
            holder.circleImageView.setImageBitmap(data.getmBitmap());
        } else {
            holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
        }
        holder.displayName.setText(data.getName());
        StringBuilder str=new StringBuilder();
        for (int i = 0; i < data.getmPhone().size(); i++){
            str.append(data.getmPhone().get(i).getPhone());
            str.append(",");
        }
        holder.number.setText(str);

    }

    public void removeAt(int position) {
        mSortedList.removeItemAt(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSortedList.size());
    }

    public String getItem(int position) {
        return mSortedList.get(position).getName();
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public void add(Data model) {
        mSortedList.add(model);
    }

    public void remove(Data model) {
        mSortedList.remove(model);
    }

    public void add(List<Data> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<Data> models) {
        mSortedList.beginBatchedUpdates();
        for (Data model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<Data> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Data model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }


   /* @Override
    public String getSectionTitle(int position) {
        return getItem(position).substring(0, 1);
    }*/

    protected class ListViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView displayName;
        private TextView number;

        public ListViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profile_pic);
            displayName = (TextView) itemView.findViewById(R.id.display_name);
            number = (TextView) itemView.findViewById(R.id.phone_number);
        }
    }
}
