package figaro.oklab.com.figaro.data;

/**
 * Created by olgakuklina on 8/4/17.
 */

public class UserData {

    private final String authorName;
    private final String authorAvatarURL;
    private final String login;

    public UserData(String authorName, String authorAvatarURL, String login) {
        this.authorName = authorName;
        this.authorAvatarURL = authorAvatarURL;
        this.login = login;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorAvatarURL() {
        return authorAvatarURL;
    }

    public String getLogin() {
        return login;
    }
}
