package com.example.outstagram

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_profile_update.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileUpdateActivity : AppCompatActivity() {
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)


        val cameraPermissionCheck = ContextCompat.checkSelfPermission(
            this@ProfileUpdateActivity,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // 권한이 없는 경우
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
        }

        view_pictures.setOnClickListener { getPicture() }
        upload_post.setOnClickListener { uploadPost() }

        all_list.setOnClickListener { startActivity(Intent(this, PostListActivity::class.java)) }
        my_list.setOnClickListener { startActivity(Intent(this, MyPostListActivity::class.java)) }
        upload.setOnClickListener { startActivity(Intent(this, UploadActivity::class.java)) }
        user_info.setOnClickListener { startActivity(Intent(this, UserInfo::class.java)) }
    }


    fun getPicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val uri: Uri = data!!.data!!
            filePath = getImageFilePath(uri)
        }
    }

    fun getImageFilePath(contentUri: Uri): String {
        var columnIndex = 0
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor.getString(columnIndex)
    }

    fun uploadPost() {
        val file = File(filePath)
        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("image", file.name, fileRequestBody)
        var owner = getUserName()

        if (owner == null)
            owner = ""

        (application as MasterApplication).service.updateProfile(
            owner!!, part
        ).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    finish()
                    Toast.makeText(this@ProfileUpdateActivity, "저장되었습니다.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@ProfileUpdateActivity, UserInfo::class.java))
                } else {
                    Toast.makeText(this@ProfileUpdateActivity, "400 Bad Request", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@ProfileUpdateActivity, "서버 오류", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getUserName(): String? {
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val username = sp.getString("username", "null")
        if (username == "null") return null
        else return username
    }
}