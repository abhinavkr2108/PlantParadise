package com.example.plantparadise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creating ArrayList of Images to be displayed in SlideShow
        val imageList = ArrayList<SlideModel>()

        //Adding Images to the ArrayList
        imageList.add(SlideModel("https://raw.githubusercontent.com/abhinavkr2108/Image/main/Images/Images/Images/Slider1.png", ""))
        imageList.add(SlideModel("https://raw.githubusercontent.com/abhinavkr2108/Image/main/Images/Images/Images/slider2.png", ""))
        imageList.add(SlideModel("https://raw.githubusercontent.com/abhinavkr2108/Image/main/Images/Images/Images/slider3.png", ""))
        imageList.add(SlideModel("https://raw.githubusercontent.com/abhinavkr2108/Image/main/Images/Images/Images/slider4.png", ""))

        // Setting Image from ArrayList to our Activity
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        //Linking CardViews to their respected Activities
        cvPlantName.setOnClickListener{
            val intent = Intent(this, NameActivity::class.java)
            startActivity(intent)
        }
        cvPlantDisease.setOnClickListener{
            val intent = Intent(this, DiseaseActivity::class.java)
            startActivity(intent)
        }
        cvPlantBlogs.setOnClickListener {
            val intent = Intent(this, BlogActivity::class.java)
            startActivity(intent)
        }

    }
}