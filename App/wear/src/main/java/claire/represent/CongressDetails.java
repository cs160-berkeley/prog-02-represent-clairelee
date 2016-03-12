package claire.represent;

/**
 * Created by clairelee on 3/11/16.
 */
public class CongressDetails {
    private  String mbioguide_id;
    private String mTitle;
    private  String mLastName;
    private  String mFirstName;
    private  String mParty;
    private String mEmail;
    private String mWebsite;
    private String mTermEnd;
    private String mTwitter;

    public CongressDetails(String bioguide_id, String title, String firstName, String lastName, String party, String email, String website, String termEnd, String twitter)
    {
        mbioguide_id = bioguide_id;
        mTitle = title;
        mLastName = lastName;
        mFirstName = firstName;
        mParty = party;
        mEmail = email;
        mWebsite = website;
        mTermEnd = termEnd;
        mTwitter = twitter;
    }

    public void setMbioguide_id(String bioguide_id) {
        mbioguide_id=bioguide_id;
    }

    public String getMbioguide_id() {
        return mbioguide_id;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getLastName() {
        return mLastName;
    }


    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }


    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getFirstName() {
        return mFirstName;
    }


    public void setParty(String party) {
        mParty = party;
    }

    public String getParty() {
        return mParty;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setTwitter(String twitter) {
        mTwitter = twitter;
    }

    public String getTwitter() {

        return mTwitter;
    }

    public void setTermEnd(String termEnd) {
        mTermEnd = termEnd;
    }

    public String getTermEnd() {
        return mTermEnd;
    }

}
