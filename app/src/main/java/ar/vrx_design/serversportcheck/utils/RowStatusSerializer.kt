package ar.vrx_design.serversportcheck.utils

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class RowStatusData(
    val host: String,
    val port: String
)

object RowStatusSerializer : Serializer<List<RowStatusData>> {
    override val defaultValue: List<RowStatusData> = emptyList()

    override suspend fun readFrom(input: InputStream): List<RowStatusData> {
        return try {
            val bytes = input.readBytes()
            if (bytes.isEmpty()) {
                emptyList()
            } else {
                Json.decodeFromString(
                    //ListSerializer(RowStatusData.serializer()),
                    bytes.decodeToString()
                )
            }
        } catch (e: Exception) {
            throw CorruptionException("No se pudo leer DataStore", e)
        }
    }

    override suspend fun writeTo(t: List<RowStatusData>, output: OutputStream) {
        /*val json = Json.encodeToString(
            ListSerializer(RowStatusData.serializer()),
            t
        )*/
        //output.write(json.encodeToByteArray())
    }
}
