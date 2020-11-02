package br.com.unb.smms.view.fragment

import DatePickerFragment
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentNewPostBinding
import br.com.unb.smms.utils.AlarmReceiver
import br.com.unb.smms.viewmodel.NewPostViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        const val SELECTED_PIC = 1
    }

    lateinit var binding: FragmentNewPostBinding
    private val viewModel: NewPostViewModel by activityViewModels()

    private var bitmap: Bitmap? = null
    private var userSelectedPhoto: Boolean = false
    lateinit var mStorageRef: StorageReference
    private var localUri : Uri? = null


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

        viewModel.dataLoading.observe(viewLifecycleOwner, { loading ->
            binding.clLoading.visibility = if (loading) View.VISIBLE else View.INVISIBLE
        })

        viewModel.resultUploadPhoto.observe(viewLifecycleOwner, {
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
                is SmmsData.Success -> {
                    viewModel.pendingProcess.value?.let { isPending ->
                        if (isPending) {
                            viewModel.downloadPhotoUrl.value = it.data.toString()
                            viewModel.savePendingPost()
                            return@observe
                        }
                    }

                    if (viewModel.postFacebook.value!!) {
                        viewModel.downloadPhotoUrl.value = it.data.toString()
                        viewModel.feed()
                    } else if (viewModel.postInsta.value!! || viewModel.postInstaStory.value!!) {
                        createInstagramIntent()
                    }

                }
            }
        })

        viewModel.resultPost.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is SmmsData.Error -> Toast.makeText(
                    context,
                    it.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
                is SmmsData.Success -> {
                    copy()
                    if (binding.ckInsta.isChecked || binding.ckInstaStory.isChecked) {
                        Handler().postDelayed({
                            Toast.makeText(
                                context,
                                "Publicado com sucesso no Facebook, aguarde enquanto redirecionamos para o Instagram! O seu texto foi copiado na área de transferência.",
                                Toast.LENGTH_LONG
                            ).show()
                            createInstagramIntent()
                        }, 2500)
                        viewModel.resetAllFields()
                        binding.clUploadPhoto.visibility = View.VISIBLE
                        binding.ivPhoto.visibility = View.GONE
                    } else {
                        Toast.makeText(
                            context,
                            "Seu post foi publicado com sucesso no Facebook!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        ArrayAdapter.createFromResource(
            requireContext(), R.array.string_array_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategories.adapter = adapter
        }

        viewModel.downloadPhotoUrl.observe(viewLifecycleOwner, {
            if (it.isNullOrBlank()) {
                binding.ivPhoto.visibility = View.GONE
                binding.clUploadPhoto.visibility = View.VISIBLE
            }
        })

        binding.spinnerCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.categorySelected.value = "day"
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val categories = resources.getStringArray(R.array.string_array_categories)
                    viewModel.categorySelected.value = categories[position]
                }
            }
    }

    private fun copy() {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("texto", viewModel.textPost.value)
        clipboard.setPrimaryClip(clip)
    }

    private fun createInstagramIntent() {
        val type = "image/*"
        createInstagramIntent(type);
    }

    private fun createInstagramIntent(type: String) {
        if (viewModel.postInsta.value!!) {
            val share = Intent("com.instagram.share.ADD_TO_FEED")
            share.type = type
            share.putExtra(Intent.EXTRA_STREAM, localUri)
            startActivity(share)
        } else {
            val share = Intent("com.instagram.share.ADD_TO_STORY");
            share.setDataAndType(localUri, type);
            share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            share.putExtra("content_url", "https://www.google.com");

            if (activity?.packageManager?.resolveActivity(share, 0) != null) {
                activity?.startActivityForResult(share, 0);
            }
        }
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

        if (!viewModel.validateFields()) {
            Toast.makeText(
                context,
                "Campos de publicação incompletos.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (viewModel.uriPhoto.value != null) {
                viewModel.uploadFirebaseImage()
            } else if (binding.ckFace.isChecked) {
                viewModel.feed()
            } else if (binding.ckInsta.isChecked || binding.ckInstaStory.isChecked) {
                createInstagramIntent()
            }
        }
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
                if (data == null) return
                localUri = data.data

                if (localUri != null && DocumentsContract.isDocumentUri(context, localUri)) {
                    val docId = DocumentsContract.getDocumentId(localUri)
                    if ("com.android.providers.media.documents" == localUri!!.authority) {
                        val id = docId.split(":")[1]
                        val selsetion = MediaStore.Images.Media._ID + "=" + id
                        viewModel.uriPhoto.value =
                            imagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selsetion)
                    } else if ("com.android.providers.downloads.documents" ==
                        localUri!!.authority
                    ) {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(docId)
                        )
                        viewModel.uriPhoto.value = imagePath(contentUri, null)
                    }
                } else if ("content".equals(localUri!!.scheme, ignoreCase = true)) {
                    viewModel.uriPhoto.value = imagePath(localUri, null)
                } else if ("file".equals(localUri!!.scheme, ignoreCase = true)) {
                    viewModel.uriPhoto.value = localUri!!.path
                }

                userSelectedPhoto = true
                displayImage()
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

    private fun displayImage() {
        if (viewModel.uriPhoto.value != null) {
            bitmap = BitmapFactory.decodeFile(viewModel.uriPhoto.value)
            binding.ivPhoto.setImageBitmap(bitmap)
            binding.clUploadPhoto.visibility = View.INVISIBLE
            binding.ivPhoto.visibility = View.VISIBLE
        } else {
            Toast.makeText(context, "Failed to get image", Toast.LENGTH_SHORT).show()
        }
    }

    fun showTimerPickerFragment(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(parentFragmentManager, "date_picker")
    }

    fun cancelAlarm(view: View) {
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.cancel(pendingIntent)
//        alarm_time_text.text = getString(R.string.time_dosent_set)
    }

    fun goToSchedule(view: View) {
        findNavController().navigate(R.id.action_newPostFragment_to_scheduleActivity)
    }

}
