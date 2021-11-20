package com.example.outstagram

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_upload.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateActivity : AppCompatActivity() {

    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val cameraPermissionCheck = ContextCompat.checkSelfPermission(
            this@UpdateActivity,
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
        val content = RequestBody.create(MediaType.parse("text/plain"), getContent())
        val pk = intent.getIntExtra("pk", -1)

        (application as MasterApplication).service.updatePost(
            pk, part, content
        ).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    finish()
                    Toast.makeText(this@UpdateActivity, "수정되었습니다.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@UpdateActivity, MyPostListActivity::class.java))
                } else {
                    Toast.makeText(this@UpdateActivity, "400 Bad Request", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Toast.makeText(this@UpdateActivity, "서버 오류", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun getContent(): String {
        return content_input.text.toString()
    }

}
