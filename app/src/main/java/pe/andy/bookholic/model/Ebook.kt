package pe.andy.bookholic.model

data class Ebook(var libraryName: String) : Comparable<Ebook> {
    var seq: String = ""
    var title: String = ""
    var author: String = ""
    var publisher: String = ""
    var thumbnailUrl: String = ""
    var url: String = ""
    var countTotal = -1
    var countRent = -1
    var date: String = ""
    var platformClass: String = ""

    var platform: String = ""
        set(value) {
            platformClass = with(platform) {
                when {
                    contains("교보") -> "label-success"
                    contains("북큐브") -> "bg-orange"
                    contains("예스24") || contains("YES24") -> "label-primary"
                    contains("메키아") || contains("MEKIA") -> "bg-purple"
                    contains("알라딘") -> "label-danger"
                    else -> "label-danger"
                }
            }
            field = value
        }

    val isAvailable: Boolean
        get() = (countRent < countTotal)

    override fun compareTo(other: Ebook): Int {
        if (this == other || this.countTotal <= 0 || other.countTotal <= 0) {
            return 0
        }

        return when {
            this.isAvailable && !other.isAvailable -> -1
            !this.isAvailable && other.isAvailable -> 1
            else -> 0
        }
    }
}