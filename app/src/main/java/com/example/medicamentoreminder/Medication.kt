import android.os.Parcel
import android.os.Parcelable

data class Medication(
    val name: String,
    val quantity: String,
    val interval: String,
    val intervalInMinutes: Int,
    val medicationIndex: Int,
    val uniqueID : Int,
    val daysDuration : Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(quantity)
        parcel.writeString(interval)
        parcel.writeInt(intervalInMinutes)
        parcel.writeInt(medicationIndex)
        parcel.writeInt(uniqueID)
        parcel.writeInt(daysDuration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Medication> {
        override fun createFromParcel(parcel: Parcel): Medication {
            return Medication(parcel)
        }

        override fun newArray(size: Int): Array<Medication?> {
            return arrayOfNulls(size)
        }
    }
}