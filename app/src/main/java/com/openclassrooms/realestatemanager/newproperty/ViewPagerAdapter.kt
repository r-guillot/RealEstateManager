package com.openclassrooms.realestatemanager.newproperty

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.openclassrooms.realestatemanager.databinding.ItemViewpagerBinding
import com.openclassrooms.realestatemanager.detail.FullScreenImage


class ViewPagerAdapter(var list: List<Uri>, var context: Context, var width: Float, private var photoDescription: List<String>?) : PagerAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    //display photo and description from list to the view pager
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemViewpagerBinding.inflate(LayoutInflater.from(context), container, false)
        binding.root
        binding.imageViewViewPager.setImageURI(list[position])

        if (photoDescription != null && photoDescription?.size!! > position &&
                photoDescription?.get(position).toString().isNotBlank()){
            binding.textViewImageDescription.text = photoDescription?.get(position)
            binding.textViewImageDescription.visibility = View.VISIBLE
        } else {
            binding.textViewImageDescription.visibility = View.GONE
        }
        container.addView(binding.root, 0)

        binding.imageViewViewPager.setOnClickListener {
            if (!list[position].path.isNullOrEmpty()) {
                val intent = Intent(context, FullScreenImage::class.java).apply {
                    putExtra("image", list[position].toString())
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
        return binding.root
    }

    //depending on value of width, the viewPager will displayed one or two images
    override fun getPageWidth(position: Int): Float {
        return width
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}