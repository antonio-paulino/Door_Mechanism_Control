import java.io.Serial


object DoorMechanism { // Controla o estado do mecanismo de abertura da porta.

    const val OPENCMD = 0b10000
    const val CLOSECMD = 0b00000

    var busy = false

    // Inicia a classe, estabelecendo os valores iniciais.
    fun init() {
        busy = false
    }

    // Envia comando para abrir a porta, com o parâmetro de velocidade
    fun open(velocity: Int) = SerialEmitter.send(SerialEmitter.Destination.DOOR, OPENCMD or velocity)


    // Envia comando para fechar a porta, com o parâmetro de velocidade
    fun close(velocity: Int) = SerialEmitter.send(SerialEmitter.Destination.DOOR, CLOSECMD or velocity)

    // Verifica se o comando anterior está concluído
    fun finished(): Boolean {
        busy = SerialEmitter.isBusy()
        return !busy
    }

}