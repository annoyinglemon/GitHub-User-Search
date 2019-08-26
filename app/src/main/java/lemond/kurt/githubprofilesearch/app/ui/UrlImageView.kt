package lemond.kurt.githubprofilesearch.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.AttributeSet
import android.widget.ImageView
import lemond.kurt.githubprofilesearch.R
import java.net.HttpURLConnection
import java.net.URL

private const val THREAD_NAME = "image_downloader_thread"
private const val DOWNLOAD_BITMAP = 99
private const val DOWNLOADED_BITMAP = 100

class UrlImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    var onImageLoadListener: OnImageLoadListener? = null

    private val imageDownloaderThread = ImageDownloaderThread( Handler { message ->
        if (message.what == DOWNLOADED_BITMAP) {
            val downloadedBitmap = message.obj as Bitmap
            this.setImageBitmap(downloadedBitmap)
            onImageLoadListener?.onImageLoad()
        }
        return@Handler true
    })

    var imageUrl: String? = null
        set(value) {
            field = value
            if (!value.isNullOrEmpty()) {
                imageDownloaderThread.startDownload(value)
            }
        }

    init {
        imageDownloaderThread.start()
        imageDownloaderThread.prepareHandler()

        context.theme.obtainStyledAttributes(attrs, R.styleable.UrlImageView, 0, 0).apply {
            try {
                imageUrl = getString(R.styleable.UrlImageView_imageUrl)
            } finally {
                recycle()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        imageDownloaderThread.quit()
    }

    private inner class ImageDownloaderThread(private val uiHandler: Handler): HandlerThread(THREAD_NAME) {
        private lateinit var workerHandler: Handler

        private val onDownloadCallback: ((Message) -> Boolean) = {
            if (it.what == DOWNLOAD_BITMAP) {

                val bitmap = downloadBitMapImage(it.obj as String)
                val resultMessage = Message().apply {
                    this.what = DOWNLOADED_BITMAP
                    this.obj = bitmap
                }

                uiHandler.sendMessage(resultMessage)
            }

            true
        }

        fun startDownload(url: String) {
            val message = workerHandler.obtainMessage(DOWNLOAD_BITMAP, url)

            if (workerHandler.hasMessages(DOWNLOAD_BITMAP)) {
                workerHandler.removeCallbacksAndMessages(null)
                prepareHandler()
            }

            workerHandler.sendMessage(message)
        }

        fun prepareHandler() {
            workerHandler = Handler(looper, onDownloadCallback)
        }

        private fun downloadBitMapImage(url: String): Bitmap {
            return try {
                val httpUrl = URL(url)
                val httpConnection = httpUrl.openConnection() as HttpURLConnection

                val rawBitmap = BitmapFactory.decodeStream(httpConnection.inputStream)

                if (measuredWidth > 0 && measuredHeight > 0) {
                    Bitmap.createScaledBitmap(rawBitmap, measuredWidth, measuredHeight, true)
                } else {
                    rawBitmap
                }

            } catch (exception: Exception) {
                BitmapFactory.decodeResource(resources, R.drawable.ic_image_broken)
            }
        }
    }

    interface OnImageLoadListener {
        fun onImageLoad()
    }

}