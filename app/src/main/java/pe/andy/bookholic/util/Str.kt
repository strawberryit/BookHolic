package pe.andy.bookholic.util


class Str {
    companion object {
        @JvmStatic
        fun def(text: String?): String {
            return text ?: ""
        }

        private val nonDigitPattern: Regex = Regex("\\D*")

        @JvmStatic
        fun extractInt(str: String = "0"): Int {
            return try {
                Integer.parseInt(str.replace(nonDigitPattern, ""))
            } catch (e: Exception) {
                0
            }
        }
    }
}