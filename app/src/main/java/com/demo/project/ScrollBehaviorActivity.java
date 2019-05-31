package com.demo.project;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScrollBehaviorActivity extends AppCompatActivity {

    private AppBarLayout mAppBarLayout;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout mToolbarLayout;
    private Button mButton;
    private int state = STATE_APPBAR;
    private static final int STATE_APPBAR = 1;
    private static final int STATE_CONTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_flag);

        mAppBarLayout = findViewById(R.id.appbar);
        mRecyclerView = findViewById(R.id.recyclerview);
        mButton = findViewById(R.id.button);
        mToolbarLayout = findViewById(R.id.toolbar_layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new MyAdapter());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        params.setBehavior(new AppBarLayout.Behavior());
        AppBarLayout.Behavior appBarBehavior = (AppBarLayout.Behavior) params.getBehavior();
        if (appBarBehavior != null) {
            appBarBehavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return state == STATE_APPBAR;
                }
            });
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_APPBAR) {
                    state = STATE_CONTENT;
//                    setAppbarLayoutScrollFlag(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
                    ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
                } else {
                    state = STATE_APPBAR;
//                    setAppbarLayoutScrollFlag(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
                    ViewCompat.setNestedScrollingEnabled(mRecyclerView, true);
                }
            }
        });
    }

    private void setAppbarLayoutScrollFlag(int flags) {
        AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) mToolbarLayout.getLayoutParams();
        lp.setScrollFlags(flags);
        mToolbarLayout.setLayoutParams(lp);
    }

    private static class MyAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<String> datas = new ArrayList<>();

        public MyAdapter() {
            for (int i = 0; i < 30; i++) {
                datas.add("This is No." + i + " item");
            }
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ItemHolder.create(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.bind(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        private ItemHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

        public void bind(String text) {
           textView.setText(text);
        }

        public static ItemHolder create(ViewGroup parent) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false));
        }
    }
}
