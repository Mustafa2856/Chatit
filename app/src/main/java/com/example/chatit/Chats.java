package com.example.chatit;

import android.util.Pair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class to centrally store all chats of the user.
 */
public class Chats {
    Map<String, String> usernames = new HashMap<>();
    Map<String, Pair<Timestamp, String>> display_list = new HashMap<>();
    Map<String, ArrayList<Pair<Timestamp, String>>> messages = new HashMap<>();
    Map<String, ArrayList<Pair<Timestamp, String>>> sent = new HashMap<>();
    Timestamp tmp = new Timestamp(0);

    public void addMessage(String uname, String email, String message, Timestamp tmp) {
        if (usernames == null) usernames = new HashMap<>();
        if (messages == null) display_list = new HashMap<>();
        if (display_list == null) messages = new HashMap<>();
        if (sent == null) sent = new HashMap<>();
        if (usernames.get(email) == null) {
            usernames.put(email, uname);
            messages.put(email, new ArrayList<>());
            Objects.requireNonNull(messages.get(email)).add(new Pair<>(tmp, message));
            display_list.put(email, new Pair<>(tmp, message));
        } else {
            if (messages.get(email) == null) messages.put(email, new ArrayList<>());
            Objects.requireNonNull(messages.get(email)).add(new Pair<>(tmp, message));
            if (display_list.get(email) != null && Objects.requireNonNull(display_list.get(email)).first.compareTo(tmp) <= 0)
                display_list.put(email, new Pair<>(tmp, message));
        }
        if(tmp.after(this.tmp))this.tmp = tmp;
    }

    public void sendMessage(String uname, String remail, String message, Timestamp tmp) {
        if (!sent.containsKey(remail)) {
            sent.put(remail, new ArrayList<>());
        }
        Objects.requireNonNull(sent.get(remail)).add(new Pair<>(tmp, message));
        if (display_list.get(remail) != null && Objects.requireNonNull(display_list.get(remail)).first.compareTo(tmp) <= 0)
            display_list.put(remail, new Pair<>(tmp, message));
        else if (display_list.get(remail) == null) {
            usernames.put(remail, uname);
            display_list.put(remail, new Pair<>(tmp, message));
        }
    }
}
