package com.example.android.newsfeed;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.example.android.newsfeed.NewsFeed;

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * An {@link NewsFeedAdapter} knows how to create a list item layout for each news
 * in the data source (a list of {@link NewsFeed} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsFeedAdapter extends ArrayAdapter <NewsFeed> {


    private List <NewsFeed> newsfeeds = new ArrayList <NewsFeed>();

    public NewsFeedAdapter(Context context, List <NewsFeed> newsfeeds) {
        super( context, 0, newsfeeds );

    }


    /**
     * Returns a list item view that displays information about the news at the given position
     * in the list of news.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.newsfeed_list_item, parent, false );
        }

        // Find the news at the given position in the list of news
        NewsFeed currentNewsFeed = getItem( position );

        // Find the TextView with view called sectionId
        String sectionId = currentNewsFeed.getSectionId();
        TextView idView = listItemView.findViewById( R.id.sectionId );
        idView.setText( sectionId );

        // Find the TextView with view name
        TextView nameView = listItemView.findViewById( R.id.sectionName );
        String sectionName = currentNewsFeed.getSectionId();
        // Display the name of the current news in that TextView
        nameView.setText( sectionName );

        // Find the TextView with view title
        TextView titleView = listItemView.findViewById( R.id.webTitle );
        String webTitle = currentNewsFeed.getWebTitle();
        // Display the title of the current news in that TextView
        titleView.setText( webTitle );


        // Find the TextView with view ID date
        TextView dateView = listItemView.findViewById( R.id.webPublicationDate );
        // Format the date string (i.e. "July 3, 1991")
        String webPublicationDate = currentNewsFeed.getWebPublicationDate();
        // Display the date of the current news in that TextView
        dateView.setText( webPublicationDate );

        // Find the TextView with view called author name
        String authorName = currentNewsFeed.getAuthorName();
        TextView authorView = listItemView.findViewById( R.id.authorName );
        authorView.setText( authorName );

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }


    public void setNewsFeeds(List <NewsFeed> data) {
        newsfeeds.addAll( data );
        notifyDataSetChanged();
    }

}