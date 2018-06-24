package com.example.android.newsfeed;

/**
 * An {@link NewsFeed} object contains information related to a single news.
 */
public class NewsFeed {

    /**
     * Name of the newsfeed
     */
    private String mSectionId;


    /**
     * Name of the newsfeed
     */
    private String mSectionName;

    /**
     * Title of the newsfeed
     */
    private String mWebTitle;

    /**
     * Date of the newsfeed
     */
    private String mWebPublicationDate;

    /**
     * Name of the newsfeed
     */
    private String mAuthorName;

    /**
     * Website URL of the newsfeed
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsFeed } object.
     *
     * @param sectionId          is the type of the news
     * @param sectionName        is the Sectionname of the news
     * @param webTitle           is the Sectiontitle of the news
     * @param webPublicationDate is the time in milliseconds (from the Epoch) when
     *                           it was released.
     * @param authorName         is the author's name
     * @param url                is the website URL to find more details about the news
     */
    public NewsFeed(String sectionId, String sectionName, String webTitle, String webPublicationDate, String authorName, String url) {
        mSectionId = sectionId;
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebPublicationDate = webPublicationDate;
        mAuthorName = authorName;
        mUrl = url;
    }


    /**
     * Returns the Type of the NEWS.
     */
    public String getSectionId() {
        return mSectionId;
    }


    /**
     * Returns the NAME of the NEWS FEED.
     */
    public String getSectionName() {
        return mSectionName;
    }


    /**
     * Returns the TITLE of the NEWS.
     */
    public String getWebTitle() {
        return mWebTitle;
    }


    /**
     * Returns the time of the NEWS.
     */
    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }


    /**
     * Returns the name of the author.
     */
    public String getAuthorName() {
        return mAuthorName;
    }


    /**
     * Returns the website URL to find more information about the news.
     */
    public String getUrl() {
        return mUrl;
    }
}