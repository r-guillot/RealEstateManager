package com.openclassrooms.realestatemanager.newproperty

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.openclassrooms.realestatemanager.databinding.ItemViewpagerBinding
import com.openclassrooms.realestatemanager.detail.FullScreenImage
import java.io.File


class ViewPagerAdapter(var list: MutableList<Uri>, var context: Context, var width: Float) : PagerAdapter() {
    var bitmap: Bitmap? = null

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemViewpagerBinding.inflate(LayoutInflater.from(context), container, false)
        binding.root
        binding.imageViewViewPager.setImageURI(list[position])
        container.addView(binding.root, 0)

        binding.imageViewViewPager.setOnClickListener {
            if (!list[position].path.isNullOrEmpty()) {
                val intent = Intent(context, FullScreenImage::class.java).apply {
                    putExtra("image", list[position].toString())
                    Log.d("FS", "intent: " + list[position])
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }

        return binding.root
    }

    override fun getPageWidth(position: Int): Float {
        return width
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}