package com.example.android.newsfeed;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks <List <NewsFeed>> {


    /**
     * URL for news data from the USGS dataset
     */
    private static final String GUARDIAN_URL =
            "https://content.guardianapis.com/search";


    /**
     * add log tag
     */

    private static final String LOG_TAG = NewsFeedLoader.class.getName();

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWSFEED_LOADER_ID = 1;


    /**
     * Adapter for the list of news
     */
    private NewsFeedAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );


        // Find a reference to the {@link ListView} in the layout
        ListView newsfeedListView = findViewById( R.id.list );

        mEmptyStateTextView = findViewById( R.id.empty_view );
        newsfeedListView.setEmptyView( mEmptyStateTextView );


        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsFeedAdapter( this, new ArrayList <NewsFeed>() );

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsfeedListView.setAdapter( mAdapter );

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsfeedListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                NewsFeed currentNewsFeed = mAdapter.getItem( position );

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsfeedUri = Uri.parse( currentNewsFeed.getUrl() );

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent( Intent.ACTION_VIEW, newsfeedUri );
                startActivity( websiteIntent );
            }
        } );

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService( Context.CONNECTIVITY_SERVICE );

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // this is to fetch data - if there is network
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter
            loaderManager.initLoader( NEWSFEED_LOADER_ID, null, this );
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById( R.id.loading_indicator );
            loadingIndicator.setVisibility( View.GONE );

            // Update empty state with no connection error message
            mEmptyStateTextView.setText( R.string.no_internet_connection );
        }
    }


    @Override
    public Loader <List <NewsFeed>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( this );

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String term = sharedPrefs.getString(
                getString( R.string.settings_term_key ),
                getString( R.string.settings_term_default ) );

        String orderBy = sharedPrefs.getString(
                getString( R.string.settings_order_by_key ),
                getString( R.string.settings_order_by_default )
        );

        Uri baseUri = Uri.parse( GUARDIAN_URL );

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter( "api-key", getString( R.string.api_key_news ));
        uriBuilder.appendQueryParameter( "q", term );
        uriBuilder.appendQueryParameter( "page-size", getString( R.string.settings_min_size_key ) );
        uriBuilder.appendQueryParameter( "show-tags", "contributor" );
        uriBuilder.appendQueryParameter( "order-by", orderBy );


        Log.i( LOG_TAG, uriBuilder.toString() );
        // Return the completed uri:
        //https://content.guardianapis.com/search?&show-tags=contributor&show-fields=thumbnail&api-key=03801489-c242-476c-ab0c-d841def5d3e2&show-tags=contributor
        return new NewsFeedLoader( this, uriBuilder.toString() );
    }


    @Override
    public void onLoadFinished(Loader <List <NewsFeed>> loader, List <NewsFeed> data) {
        mAdapter.setNewsFeeds( data );
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById( R.id.loading_indicator );
        loadingIndicator.setVisibility( View.GONE );

        // Set empty state text to display "No news available."
        mEmptyStateTextView.setText( R.string.no_newsfeeds );


        // Clear the adapter of previous news feed data
        mAdapter.clear();

        // If there is a valid list of {@link newsfeed}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll( data );
        }
    }

    @Override
    public void onLoaderReset(Loader <List <NewsFeed>> loader) {
        mAdapter.setNewsFeeds( new ArrayList <NewsFeed>() );
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }

    @Override
    //This method passes the MenuItem that is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent( this, SettingsActivity.class );
            startActivity( settingsIntent );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }
}


