package com.example.outstagram

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_my_post_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostListActivity : AppCompatActivity() {

    lateinit var myPostRecyclerView: RecyclerView
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_post_list)

        myPostRecyclerView = mypost_recyclerview
        glide = Glide.with(this@MyPostListActivity)
        createList()
        user_info.setOnClickListener { startActivity(Intent(this, UserInfo::class.java)) }
        all_list.setOnClickListener { startActivity(Intent(this, PostListActivity::class.java)) }
        upload.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
    }

    fun createList() {
        (application as MasterApplication).service.getUserPostList().enqueue(
            object : Callback<ArrayList<Post>> {
                override fun onResponse(
                    call: Call<ArrayList<Post>>,
                    response: Response<ArrayList<Post>>
                ) {
                    if (response.isSuccessful) {
                        val myPostList = response.body()
                        val adapter = MyPostAdapter(
                            myPostList!!,
                            LayoutInflater.from(this@MyPostListActivity),
                            glide,
                            this@MyPostListActivity
                        )
                        myPostList.reverse()
                        myPostRecyclerView.adapter = adapter
                        myPostRecyclerView.layoutManager = LinearLayoutManager(this@MyPostListActivity)
                    }
                }

                override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                    Toast.makeText(this@MyPostListActivity, "서버 오류", Toast.LENGTH_LONG).show()
                }
            }
        )

    }

}

class MyPostAdapter(
    var postList: ArrayList<Post>,
    val inflater: LayoutInflater,
    val glide: RequestManager,
    val activity: Activity

) : RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postOwner: TextView
        val postImage: ImageView
        val postContent: TextView

        init {
            postOwner = itemView.findViewById(R.id.post_owner)
            postImage = itemView.findViewById(R.id.post_img)
            postContent = itemView.findViewById(R.id.post_content)

            itemView.findViewById<TextView>(R.id.delete).setOnClickListener {
                (activity.application as MasterApplication).service.deletePost(
                    postList.get(adapterPosition).id!!
                ).enqueue(object : Callback<Post> {

                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (response.isSuccessful) {
                            Toast.makeText(activity, "삭제되었습니다.", Toast.LENGTH_LONG).show()
                            activity.startActivity(
                                Intent(
                                    activity,
                                    MyPostListActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(activity, "삭제 오류", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        Toast.makeText(activity, "서버 오류", Toast.LENGTH_LONG).show()
                    }
                })
            }

            itemView.findViewById<TextView>(R.id.update).setOnClickListener {
                val intent = Intent(activity, UpdateActivity::class.java)
                intent.putExtra("pk", postList.get(adapterPosition).id)
                activity.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.myitem_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.postOwner.setText(postList.get(position).owner)
        holder.postContent.setText(postList.get(position).content)
        glide.load(postList.get(position).image).into(holder.postImage)
    }
}