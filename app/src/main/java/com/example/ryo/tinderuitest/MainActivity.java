package com.example.ryo.tinderuitest;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.RelativeLayout;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;


import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainActivity extends AppCompatActivity implements CardModel.OnCardDimissedListener, CardModel.OnClickListener{

    private int likeCount;
    private int disLikeCount;

    public static final String TITLE        = "SampleCard1";
    public static final String DESCRIPTION  = "This is Sample card";
    public static final String TAG          = MainActivity.class.getSimpleName();

    private SimpleCardStackAdapter adapter;
    private CardModel cardModel;

    @InjectView(R.id.layoutview)
    CardContainer mLayoutview;
    @InjectView(R.id.rootLayout)
    RelativeLayout mRootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Resources resources = getResources();
        mLayoutview.setOrientation(Orientations.Orientation.Ordered);
        cardModel = new CardModel(TITLE, DESCRIPTION, resources.getDrawable(R.drawable.picture1, this.getTheme()));
        cardModel.setOnCardDimissedListener(this);
        cardModel.setOnClickListener(this);
        adapter = new SimpleCardStackAdapter(this);
        AddCardModel();
        mLayoutview.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void AddCardModel() {
        for (int i = 0; i < 10; i++) {
            adapter.add(cardModel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLike() {
        likeCount++;
        Snackbar.make(mRootLayout, "You like this picture." + likeCount, Snackbar.LENGTH_SHORT).show();
        ShowEmptyMsg(adapter.getCount());
    }

    @Override
    public void onDislike() {
        disLikeCount++;
        Snackbar.make(mRootLayout, "You dislike this picture." + disLikeCount, Snackbar.LENGTH_SHORT).show();
        ShowEmptyMsg(adapter.getCount());
    }

    @Override
    public void OnClickListener() {
        Snackbar.make(mRootLayout, "You click this picture.", Snackbar.LENGTH_SHORT).show();
    }

    public void ShowEmptyMsg(final int cardCount) {
        Log.i(TAG, "Like count is " + likeCount + " DisLike count is " + disLikeCount);
        if (cardCount == likeCount + disLikeCount) {
            Snackbar.make(mRootLayout, "Do you want more ?" , Snackbar.LENGTH_LONG).setAction("Yes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ViewBus(true));
                }
            }).show();
        }
    }

    @Subscribe
    public void onEvent(ViewBus viewBus) {
        if (viewBus.isFinish()) {
            Resources resources = getResources();
            mLayoutview.setOrientation(Orientations.Orientation.Ordered);
            cardModel = new CardModel(TITLE, DESCRIPTION, resources.getDrawable(R.drawable.picture1, this.getTheme()));
            cardModel.setOnCardDimissedListener(this);
            cardModel.setOnClickListener(this);
            adapter = new SimpleCardStackAdapter(this);
            AddCardModel();
            mLayoutview.setAdapter(adapter);

            likeCount       = 0;
            disLikeCount    = 0;

            Log.i(TAG, "Card count is " + adapter.getCount());
        }
    }

}
