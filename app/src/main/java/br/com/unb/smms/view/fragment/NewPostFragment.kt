package br.com.unb.smms.view.fragment

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentNewPostBinding
import br.com.unb.smms.viewmodel.NewPostViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_post.*
import java.io.File


class NewPostFragment : Fragment() {

    companion object {
        const val SELECTED_PIC = 1
    }

    lateinit var binding: FragmentNewPostBinding
    private lateinit var database: DatabaseReference
    private var bitmap: Bitmap? = null


    private var imagePath: String? = null
    private var userSelectedPhoto: Boolean = false
    lateinit var mStorageRef: StorageReference
    var downloadUri: Uri? = null
    private var localUri : Uri? = null

    private val viewModel: NewPostViewModel by lazy {
        ViewModelProvider(this).get(NewPostViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.fragment = this@NewPostFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        mStorageRef = FirebaseStorage.getInstance().reference;

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataLoading.observe(viewLifecycleOwner, Observer<Boolean> { loading ->
            clLoading.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        })

        viewModel.resultPost.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
                is SmmsData.Success -> {
                    resetAllFields()
                    Toast.makeText(
                        context,
                        "Publicado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    }

    fun choosePhoto(view: View) {
        val checkSelfPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECTED_PIC)
        }
    }

    fun post(view: View?) {
        viewModel.feed(downloadUri)
//        createInstagramIntent();
    }

    private fun createInstagramIntent() {
        val type = "image/*"
        createInstagramIntent(type);
    }

    private fun createInstagramIntent(type: String) {
        val share = Intent("com.instagram.share.ADD_TO_FEED")
        share.type = type
        val uri = localUri
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(share)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 ->
                if (grantResults.isEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECTED_PIC -> {
                 localUri = data!!.data

                if (localUri != null && DocumentsContract.isDocumentUri(context, localUri)) {
                    val docId = DocumentsContract.getDocumentId(localUri)
                    if ("com.android.providers.media.documents" == localUri!!.authority) {
                        val id = docId.split(":")[1]
                        val selsetion = MediaStore.Images.Media._ID + "=" + id
                        imagePath =
                            imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
                    } else if ("com.android.providers.downloads.documents" ==
                        localUri!!.authority) {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)
                        )
                        imagePath = imagePath(contentUri, null)
                    }
                } else if ("content".equals(localUri!!.scheme, ignoreCase = true)) {
                    imagePath = imagePath(localUri, null)
                } else if ("file".equals(localUri!!.scheme, ignoreCase = true)) {
                    imagePath = localUri!!.path
                }

                userSelectedPhoto = true
                displayImage(imagePath)
                uploadImageFirebase()

            }
        }
    }

    private fun uploadImageFirebase() {

        val file = Uri.fromFile(File(imagePath))
        val ref: StorageReference = mStorageRef.child("images/${file.lastPathSegment}")
        var uploadTask = ref.putFile(file)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result!!
            }
        }

    }

    private fun imagePath(uri: Uri?, selection: String?): String {
        var path: String? = null

        val cursor = requireActivity().contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath)
            binding.ivPhoto?.setImageBitmap(bitmap)
            binding.clUploadPhoto.visibility = View.INVISIBLE
            binding.ivPhoto.visibility = View.VISIBLE
        } else {
            Toast.makeText(context, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetAllFields() {
        viewModel.text.value = ""
        viewModel.title.value = ""
        viewModel.tags.value = ""
        binding.clUploadPhoto.visibility = View.VISIBLE
        binding.ivPhoto.visibility = View.GONE
    }


}
