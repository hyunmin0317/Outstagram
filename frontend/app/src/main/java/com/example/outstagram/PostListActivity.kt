package com.example.outstagram

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
import kotlinx.android.synthetic.main.activity_post_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostListActivity : AppCompatActivity() {

    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_list)

        glide = Glide.with(this)

        (application as MasterApplication).service.getAllPosts().enqueue(
            object : Callback<ArrayList<Post>> {
                override fun onResponse(
                    call: Call<ArrayList<Post>>,
                    response: Response<ArrayList<Post>>
                ) {
                    if (response.isSuccessful) {
                        val postList = response.body()
                        val adapter = PostAdapter(
                            postList!!,
                            LayoutInflater.from(this@PostListActivity),
                            glide
                        )
                        postList.reverse()
                        post_recyclerview.adapter = adapter
                        post_recyclerview.layoutManager = LinearLayoutManager(this@PostListActivity)
                    } else {
                        Toast.makeText(this@PostListActivity, "400 Bad Request", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                    Toast.makeText(this@PostListActivity, "서버 오류", Toast.LENGTH_LONG).show()
                }
            }
        )

        user_info.setOnClickListener { startActivity(Intent(this, UserInfo::class.java)) }
        my_list.setOnClickListener { startActivity(Intent(this, MyPostListActivity::class.java)) }
        upload.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
    }
}

class PostAdapter(
    var postList: ArrayList<Post>,
    val inflater: LayoutInflater,
    val glide: RequestManager
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postOwner: TextView
        val postImage: ImageView
        val postContent: TextView

        init {
            postOwner = itemView.findViewById(R.id.post_owner)
            postImage = itemView.findViewById(R.id.post_img)
            postContent = itemView.findViewById(R.id.post_content)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.outstagram_item_view, parent, false)
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
