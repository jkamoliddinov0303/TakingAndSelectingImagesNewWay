package uz.jkamoliddinov.takingandselectingimagesnewway

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import java.io.File

class MainActivity : AppCompatActivity() {
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    previewImage.setImageURI(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { previewImage.setImageURI(uri) }
        }

    private var latestTmpUri: Uri? = null

    private val previewImage by lazy { findViewById<ImageView>(R.id.image_preview) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setClickListeners()
    }

    private fun setClickListeners() {
        findViewById<MaterialButton>(R.id.take_image_button).setOnClickListener { takeImage() }
        findViewById<MaterialButton>(R.id.select_image_button).setOnClickListener { selectImageFromGallery() }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            applicationContext,
            "${packageName}.provider",
            tmpFile
        )
    }
}