package com.example.progra32p.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {
    @Query("SELECT * FROM mascotas ORDER BY orden ASC")
    fun observarTodas(): Flow<List<MascotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(mascotas: List<MascotaEntity>)

    @Query("DELETE FROM mascotas")
    suspend fun borrarTodo()

    @Transaction
    suspend fun reemplazarTodo(mascotas: List<MascotaEntity>) {
        borrarTodo()
        insertarTodas(mascotas)
    }
}
