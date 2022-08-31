package com.example.plantparadise

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.plantparadise.ml.ModelUnquant
import kotlinx.android.synthetic.main.activity_name.*
import kotlinx.android.synthetic.main.activity_name.view.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer


class NameActivity : AppCompatActivity() {
    lateinit var imageBitmap: Bitmap
    val imageSize = 224
    //lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val REQUEST_IMAGE_CAPTURE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

        //Moving back on pressing the arrow back button
        nameArrowBack.setOnClickListener {
            finish()
        }
        //Opening the camera on pressing the capture image button
        btnCaptureName.setOnClickListener {
            //Checking for camera permission
            //If function CheckForCameraPermission returns true, we have got permission to open camera
            if (CheckForCameraPermission()==true){
                LaunchCamera()
            }
            // Execute else block if camera permission is not granted
            else{
                RequestForCameraPermission()
                Toast.makeText(this, "We require Camera permissions to Launch Camera", Toast.LENGTH_SHORT).show()
            }
        }

        btnName.setOnClickListener{
            Log.d("mssg", "button pressed")
            var intent  = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
        }

    }
    // Function to check for camera permission
    private fun CheckForCameraPermission(): Boolean{
        return ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
    // Function to request for camera permission
    private fun RequestForCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        }
    }
    // Function to launch camera after getting the camera permission
    private fun LaunchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Unable to Open Camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun BrowseImage(data: Intent?) {
        imgName.setImageURI(data?.data)

        var uri : Uri?= data?.data
        imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

    }

    private fun PredictImage(imageBitmap: Bitmap){
        val fileName = "labels.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        var plantList = inputString.split("\n")

        val model = ModelUnquant.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        var byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4*imageSize*imageSize*3)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        var confidence: FloatArray = outputFeature0.floatArray

        //Find index of the class with biggest confidence
        var maxPosition = 0
        var maxConfidence = 0.0f

        // For loop for iterating each confidence and selecting the maximum confidence
        for (i in confidence.indices){
            if (confidence[i] > maxConfidence){
                maxPosition = i
                maxConfidence = confidence[i]
            }
        }
        //val classes = arrayOf("Aloevera", "Bamboo", "Cactus", "Holy Basil", "Madagascar Periwinkle", "Money Plant", "Snake Plant" )
        tvPredictedImage.text = "Predicted Image is ${plantList[maxPosition]}"

        // Releases model resources if no longer used.
        model.close()
    }
    //Load the captured image to the image view after clicking the photo from camera

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap //Converting image to bitmap
            val dimension = Math.min(imageBitmap.height, imageBitmap.width)

            //imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap,dimension,dimension)
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,224,224,true)

            imgName.setImageBitmap(imageBitmap) //Setting the image to image view

            btnCaptureName.visibility = View.GONE
            btnName.visibility = View.GONE
            nameInstructions.visibility = View.GONE

            btnCaptureAgainName.visibility = View.VISIBLE
            btnBrowseAgainName.visibility = View.VISIBLE
            btnPredictName.visibility = View.VISIBLE

            btnPredictName.setOnClickListener {
                PredictImage(imageBitmap)
            }

        }
        else  if(requestCode == 250){
            BrowseImage(data)
            btnCaptureName.visibility = View.GONE
            btnName.visibility = View.GONE
            nameInstructions.visibility = View.GONE

            btnCaptureAgainName.visibility = View.VISIBLE
            btnBrowseAgainName.visibility = View.VISIBLE
            btnPredictName.visibility = View.VISIBLE

            btnPredictName.setOnClickListener {
                PredictImage(imageBitmap)
            }
        }
        else{
            Toast.makeText(this, "Couldn't load Image", Toast.LENGTH_SHORT).show()
        }
        //Launch the camera again after pressing capture again button
        btnCaptureAgainName.setOnClickListener {
            LaunchCamera()
        }

        btnBrowseAgainName.setOnClickListener {
            Log.d("mssg", "button pressed")
            var intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
            BrowseImage(data)
        }



        
    }




}