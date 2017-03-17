package com.jokerwan.testipc.messenger;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${JokerWan} on 2017/3/16.
 * WeChat：wjc398556712
 * Function：
 */

public class User implements Parcelable {

    private String name;

    public User(String name) {
        this.name = name;
    }

    protected User(Parcel in) {
        name = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
    }

    public String getName() {
        return name;
    }
}
