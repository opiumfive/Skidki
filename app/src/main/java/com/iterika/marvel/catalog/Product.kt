package com.iterika.marvel.catalog

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String?,
    val name: String?,
    val picts: List<String>?,
    val discount: String?,
    val category: String?,
    val retails: List<CatalogRetail>?,
    var count: Int = 1

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(CatalogRetail),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeStringList(picts)
        parcel.writeString(discount)
        parcel.writeString(category)
        parcel.writeTypedList(retails)
        parcel.writeInt(count)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}

data class CatalogRetail(val retail: String?, val discount: String?, val expires: String?, val logo:String?) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(retail)
        parcel.writeString(discount)
        parcel.writeString(expires)
        parcel.writeString(logo)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<CatalogRetail> {
        override fun createFromParcel(parcel: Parcel): CatalogRetail {
            return CatalogRetail(parcel)
        }

        override fun newArray(size: Int): Array<CatalogRetail?> {
            return arrayOfNulls(size)
        }
    }
}