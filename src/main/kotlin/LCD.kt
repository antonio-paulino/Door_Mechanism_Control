object LCD {

    const val LINES = 2

    const val COLS = 16

    private const val RSBITMASK = 0b00010000

    private const val ENMASK = 0b00100000

    private const val DATAMASK = 0b00001111

    private const val PULSEDELAY = 500

    private const val RISEDELAY = 100

    private const val INITDELAY = 30

    private const val CMDDELAY = 2

    private const val CMDNIBBLE = 0b00000

    private const val DATANIBBLE = 0b10000

    private fun pulse() {
        waitTimeNano(PULSEDELAY)
        HAL.setBits(ENMASK)
        waitTimeNano(PULSEDELAY)
        HAL.clrBits(ENMASK)
        waitTimeNano(PULSEDELAY)
    }

    private fun writeNibbleParallel(rs: Boolean, data: Int) {
        if (!rs) {
            HAL.clrBits(RSBITMASK)
            waitTimeNano(RISEDELAY)
        } else {
            HAL.setBits(RSBITMASK)
            waitTimeNano(RISEDELAY)
        }
        HAL.writeBits(DATAMASK, data)
        pulse()
    }

    private fun writeNibbleSerial(rs: Boolean, data: Int) {
        val datasend = if (rs) DATANIBBLE or data else CMDNIBBLE or data
        SerialEmitter.send(SerialEmitter.Destination.LCD, datasend)
    }

    private fun writeNibble(rs: Boolean, data: Int) {
        // if (HAL.isBit(SERIALMASK)) {
        //     writeNibbleSerial(rs, data)h
        // } else {
        //    writeNibbleParallel(rs, data)
        // }
        writeNibbleSerial(rs, data)
    }

    private fun writeByte(rs: Boolean, data: Int) {
        val dataHigh = data.shr(4)
        val dataLow = data.and(15)
        writeNibble(rs, dataHigh)
        writeNibble(rs, dataLow)
    }

    private fun writeCMD(data: Int) {
        writeByte(false, data)
        waitTimeMilli(CMDDELAY)
    }

    private fun writeDATA(data: Int) {
        writeByte(true, data)
    }


    fun init() {
        waitTimeMilli(INITDELAY)
        writeNibble(false, 0b00000011)
        waitTimeMilli(INITDELAY)
        writeNibble(false, 0b00000011)
        waitTimeMilli(INITDELAY)
        writeNibble(false, 0b00000011)
        waitTimeMilli(INITDELAY)
        writeNibble(false, 0b00000010)

        writeCMD(0b00101000) // function set: 4-bit mode, 2 lines, 5x8 dots
        writeCMD(0b00001000) // display control : display off, cursor off, blink off
        writeCMD(0b00000001) // clear
        writeCMD(0b00000110) // entry mode set: increment cursor, no display shift


        writeCMD(0b00001100) // display control: display on, cursor off, blink off
    }

    fun write(c: Char) = writeDATA(c.code)

    fun write(text: String) {
        for (char in text) {
            write(char)
        }
    }

    fun cursor(line: Int, column: Int) = writeCMD(0b10000000 or (((line - 1) shl (6) and 0b01000000) + column - 1))
    fun clear() = writeCMD(1) // clear display
}