package com.example.hj.testproject;


import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by SoYeon on 2017. 5. 30..
 */

@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String author;
    public String comment;
    public ImageView author_pic;





    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment( String uid, String author, String comment, ImageView author_pic) {


        this.uid = uid;
        this.author = author;
        this.comment = comment;
        this.author_pic = author_pic;

    }



    public String getUid(){

        return uid;

    }

    public void setUid(String uid){

        this.uid = uid;
    }

    public String getAuthor(){

        return author;

    }

    public void setAuthor(String author){

        this.author = author;
    }

    public String getComment(){

        return comment;

    }

    public void setComment(String comment){

        this.comment = comment;

    }

    public ImageView getAuthor_pic(){

        return author_pic;

    }

    public void setAuthor_pic(ImageView author_pic){

        this.author_pic = author_pic;

    }


}