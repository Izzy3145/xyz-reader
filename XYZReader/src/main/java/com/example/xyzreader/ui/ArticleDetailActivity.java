package com.example.xyzreader.ui;


import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private int mSelectedItemId;

    private FloatingActionButton mFab;
    private Toolbar mDetailToolbar;
    private ImageView mPhotoView;
    private TextView mArticleTitle;
    private TextView mArticleByline;
    private TextView mArticleBody;

    private final String INTENT_ADAPTER_POSITION = "adapter_position";
    private final String SAVED_POSITION = "adapter_position";

    private final String TAG = ArticleDetailActivity.class.getSimpleName();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        mPhotoView = (ImageView) findViewById(R.id.photo);
        mArticleTitle = (TextView) findViewById(R.id.article_title);
        mArticleByline = (TextView) findViewById(R.id.article_byline);
        mArticleBody = (TextView) findViewById(R.id.article_body);

        //set up toolbar with up action
        mDetailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mDetailToolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayShowTitleEnabled(false);
            ab.setDisplayHomeAsUpEnabled(true);}



        getLoaderManager().initLoader(0, null, this);

        mFab = (FloatingActionButton) findViewById(R.id.share_fab);
        setUpFabButton();

        if (savedInstanceState != null) {
            mSelectedItemId = savedInstanceState.getInt(SAVED_POSITION);
        } else {
            //get id of item passed from ArticleListActivity
            if (getIntent() != null && getIntent().getExtras() != null) {
                //mStartId = ItemsContract.Items.getItemId(getIntent().getData());
                mSelectedItemId = getIntent().getIntExtra(INTENT_ADAPTER_POSITION, 0);
            }
        }
    }

    public void setUpFabButton(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mCursor.moveToPosition(mSelectedItemId);

        String articleTitle = mCursor.getString(cursor.getColumnIndex(ItemsContract.ItemsColumns.TITLE));
        String articleBody = mCursor.getString(cursor.getColumnIndex(ItemsContract.ItemsColumns.BODY));
        mArticleTitle.setText(articleTitle);
        mArticleBody.setText(Html.fromHtml(articleBody.replaceAll("(\r\n\r\n)", "<br /><br />")));

        String articleAuthor = mCursor.getString(cursor.getColumnIndex(ItemsContract.ItemsColumns.AUTHOR));
        String articleDate = mCursor.getString(cursor.getColumnIndex(ItemsContract.ItemsColumns.PUBLISHED_DATE));

        Date publishedDate = parsePublishedDate(articleDate);
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            mArticleByline.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by "
                            + articleAuthor));


        } else {
            // If date is before 1902, just show the string
            mArticleByline.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + " by "
                            + articleAuthor));

        }

        String imageUrl = mCursor.getString(cursor.getColumnIndex(ItemsContract.ItemsColumns.PHOTO_URL));
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(mPhotoView);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_POSITION, mSelectedItemId);
        super.onSaveInstanceState(outState);
    }
}
