package xyz.minecat

enum class Opcode {
    LOGIN,  // 0
    MESSAGE;  // 1

    companion object {
        private val VALUES = values()
        fun from(ordinal: Int) = VALUES.firstOrNull { it.ordinal == ordinal }
    }
}
