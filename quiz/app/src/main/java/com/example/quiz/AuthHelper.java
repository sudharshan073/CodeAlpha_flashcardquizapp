
package com.example.quiz;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;

public class AuthHelper {

    private static final String PREFS = "FlashcardAuth";
    private static final String USERS = "RegisteredUsers";
    private static final String SESSION = "LoginSession";

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(
                PREFS, Context.MODE_PRIVATE);
    }

    private static String hash(String value) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(
                    value.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b & 0xff));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }

    public static boolean register(
            Context context, String name,
            String username, String password) {

        SharedPreferences p = prefs(context);
        String key = username.trim().toLowerCase();

        if (p.contains("user_" + key)) {
            return false;
        }

        p.edit()
                .putString("user_" + key, name.trim())
                .putString("pass_" + key, hash(password))
                .apply();

        return true;
    }

    public static boolean login(
            Context context, String username, String password) {

        SharedPreferences p = prefs(context);
        String key = username.trim().toLowerCase();
        String savedHash = p.getString("pass_" + key, null);

        if (savedHash == null ||
                !savedHash.equals(hash(password))) {
            return false;
        }

        p.edit()
                .putBoolean("logged_in", true)
                .putString("current_user", key)
                .apply();

        return true;
    }

    public static boolean isLoggedIn(Context context) {
        return prefs(context).getBoolean("logged_in", false);
    }

    public static String getName(Context context) {
        SharedPreferences p = prefs(context);
        String username = p.getString("current_user", "");
        return p.getString("user_" + username, "Student");
    }

    public static void logout(Context context) {
        prefs(context).edit()
                .putBoolean("logged_in", false)
                .remove("current_user")
                .apply();
    }
}