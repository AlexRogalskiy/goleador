package ris58h.goleador.main;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadSnippet;

public class YoutubeCommenter {
    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    private final YouTube youTube;

    public YoutubeCommenter(String clientId, String clientSecret, String refreshToken) {
        Credential credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);
        this.youTube = new YouTube.Builder(TRANSPORT, JSON_FACTORY, credential).build();
    }

    public String comment(String videoId, String text) throws Exception {
        CommentThread commentThread = new CommentThread();
        commentThread.setSnippet(new CommentThreadSnippet()
                .setVideoId(videoId)
                .setTopLevelComment(new Comment()
                        .setSnippet(new CommentSnippet()
                                .setTextOriginal(text))));
        CommentThread videoCommentInsertResponse = this.youTube.commentThreads()
                .insert("snippet", commentThread).execute();
        return videoCommentInsertResponse.getSnippet().getTopLevelComment()
                .getId();
    }
}
