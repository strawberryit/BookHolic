package pe.andy.bookholic.util

class TextSlicer(text: String, regexp: String) {

    private var slices: MutableList<String> = mutableListOf()

    init {
        slices = text.trim()
                    .split(Regex(regexp))
                    .toMutableList()
    }

    fun get(index: Int): String {
        return when (this.slices.size > index && index >= 0) {
            true -> slices[index].trim()
            false -> ""
        }
    }

    fun pop(): String {
        return when (this.slices.size > 0) {
            true -> this.slices.removeAt(0).trim()
            false -> ""
        }
    }
}
