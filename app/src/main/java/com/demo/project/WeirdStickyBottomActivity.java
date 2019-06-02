package com.demo.project;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WeirdStickyBottomActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mStickyView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weird_sticky_bottom);

        mRecyclerView = findViewById(R.id.recyclerview);
        mStickyView = findViewById(R.id.sticky);
        Button mAddButton = findViewById(R.id.button_add);
        Button mDeleteButton = findViewById(R.id.button_delete);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        hideStickyLayoutIfNeed();

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addItems();
                hideStickyLayoutIfNeed();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.deleteItems();
                hideStickyLayoutIfNeed();
            }
        });

        mStickyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(WeirdStickyBottomActivity.this, "Sticky Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideStickyLayoutIfNeed() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int firstVisible = mLayoutManager.findFirstVisibleItemPosition();
                int lastCompletelyVisible = mLayoutManager.findLastCompletelyVisibleItemPosition();

                //adapter list size display beyond the screen
                if (firstVisible > 0 || lastCompletelyVisible < mAdapter.getItemCount() - 1) {
                    mAdapter.addStickyItem(true);
                    if (mStickyView.getVisibility() == View.VISIBLE) {
                        mStickyView.setVisibility(View.GONE);
                    }
                } else if (firstVisible == 0 && lastCompletelyVisible == mAdapter.getItemCount() - 1) {
                    if (mStickyView.getVisibility() != View.VISIBLE) {
                        mStickyView.setVisibility(View.VISIBLE);
                    }
                }
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                return true;
            }
        });
    }

    private static class MyAdapter extends RecyclerView.Adapter<ItemHolder> {

        private int mStickyIndex = -1;

        static final int HOLDER_TYPE_NORMAL = 1;
        static final int HOLDER_TYPE_STICKY = 2;

        private List<String> dataList = new ArrayList<>();
        private Context mContext;

        MyAdapter(Context context) {
            this.mContext = context;
            addItems();
        }

        public void addItems() {
            boolean hasStickyItem = mStickyIndex > 0;
            if (hasStickyItem) {
                dataList.remove(mStickyIndex);
                mStickyIndex = -1;
            }
            int size = dataList.size();
            for (int i = size; i < size + 5; i++) {
                dataList.add("This is a item");
            }
            notifyDataSetChanged();
        }

        public void deleteItems() {
            int size = dataList.size();
            if (size < 6) {
                return;
            }
            boolean hasStickyItem = mStickyIndex > 0;
            if (hasStickyItem) {
                dataList.remove(mStickyIndex);
                mStickyIndex = -1;
            }
            if (size > size - 5) {
                dataList.subList(size - 5, size - 1).clear();
            }
            notifyDataSetChanged();
        }

        public void addStickyItem(boolean notify) {
            //did not add sticky item before
            if (mStickyIndex <= 0) {
                mStickyIndex = dataList.size();
                dataList.add(mContext.getResources().getString(R.string.sticky_layout));
                if (notify) {
                    notifyDataSetChanged();
                }
            }
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ItemHolder.create(parent, viewType == HOLDER_TYPE_STICKY);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.bind(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {

            if (position == mStickyIndex) {
                return HOLDER_TYPE_STICKY;
            }

            return HOLDER_TYPE_NORMAL;
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private boolean isSticky;

        private ItemHolder(@NonNull View itemView, boolean isSticky) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            this.isSticky = isSticky;
        }

        void bind(String text) {
           textView.setText(text);
            if (isSticky) {
                textView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Sticky Click", Toast.LENGTH_SHORT).show();
                    }
                });
                textView.setBackgroundColor(textView.getResources().getColor(R.color.colorPrimary));
            } else {
                textView.setOnClickListener(null);
                textView.setBackgroundColor(Color.WHITE);
            }
        }

        static ItemHolder create(ViewGroup parent, boolean isSticky) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false), isSticky);
        }
    }
}
