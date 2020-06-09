package br.com.unb.smms.view.activity


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.unb.smms.R
import br.com.unb.smms.extension.toJson
import br.com.unb.smms.security.SecurityConstants
import br.com.unb.smms.security.getEncrypSharedPreferences
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_select_social_media.*


class SelectSocialMediaActivity : AppCompatActivity() {

    var callbackManager = CallbackManager.Factory.create();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_social_media)

        FacebookSdk.setGraphApiVersion("v7.0")

        LoginManager.getInstance().logInWithReadPermissions(
            this@SelectSocialMediaActivity,
            listOf(
                "user_likes",
                "user_photos",
                "user_status",
                "user_friends",
                "user_posts",
                "email",
                "public_profile",
                "pages_show_list",
                "pages_manage_ads",
                "pages_manage_metadata",
                "pages_manage_engagement",
                "pages_read_engagement",
                "pages_read_user_content",
                "pages_manage_posts",
                "instagram_basic",
                "instagram_manage_comments",
                "instagram_manage_insights"
            )
        );

        LoginManager.getInstance().logInWithPublishPermissions(
            this@SelectSocialMediaActivity,
            listOf(
                "manage_pages",
                "publish_pages"
            )
        )

        if (getEncrypSharedPreferences(this@SelectSocialMediaActivity).getString(
                SecurityConstants.LOGIN_RESULT,
                ""
            )!!.isNotEmpty()
        ) {
            startActivity(Intent(this@SelectSocialMediaActivity, SmmsActivity::class.java))
            return
        }



        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {

                getEncrypSharedPreferences(this@SelectSocialMediaActivity).edit()
                    .putString(SecurityConstants.LOGIN_RESULT, loginResult!!.toJson()).apply()
                getEncrypSharedPreferences(this@SelectSocialMediaActivity).edit()
                    .putString(SecurityConstants.ACCESS_TOKEN, loginResult.accessToken.token).apply()
                startActivity(Intent(this@SelectSocialMediaActivity, SmmsActivity::class.java))
            }

            override fun onCancel() {
                Toast.makeText(this@SelectSocialMediaActivity, "Cancelado", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(
                    this@SelectSocialMediaActivity,
                    exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
