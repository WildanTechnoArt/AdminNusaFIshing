package com.admin.nusafishing

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mCodeProduct: String? = null
    private var mBrandName: String? = null
    private var mCodeData: String? = null
    private var mBarcode: Int = 0
    private var mCount: Long = 1
    private lateinit var mAmount: String
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepare()
        btn_save.setOnClickListener {
            getInputData()
        }
    }

    private fun getInputData() {
        mCount = 1
        mBrandName = tie_brand.text.toString()
        mCodeProduct = tie_code_product.text.toString()
        mBarcode = Integer.valueOf(tie_barcode.text.toString())
        mAmount = tie_amount.text.toString()
        showProgress()
        inputToDatabase()
    }

    private fun inputToDatabase() {
        mCodeData = "${mCodeProduct}-${String.format("%08d", mBarcode)}"

        val data = ProductData()
        data.brand = mBrandName

        db.collection("barcode")
            .document(mCodeData.toString())
            .set(data)
            .addOnSuccessListener {
                if (mCount >= mAmount.toLong()) {
                    Toast.makeText(this, "Data Berhasil Disimpan", Toast.LENGTH_LONG).show()
                    hideProgress()
                } else {
                    mBarcode++
                    mCount++
                    inputToDatabase()
                }
            }.addOnFailureListener {
                hideProgress()
                Toast.makeText(
                    this,
                    "Terjadi Kesalahan, Silakan Coba Lagi",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showProgress() {
        btn_save.showProgress { progressColor = Color.WHITE }
        tie_brand.isEnabled = false
        tie_code_product.isEnabled = false
        tie_barcode.isEnabled = false
        tie_amount.isEnabled = false
        btn_save.isEnabled = false
    }

    private fun hideProgress() {
        btn_save.hideProgress(R.string.btn_save)
        tie_brand.isEnabled = true
        tie_code_product.isEnabled = true
        tie_barcode.isEnabled = true
        tie_amount.isEnabled = true
        btn_save.isEnabled = true
    }

    private fun prepare() {
        bindProgressButton(btn_save)
        btn_save.attachTextChangeAnimator()
        db = FirebaseFirestore.getInstance()
    }
}