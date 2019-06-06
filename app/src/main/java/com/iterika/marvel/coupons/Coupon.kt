package com.iterika.marvel.coupons

import android.os.Parcel
import android.os.Parcelable

data class Coupon(
    val id: String? = null,
    val productNames: List<ProductName>? = null,
    val retailLogo: String? = null,
    val retailId: String? = null,
    val date: String? = null,
    val status: String? = null,
    val itemsCount: Int? = null,
    val discount: String? = null,
    val barcode: String? = null,
    val images: List<String>? = null,
    var shouldGoToCoupons: Boolean = false
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(ProductName),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readByte().compareTo(1) == 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeTypedList(productNames)
        parcel.writeString(retailLogo)
        parcel.writeString(retailId)
        parcel.writeString(date)
        parcel.writeString(status)
        parcel.writeValue(itemsCount)
        parcel.writeString(discount)
        parcel.writeString(barcode)
        parcel.writeStringList(images)
        parcel.writeByte(if (shouldGoToCoupons) 1.toByte() else 0.toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coupon> {
        override fun createFromParcel(parcel: Parcel): Coupon {
            return Coupon(parcel)
        }

        override fun newArray(size: Int): Array<Coupon?> {
            return arrayOfNulls(size)
        }
    }
}

data class ProductName(val name: String?, val count: String? = "", val sale: String? = ""): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(count)
        parcel.writeString(sale)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductName> {
        override fun createFromParcel(parcel: Parcel): ProductName {
            return ProductName(parcel)
        }

        override fun newArray(size: Int): Array<ProductName?> {
            return arrayOfNulls(size)
        }
    }
}