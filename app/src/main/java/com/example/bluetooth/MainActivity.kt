package com.example.bluetooth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class MainActivity : AppCompatActivity(), PrintingCallback {

    internal var printing : Printing?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            initView()
    }

    private fun initView(){
       if(printing != null)
       {
           printing!!.printingCallback = this
       }

        val buttonpair : Button = findViewById(R.id.btnPairunpair)
        buttonpair.setOnClickListener {
            if(Printooth.hasPairedPrinter())
                Printooth.removeCurrentPrinter()
            else{
                startActivityForResult(Intent(this@MainActivity, ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
                changepairUnpair()
            }
        }

        val buttonPrintimage : Button = findViewById(R.id.btnPrintimage)
        buttonPrintimage.setOnClickListener {
            if(!Printooth.hasPairedPrinter())
            {
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            }
            else
                printImage()
        }

        val buttonPrinttext : Button = findViewById(R.id.btnPrint)
        buttonPrinttext.setOnClickListener {
            if(!Printooth.hasPairedPrinter())
            {
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java), ScanningActivity.SCANNING_FOR_PRINTER)
            }
            else
                printText()
        }
    }

    private fun printText() {
        val printables = ArrayList<Printable>()
        printables.add(RawPrintable.Builder(byteArrayOf(27,100,4)).build())

        // Add Text
        printables.add(TextPrintable.Builder()
            .setText("Helllo World")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())
        // Custom Text
        printables.add(TextPrintable.Builder()
            .setText("hello World")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build()
        )

        printing!!.print(printables)
    }

    private fun printImage() {
        val printables = ArrayList<Printable>()

        // load Bitmap from internet
        Picasso.get().load("https://cdn.freebiesupply.com/logos/large/2x/android-logo-black-and-white.png")
            .into(object:Target{
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    printables.add(ImagePrintable.Builder(bitmap!!).build())
                    printing?.print(printables)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    TODO("Not yet implemented")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun changepairUnpair() {
        val buttonpair : Button = findViewById(R.id.btnPairunpair)
        if(Printooth.hasPairedPrinter()){
            buttonpair.text = "Unpair ${Printooth.getPairedPrinter()?.name}"
        }
        else
        {
            buttonpair.text = "Pair with Printer"
        }
    }

    override fun connectingWithPrinter() {
        Toast.makeText(this, "Connecting to the printer", Toast.LENGTH_LONG).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this, "failed", Toast.LENGTH_LONG).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this, "Error : $error", Toast.LENGTH_LONG).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this, "Message : $message", Toast.LENGTH_LONG).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this, "Order sent to Printer", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK)
            initPrinting();
        changepairUnpair()
    }

    private fun initPrinting() {
        if(Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        if(printing != null)
            printing!!.printingCallback = this
    }
}