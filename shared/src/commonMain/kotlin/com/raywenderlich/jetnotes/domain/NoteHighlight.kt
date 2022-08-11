enum class NoteHighlight(val colorVal: Int){
    GREEN(0x006837), BLUE(0x3584e4), RED(0xc01c28),
    BLACK(0x000000);

    fun toargbHex(): String{
        val hexStr = Integer.toHexString(colorVal)
        return "0xFF" + hexStr.uppercase()
    }

    companion object{
        fun highlightColorFrom(color: Int): NoteHighlight{
            return when(color){
                GREEN.colorVal -> GREEN
                BLUE.colorVal -> BLUE
                RED.colorVal -> RED
                else -> BLACK
            }
        }

        fun intValFrom(color: NoteHighlight): Int{
            return color.colorVal
        }
    }

}

object NoteColors{
    val arr: Array<Int> = Array(NoteHighlight.values().size){
        i -> NoteHighlight.values()[i].colorVal
    }
}

fun argbValueRgb(s: String): Int{
    return s.drop(4).toInt(16)
}

