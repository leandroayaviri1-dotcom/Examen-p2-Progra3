package com.example.progra32p.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PaseoDao {
    // Flow: Room vuelve a emitir la lista cada vez que la tabla cambia.
    @Query("SELECT * FROM paseos ORDER BY raza ASC")
    fun observarTodos(): Flow<List<PaseoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(paseos: List<PaseoEntity>)

    @Query("DELETE FROM paseos")
    suspend fun borrarTodo()

    // Todo o nada: la pantalla nunca ve la tabla vacía a medio actualizar.
    @Transaction
    suspend fun reemplazarTodo(paseos: List<PaseoEntity>) {
        borrarTodo()
        insertarTodos(paseos)
    }
}
