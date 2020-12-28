package com.cn.lib.util


import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.cn.lib.util.GlideUtil.GlideEnum.HEAD_IMAGE
import java.io.File
import java.security.MessageDigest


/**
 * Glide工具类
 * Created by heym on 16/9/22.
 */
object GlideUtil {

    /**
     * 加载图片
     */
    fun loadImage(mContext: Context, url: String, imageView: ImageView, errorId: Int) {
        val options = RequestOptions()
        options.error(errorId).placeholder(errorId)
        val requestBuilder = Glide.with(mContext).asBitmap().load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
        requestBuilder.apply(options).into<BitmapImageViewTarget>(object : BitmapImageViewTarget(imageView) {
            override fun setResource(resource: Bitmap?) {
                imageView.setImageBitmap(resource)

            }
        })
    }

    /**
     * 加载图片
     */
    @JvmOverloads
    fun loadImage(mContext: Context, url: String, imageView: ImageView, glideEnum: GlideEnum, isCompress: Boolean = true) {
        loadImage(mContext, url, imageView, glideEnum, null, isCompress)
    }

    //    _100x50.jpg
    fun loadImage(mContext: Context, url: String, imageView: ImageView, glideEnum: GlideEnum, imageLoadListener: ImageLoadListener?, isCompress: Boolean) {
        val imageId = getImgIdByEnum(glideEnum)
        //        int width = imageView.getWidth();
        //        int height = imageView.getHeight();
        //        if (isCompress && !StringUtils.isEmpty(url) && width != 0 && height != 0) {//获取文件后缀
        //            int lastIndexOf = url.lastIndexOf(".");
        //            if (lastIndexOf > -1) {
        //                String urlPostfix = url.substring(lastIndexOf);
        //                url = url + "_" + width + "x" + height + urlPostfix;
        //            }
        //        }
        val options = RequestOptions()
        options.error(imageId).placeholder(imageId)
        if (HEAD_IMAGE == glideEnum) {
            options.circleCrop()
        }
        val requestBuilder = Glide.with(mContext).asBitmap().load(url)
                .transition(BitmapTransitionOptions.withCrossFade())
        if (glideEnum == GlideEnum.RADIUS_IMAGE) {
            options.transform(GlideRoundTransform(mContext, 10))
            requestBuilder.apply(options).into<BitmapImageViewTarget>(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    imageView.setImageBitmap(resource)
                    imageLoadListener?.imageSuccess(resource)
                }
            })
        } else {
            requestBuilder.apply(options).into<BitmapImageViewTarget>(object : BitmapImageViewTarget(imageView) {
                override fun setResource(resource: Bitmap?) {
                    if (HEAD_IMAGE == glideEnum) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        imageView.setImageDrawable(circularBitmapDrawable)
                    } else {
                        imageView.setImageBitmap(resource)
                    }
                    imageLoadListener?.imageSuccess(resource)
                }
            })
        }


    }

    private fun getImgIdByEnum(glideEnum: GlideEnum): Int {
        //        switch (glideEnum) {
        //            case BIG_IMAGE:
        //                return R.mipmap.icon_big_default;
        //            case SMALL_IMAGE:
        //                return R.mipmap.icon_small_default;
        //            case HEAD_IMAGE:
        //                return R.mipmap.default_user_header;
        //            default:
        //                return R.mipmap.icon_small_default;
        //        }
        return 0
    }


    interface ImageLoadListener {
        fun imageSuccess(bitmap: Bitmap?)
    }

    enum class GlideEnum {
        BIG_IMAGE, //默认大图片
        SMALL_IMAGE, //默认小图片
        HEAD_IMAGE, //头像图片
        RADIUS_IMAGE
        //有圆角的图片
    }


    /**
     * 清除缓存
     */
    fun clearCache(mContext: Context) {
        //内部缓存的路径
        val cacheDir = File(mContext.cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
        if (cacheDir.list() != null) {
            deleteFolderFile(cacheDir.absolutePath, true)
        }
        //外部缓存的路径
        val externalCacheDir = File(mContext.externalCacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
        deleteFolderFile(externalCacheDir.absolutePath, true)
    }

    /**
     * 获取缓存文件的大小
     */
    fun getCacheSize(mContext: Context): Long {
        var cacheSize: Long = 0
        //内部缓存的路径
        val cacheDirectory = mContext.cacheDir
        val cacheDir = File(cacheDirectory, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
        if (cacheDir.list() != null) {
            try {
                cacheSize += getFolderSize(cacheDir)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        //外部缓存的路径
        val externalCacheDirectory = mContext.externalCacheDir
        val externalCacheDir = File(externalCacheDirectory, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)
        if (externalCacheDir.list() != null) {
            try {
                cacheSize += getFolderSize(externalCacheDir)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return cacheSize
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                if (aFileList.isDirectory) {
                    size = size + getFolderSize(aFileList)
                } else {
                    size = size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().size == 0) {
                            file.delete()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    private class GlideRoundTransform @JvmOverloads constructor(context: Context, dp: Int = 4) : BitmapTransformation(context) {

        init {
            radius = Resources.getSystem().displayMetrics.density * dp
        }

        override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap? {
            return roundCrop(pool, toTransform)
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {

        }

        companion object {
            private var radius = 0f

            private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
                if (source == null) return null
                val result = pool.get(source.width, source.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(result)
                val paint = Paint()
                paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                paint.isAntiAlias = true
                val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
                canvas.drawRoundRect(rectF, radius, radius, paint)
                return result
            }
        }
    }

}
/**
 * 加载图片
 */
