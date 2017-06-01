package com.example.hj.testproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;





public class SingleBlogPostPage extends AppCompatActivity {

    private String mPost_key = null;
    private DatabaseReference database;
    private ImageView singlePostImage;
    private TextView singlePostTitle;
    private TextView singlePostDesc;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;


    private DatabaseReference database_comment;
    private EditText comment_et;
    private Button postCommentbtn;
    private ProgressDialog progress;
    private RecyclerView commentlist;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog_post_page);

        database = FirebaseDatabase.getInstance().getReference().child("Blog");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        String mPost_key = getIntent().getExtras().getString("blog_id");

        singlePostImage = (ImageView) findViewById(R.id.single_post_image);
        singlePostTitle = (TextView) findViewById(R.id.single_post_title);
        singlePostDesc = (TextView) findViewById(R.id.single_post_desc);

        //Toast.makeText(SingleBlogPostPage.this, post_key, Toast.LENGTH_LONG).show();

        database.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                //String post_uid = (String) dataSnapshot.child("uid").getValue();

                singlePostTitle.setText(post_title);
                singlePostDesc.setText(post_desc);

                Picasso.with(SingleBlogPostPage.this).load(post_image).into(singlePostImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        database_comment = FirebaseDatabase.getInstance().getReference().child("Comment");
        comment_et = (EditText) findViewById(R.id.field_comment_text);
        postCommentbtn = (Button) findViewById(R.id.button_post_comment);
        commentlist = (RecyclerView) findViewById(R.id.recycler_comments);
        progress = new ProgressDialog(this);

        postCommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        commentlist.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        commentlist.setLayoutManager(mLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comment, CommentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(

                Comment.class,
                R.layout.item_comment,
                CommentViewHolder.class,
                database_comment


        ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {

                viewHolder.setAuthor(model.getAuthor());
                viewHolder.setComment(model.getComment());
                viewHolder.setAuthor_pic(model.getAuthor_pic());



            }
        };

        commentlist.setAdapter(firebaseRecyclerAdapter);

    }



    private void postComment() {

        progress.setMessage("댓글을 달고 있습니다...");
        progress.show();

        final String comment_val = comment_et.getText().toString().trim();

        if (!TextUtils.isEmpty(comment_val)){

            final DatabaseReference newPost = database_comment.push();

            database_comment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPost.child("comment").setValue(comment_val);
                    newPost.child("uid").setValue(currentUser.getEmail());
                    newPost.child("username").setValue(currentUser.getDisplayName())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        comment_et.setText(null);


                                    }

                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });

            progress.dismiss();

        }


    }


    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),BlogActivity.class);
        startActivity(intent);
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        View mView;
        FirebaseAuth firebaseAuth;
        FirebaseUser currentUser;
        DatabaseReference database_comment;
        Context context;





        public CommentViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            database_comment =  FirebaseDatabase.getInstance().getReference().child("Comment");
            firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();



        }

        public void setComment(String comment){
            TextView comment_tv = (TextView)mView.findViewById(R.id.comment_body);
            comment_tv.setText(comment);
        }

        public void setAuthor(String author){
            TextView author_tv = (TextView)mView.findViewById(R.id.comment_author);
            author = currentUser.getDisplayName();
            author_tv.setText(author);
        }

        public void setAuthor_pic(ImageView author_pic){
            ImageView author_iv =(ImageView)mView.findViewById(R.id.comment_photo);
            Picasso.with(context).load(currentUser.getPhotoUrl()).into(author_iv);
        }

    }

}
