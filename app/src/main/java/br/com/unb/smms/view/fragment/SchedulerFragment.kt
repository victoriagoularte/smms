package br.com.unb.smms.view.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.unb.smms.R
import br.com.unb.smms.SmmsData
import br.com.unb.smms.databinding.FragmentSchedulerBinding
import br.com.unb.smms.domain.firebase.Post
import br.com.unb.smms.view.adapter.PostAdapter
import br.com.unb.smms.viewmodel.SchedulerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.net.URL


@AndroidEntryPoint
class SchedulerFragment : Fragment() {

    lateinit var binding: FragmentSchedulerBinding
    private val viewModel: SchedulerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSchedulerBinding.inflate(inflater, container, false)
        binding.fragment = this@SchedulerFragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.progressBar.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPosts()

        binding.rvPostsPending.layoutManager = LinearLayoutManager(context)

        viewModel.dataLoading.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })

        viewModel.posts.observe(viewLifecycleOwner, {
            when (it) {
                is SmmsData.Success -> {
                    if (it.data.isEmpty()) {
                        binding.tvNoPendingPosts.visibility = View.VISIBLE
                        binding.rvPostsPending.visibility = View.GONE
                    } else {
                        binding.tvNoPendingPosts.visibility = View.GONE
                        binding.rvPostsPending.visibility = View.VISIBLE
                        binding.rvPostsPending.adapter = PostAdapter(it.data) { post ->
                            selectedPost(
                                post
                            )
                        }
                    }
                }
                is SmmsData.Error -> {
                    Toast.makeText(requireContext(), it.error.localizedMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        viewModel.resultPost.observe(viewLifecycleOwner, {
            when (it) {
                is SmmsData.Success -> {
                    copy()
                    if (viewModel.postUpdating.value?.media != null) {
                        Handler().postDelayed({
                            Toast.makeText(
                                context,
                                "Publicado com sucesso no Facebook, aguarde enquanto redirecionamos para o Instagram! O seu texto foi copiado na área de transferência.",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.postUpdating.value.let { post ->
                                post?.media?.let {
                                    for (media in post.media!!) {
                                        when (media) {
                                            "instagram" -> postInstaFeed(post.urlPicture, post)
                                            "insta_story" -> postInstaStory(post.urlPicture, post)
                                        }
                                    }
                                }

                            }
                        }, 2500)
                    } else {
                        Toast.makeText(
                            context,
                            "Seu post foi publicado com sucesso no Facebook!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is SmmsData.Error -> Toast.makeText(
                    context,
                    "error: post nao efetuado",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        viewModel.resultUpdate.observe(viewLifecycleOwner, {
            when (it) {
                is SmmsData.Success -> {
                    Toast.makeText(context, "post atualizado com sucesso", Toast.LENGTH_LONG).show()
                    viewModel.getPosts()
                }
                is SmmsData.Error -> Toast.makeText(
                    context,
                    "error: post nao atualizado",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun selectedPost(post: Post) {

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.confirm_post))
            .setMessage(
                (String.format(
                    resources.getString(R.string.body_confirmation),
                    post.title
                ))
            )

        builder.apply {
            setPositiveButton(
                R.string.ok
            ) { _, _ ->
                post.media?.let {
                    for(media in it) {
                        when(media) {
                            "facebook" -> viewModel.postPublishPending(post)
                            "instagram" -> postInstaFeed(post.urlPicture, post)
                            "insta_story" -> postInstaStory(post.urlPicture, post)

                        }
                    }
                }

            }
            setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.create().show()
    }

    private fun copy() {
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("texto", viewModel.textPost.value)
        clipboard.setPrimaryClip(clip)
    }

    private fun postInstaFeed(localUri: String?, post: Post) {
        val url = URL(localUri)
        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        val share = Intent("com.instagram.share.ADD_TO_FEED")
        share.type = "image/*"
        share.putExtra(Intent.EXTRA_STREAM, getImageUri(requireContext(), image))
        startActivity(share)
        viewModel.updatePostPending(post)
    }

    private fun postInstaStory(localUri: String?, post: Post) {

        val url = URL(localUri)
        val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())

        val share = Intent("com.instagram.share.ADD_TO_STORY");
        share.setDataAndType(getImageUri(requireContext(), image), "image/*");
        share.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        share.putExtra("content_url", "https://www.google.com");

        if (activity?.packageManager?.resolveActivity(share, 0) != null) {
            activity?.startActivityForResult(share, 0);
        }

        viewModel.updatePostPending(post)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

}