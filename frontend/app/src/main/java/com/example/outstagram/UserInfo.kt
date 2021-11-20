package com.example.outstagram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.outstagram.*
import kotlinx.android.synthetic.main.activity_user_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        var username = getUserName()
        if (username == null)
            username = ""
        user.text = username

        (application as MasterApplication).service.getProfile(
            username!!
        ).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    val profile = response.body()

                    if (profile!!.image == null)
                        setImage("https://github.com/hyunmin0317/Outstagram/blob/master/github/basic.jpg?raw=true")
                    else
                        setImage(profile!!.image)
                } else {
                    setImage("https://github.com/hyunmin0317/Outstagram/blob/master/github/basic.jpg?raw=true")
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@UserInfo, "서버 오류", Toast.LENGTH_LONG).show()
            }
        })

        all_list.setOnClickListener { startActivity(Intent(this, PostListActivity::class.java)) }
        my_list.setOnClickListener { startActivity(Intent(this, MyPostListActivity::class.java)) }
        upload.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
        profile_update.setOnClickListener { startActivity(Intent(this, ProfileUpdateActivity::class.java)) }

        profile_delete.setOnClickListener {
            (application as MasterApplication).service.deleteProfile(username).enqueue(object :
                Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    (application as MasterApplication).service.uploadProfile(username).enqueue(object :
                        Callback<Profile> {
                        override fun onResponse(call: Call<Profile>, response: Response<Profile>) {}

                        override fun onFailure(call: Call<Profile>, t: Throwable) {}
                    })
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {}
            })

            startActivity(Intent(this, UserInfo::class.java))
        }

        logout.setOnClickListener {
            val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("username", "null")
            editor.putString("token", "null")
            editor.commit()
            (application as MasterApplication).createRetrofit()
            finish()
            startActivity(Intent(this, IntroActivity::class.java))
        }
    }
    fun getUserName(): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val username = sp.getString("username", "null")
        if (username == "null") return null
        else return username
    }

    fun setImage(url: String?) {
        Glide.with(this).load(url).into(findViewById(R.id.profile_img))
    }
}
