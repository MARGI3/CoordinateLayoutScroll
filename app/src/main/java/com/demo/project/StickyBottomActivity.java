package com.demo.project;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StickyBottomActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mStickyView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private FrameLayout mStickParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_bottom);

        mRecyclerView = findViewById(R.id.recyclerview);
        mStickParent = findViewById(R.id.sticky_parent);
        mStickyView = findViewById(R.id.sticky);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        setStickyViewToBehavior(mStickyView);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int index = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int type = mAdapter.getItemViewType(index);
                    if (type == MyAdapter.HOLDER_TYPE_STICKY) {
                        mStickyView.setVisibility(View.GONE);
                    }
                } else {
                    int index = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    int type = mAdapter.getItemViewType(index + 1);
                    if (type == MyAdapter.HOLDER_TYPE_STICKY) {
                        mStickyView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        mStickyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(StickyBottomActivity.this, "Sticky Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStickyViewToBehavior(View stickyView) {
        ViewGroup.LayoutParams layoutParams = mStickParent.getLayoutParams();
        if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
            if (behavior instanceof StickyBottomScrollingBehavior) {
                ((StickyBottomScrollingBehavior) behavior).addStickyView(stickyView);
            }
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<ItemHolder> {

        private static final int STICKY_INDEX = 15;

        static final int HOLDER_TYPE_NORMAL = 1;
        static final int HOLDER_TYPE_STICKY = 2;

        private List<String> dataList = new ArrayList<>();

        MyAdapter(Context context) {
            for (int i = 0; i < 30; i++) {
                if (i == STICKY_INDEX) {
                    dataList.add(context.getResources().getString(R.string.sticky_layout));
                } else {
                    dataList.add("This is No." + i + " item");
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

            if (position == STICKY_INDEX) {
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
